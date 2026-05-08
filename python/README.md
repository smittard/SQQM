# SQQM — Sarah Qu'est-ce Qu'on Mange (Python)

Version Python du projet SQQM. Cette version a été développée à l'aide de **Claude Sonnet** (Anthropic).

## Présentation

SQQM est un outil en ligne de commande qui génère automatiquement un menu de la semaine basé sur les recettes de saison, puis produit la liste de courses correspondante, triée par rayon de supermarché.

## Fonctionnalités

- **Planning configurable** : planning par défaut du samedi midi au jeudi soir, modifiable à la volée (ajout/suppression de repas)
- **Menus de saison** : les recettes sont filtrées selon le mois courant
- **Tirage aléatoire sans doublon** : évite de proposer deux fois le même plat dans la semaine
- **Validation interactive** : possibilité de re-tirer un ou plusieurs repas si la proposition ne convient pas
- **Liste de courses fusionnée** : les ingrédients identiques sont cumulés, la liste est triée par rayon (fruits & légumes, boucherie, poissonnerie, crèmerie, boulangerie, épicerie, surgelés, boissons)
- **Export fichier** : génère un fichier `courses_YYYY-MM-DD.txt` avec les menus et la liste de courses

## Structure

```
python/
├── sqqm.py          # script principal
├── recettes.json    # base de recettes (20 recettes, toutes saisons)
└── README.md
```

## Prérequis

- Python 3.7+
- Aucune dépendance externe (stdlib uniquement)

## Utilisation

```bash
python sqqm.py
```

Le script se déroule en trois étapes :

1. **Planning** — affiche le planning par défaut et propose de le modifier
2. **Menus** — tire aléatoirement des recettes de saison et permet de re-tirer les repas non souhaités
3. **Courses** — affiche la liste de courses triée par rayon et propose un export en fichier texte

## Format de `recettes.json`

```json
{
  "recettes": [
    {
      "id": 1,
      "titre": "Nom de la recette",
      "categorie": "midi",
      "mois_saison": [1, 2, 3, 10, 11, 12],
      "ingredients": [
        {
          "nom": "Nom de l'ingrédient",
          "quantite": 400,
          "unite": "g",
          "rayon": "épicerie"
        }
      ]
    }
  ]
}
```

| Champ | Valeurs possibles |
|---|---|
| `categorie` | `"midi"` ou `"soir"` |
| `mois_saison` | liste de mois (1 = janvier … 12 = décembre) |
| `rayon` | `"fruits et légumes"`, `"boucherie"`, `"poissonnerie"`, `"crèmerie"`, `"boulangerie"`, `"épicerie"`, `"surgelés"`, `"boissons"`, `"autre"` |

## Ajouter des recettes

Il suffit d'éditer `recettes.json` en respectant le format ci-dessus. Les nouveaux plats seront automatiquement pris en compte au prochain lancement.
