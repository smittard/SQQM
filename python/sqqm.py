#!/usr/bin/env python3
"""
SQQM - Sarah Qu'est-ce Qu'on Mange
Génère un menu de saison et une liste de courses pour la semaine.
"""

import json
import os
import sys
import random
import datetime
from collections import defaultdict
from copy import deepcopy

# ─── Chemins ──────────────────────────────────────────────────────────────────

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
RECETTES_PATH = os.path.join(SCRIPT_DIR, "recettes.json")

# ─── Ordre des rayons pour le tri ─────────────────────────────────────────────

ORDRE_RAYONS = [
    "fruits et légumes",
    "boucherie",
    "poissonnerie",
    "crèmerie",
    "boulangerie",
    "épicerie",
    "surgelés",
    "boissons",
    "autre",
]

# ─── Planning par défaut ───────────────────────────────────────────────────────

# Format : (jour_label, categorie)
PLANNING_DEFAUT = [
    ("Samedi midi",    "midi"),
    ("Samedi soir",    "soir"),
    ("Dimanche midi",  "midi"),
    ("Dimanche soir",  "soir"),
    ("Lundi soir",     "soir"),
    ("Mardi soir",     "soir"),
    ("Mercredi soir",  "soir"),
    ("Jeudi soir",     "soir"),
]

# ─── Chargement des recettes ───────────────────────────────────────────────────

def charger_recettes():
    if not os.path.exists(RECETTES_PATH):
        print(f"[ERREUR] Fichier recettes introuvable : {RECETTES_PATH}")
        sys.exit(1)
    with open(RECETTES_PATH, "r", encoding="utf-8") as f:
        data = json.load(f)
    return data["recettes"]

def recettes_de_saison(recettes, mois, categorie):
    """Retourne les recettes de saison pour un mois et une catégorie donnés."""
    return [r for r in recettes if mois in r["mois_saison"] and r["categorie"] == categorie]

# ─── Affichage ─────────────────────────────────────────────────────────────────

def titre(texte):
    largeur = 60
    print()
    print("=" * largeur)
    print(f"  {texte}")
    print("=" * largeur)

def sous_titre(texte):
    print(f"\n--- {texte} ---")

def afficher_planning(planning):
    """Affiche le planning actuel (liste de (label, categorie))."""
    sous_titre("Planning des repas")
    for i, (label, cat) in enumerate(planning, 1):
        print(f"  {i}. {label} ({cat})")

# ─── Saisie utilisateur ────────────────────────────────────────────────────────

def saisir_oui_non(question, defaut="o"):
    """Pose une question oui/non, retourne True si oui."""
    hint = "[O/n]" if defaut == "o" else "[o/N]"
    while True:
        rep = input(f"{question} {hint} : ").strip().lower()
        if rep == "":
            return defaut == "o"
        if rep in ("o", "oui", "y", "yes"):
            return True
        if rep in ("n", "non", "no"):
            return False
        print("  Répondez par o (oui) ou n (non).")

def saisir_entier(message, mini, maxi):
    """Saisit un entier entre mini et maxi."""
    while True:
        try:
            val = int(input(message).strip())
            if mini <= val <= maxi:
                return val
            print(f"  Entrez un nombre entre {mini} et {maxi}.")
        except ValueError:
            print("  Nombre invalide.")

def saisir_liste_entiers(message, mini, maxi):
    """Saisit une liste d'entiers séparés par des espaces/virgules."""
    while True:
        rep = input(message).strip()
        if rep == "":
            return []
        try:
            nums = [int(x) for x in rep.replace(",", " ").split()]
            if all(mini <= n <= maxi for n in nums):
                return list(dict.fromkeys(nums))  # dédoublonnage ordre préservé
            print(f"  Tous les numéros doivent être entre {mini} et {maxi}.")
        except ValueError:
            print("  Entrez des numéros séparés par des espaces ou virgules.")

# ─── Étape 1 : configurer le planning ─────────────────────────────────────────

def configurer_planning():
    """Permet à l'utilisateur de modifier le planning par défaut."""
    titre("SQQM - Sarah Qu'est-ce Qu'on Mange")
    print(f"\nMois courant : {datetime.date.today().strftime('%B %Y')}")

    planning = list(PLANNING_DEFAUT)
    afficher_planning(planning)

    if not saisir_oui_non("\nVoulez-vous modifier le planning des repas ?", defaut="n"):
        return planning

    # Suppression de repas
    sous_titre("Suppression de repas")
    print("  Entrez les numéros des repas à SUPPRIMER (séparés par espaces), ou Entrée pour ne rien supprimer :")
    a_supprimer = saisir_liste_entiers("  > ", 1, len(planning))
    if a_supprimer:
        planning = [r for i, r in enumerate(planning, 1) if i not in a_supprimer]
        print(f"  {len(a_supprimer)} repas supprimé(s).")

    # Ajout de repas
    if saisir_oui_non("\nVoulez-vous ajouter des repas ?", defaut="n"):
        JOURS = ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"]
        MOMENTS = ["midi", "soir"]
        while True:
            print("\n  Jours disponibles :")
            for i, j in enumerate(JOURS, 1):
                print(f"    {i}. {j}")
            jour_idx = saisir_entier("  Numéro du jour : ", 1, len(JOURS)) - 1
            print("  Catégorie : 1. midi  2. soir")
            cat_idx = saisir_entier("  Numéro de la catégorie : ", 1, 2) - 1
            label = f"{JOURS[jour_idx]} {MOMENTS[cat_idx]}"
            planning.append((label, MOMENTS[cat_idx]))
            print(f"  Ajouté : {label}")
            if not saisir_oui_non("  Ajouter un autre repas ?", defaut="n"):
                break

    print()
    afficher_planning(planning)
    return planning

# ─── Étape 2 : générer et valider les menus ───────────────────────────────────

def tirer_recette(recettes_dispo, deja_choisies):
    """Tire une recette au hasard parmi celles disponibles, en évitant les doublons si possible."""
    candidates = [r for r in recettes_dispo if r["id"] not in deja_choisies]
    if not candidates:
        candidates = recettes_dispo  # on accepte les doublons si pas d'autre choix
    if not candidates:
        return None
    return random.choice(candidates)

def generer_menus(planning, recettes, mois):
    """Génère un menu aléatoire de saison pour chaque repas du planning."""
    menus = []
    deja_choisies = set()
    for label, cat in planning:
        dispo = recettes_de_saison(recettes, mois, cat)
        if not dispo:
            menus.append((label, cat, None))
            continue
        recette = tirer_recette(dispo, deja_choisies)
        deja_choisies.add(recette["id"])
        menus.append((label, cat, recette))
    return menus

def afficher_menus(menus):
    sous_titre("Menus proposés")
    for i, (label, cat, recette) in enumerate(menus, 1):
        titre_recette = recette["titre"] if recette else "-- aucune recette de saison --"
        print(f"  {i:2}. {label:<20}  {titre_recette}")

def valider_menus(menus, recettes, mois):
    """Boucle de validation : l'utilisateur peut demander de nouvelles propositions."""
    while True:
        afficher_menus(menus)
        print()
        rep = input(
            "  Numéros des repas à re-tirer (séparés par espaces), ou Entrée pour valider : "
        ).strip()
        if rep == "":
            return menus

        try:
            indices = [int(x) - 1 for x in rep.replace(",", " ").split()]
        except ValueError:
            print("  Numéros invalides, réessayez.")
            continue

        if not all(0 <= i < len(menus) for i in indices):
            print(f"  Les numéros doivent être entre 1 et {len(menus)}.")
            continue

        deja_choisies = {m[2]["id"] for m in menus if m[2] is not None}
        for idx in indices:
            label, cat, ancienne = menus[idx]
            dispo = recettes_de_saison(recettes, mois, cat)
            if not dispo:
                print(f"  Aucune recette de saison disponible pour {label}.")
                continue
            # Exclure l'ancienne recette du tirage si possible
            exclus = deja_choisies - ({ancienne["id"]} if ancienne else set())
            nouvelle = tirer_recette(dispo, exclus)
            if ancienne:
                deja_choisies.discard(ancienne["id"])
            deja_choisies.add(nouvelle["id"])
            menus[idx] = (label, cat, nouvelle)

# ─── Étape 3 : générer la liste de courses ────────────────────────────────────

UNITES_NUMERABLES = {"pièce", "boîte", "rouleau", "cube", "litre", "bouquet", "feuille", "tranche", "morceau", "gousse"}

def fusionner_ingredients(menus):
    """
    Fusionne les ingrédients de tous les menus.
    Retourne un dict : nom_normalisé -> {nom, quantite, unite, rayon}
    Pour les unités identiques, cumule les quantités.
    Pour les unités différentes, crée des entrées séparées.
    """
    fusion = {}  # clé : (nom_normalisé, unite)

    for _, _, recette in menus:
        if recette is None:
            continue
        for ing in recette["ingredients"]:
            nom = ing["nom"].strip().lower()
            unite = ing["unite"].strip().lower()
            quantite = ing["quantite"]
            rayon = ing["rayon"]
            cle = (nom, unite)

            if cle in fusion:
                fusion[cle]["quantite"] += quantite
            else:
                fusion[cle] = {
                    "nom": ing["nom"],
                    "quantite": quantite,
                    "unite": unite,
                    "rayon": ing["rayon"],
                }

    return list(fusion.values())

def formater_quantite(quantite, unite):
    """Formate la quantité de façon lisible."""
    if isinstance(quantite, float) and quantite.is_integer():
        quantite = int(quantite)
    # Unités numérables : on arrondit à l'entier
    if unite in UNITES_NUMERABLES and isinstance(quantite, float):
        quantite = round(quantite)
    return f"{quantite} {unite}"

def trier_par_rayon(ingredients):
    """Trie les ingrédients par rayon selon ORDRE_RAYONS, puis par nom."""
    def cle_rayon(ing):
        rayon = ing["rayon"].strip().lower()
        try:
            return (ORDRE_RAYONS.index(rayon), ing["nom"].lower())
        except ValueError:
            return (len(ORDRE_RAYONS), ing["nom"].lower())
    return sorted(ingredients, key=cle_rayon)

def afficher_liste_courses(menus):
    titre("Liste de courses")

    ingredients = fusionner_ingredients(menus)
    ingredients_tries = trier_par_rayon(ingredients)

    rayon_courant = None
    for ing in ingredients_tries:
        rayon = ing["rayon"].strip().lower()
        if rayon != rayon_courant:
            rayon_courant = rayon
            print(f"\n  [ {ing['rayon'].upper()} ]")
        qte_str = formater_quantite(ing["quantite"], ing["unite"])
        print(f"    - {ing['nom']:<35} {qte_str}")

    return ingredients_tries

def exporter_liste_courses(menus, ingredients_tries, planning_valide):
    """Exporte la liste de courses dans un fichier texte."""
    date_str = datetime.date.today().strftime("%Y-%m-%d")
    nom_fichier = f"courses_{date_str}.txt"
    chemin = os.path.join(SCRIPT_DIR, nom_fichier)

    lignes = []
    lignes.append("=" * 60)
    lignes.append("  SQQM - Liste de courses")
    lignes.append(f"  Générée le {datetime.date.today().strftime('%d/%m/%Y')}")
    lignes.append("=" * 60)
    lignes.append("")

    lignes.append("MENUS DE LA SEMAINE")
    lignes.append("-" * 40)
    for label, cat, recette in menus:
        titre_r = recette["titre"] if recette else "-- aucune recette --"
        lignes.append(f"  {label:<20}  {titre_r}")

    lignes.append("")
    lignes.append("LISTE DE COURSES")
    lignes.append("-" * 40)

    rayon_courant = None
    for ing in ingredients_tries:
        rayon = ing["rayon"].strip().lower()
        if rayon != rayon_courant:
            rayon_courant = rayon
            lignes.append(f"\n  [ {ing['rayon'].upper()} ]")
        qte_str = formater_quantite(ing["quantite"], ing["unite"])
        lignes.append(f"    - {ing['nom']:<35} {qte_str}")

    lignes.append("")

    with open(chemin, "w", encoding="utf-8") as f:
        f.write("\n".join(lignes))

    print(f"\n  Fichier exporté : {chemin}")

# ─── Point d'entrée ────────────────────────────────────────────────────────────

def main():
    recettes = charger_recettes()
    mois = datetime.date.today().month

    # Étape 1 : planning
    planning = configurer_planning()
    if not planning:
        print("\nAucun repas dans le planning. Au revoir !")
        return

    # Étape 2 : menus
    titre("Génération des menus de saison")
    menus = generer_menus(planning, recettes, mois)
    menus = valider_menus(menus, recettes, mois)

    # Étape 3 : liste de courses
    ingredients_tries = afficher_liste_courses(menus)

    print()
    if saisir_oui_non("Exporter la liste de courses dans un fichier texte ?"):
        exporter_liste_courses(menus, ingredients_tries, planning)

    print("\nBonne semaine et bon appétit !\n")

if __name__ == "__main__":
    main()
