# SQQM — Sarah Qu'est-ce Qu'on Mange ?

SQQM est un outil de planification des repas de la semaine. Il génère un menu de saison et la liste de courses associée, triée par rayon de supermarché.

## Versions

| Version | Langage | Description |
|---|---|---|
| [`java/`](java/) | Java | Version originale |
| [`python/`](python/) | Python | Réécriture Python — [voir le README](python/README.md) |

## Fonctionnement général

1. L'utilisateur configure le planning des repas de la semaine
2. L'outil tire aléatoirement des recettes de saison pour chaque repas
3. Les menus peuvent être validés ou re-tirés interactivement
4. La liste de courses est générée, fusionnée et triée par rayon
5. Un export en fichier texte est proposé en fin de session
