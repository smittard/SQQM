# SQQM — Sarah What's for Dinner? (Java)

Original Java version of the SQQM project.

## Overview

SQQM is a Spring Boot command-line application that randomly generates a weekly menu from a recipe database, avoiding repetition across weeks, then outputs the result as a JSON file.

## Features

- **Random menu generation**: draws recipes by category to build a balanced week (dinners + lunches)
- **Anti-repetition**: uses a working list (`recipesWork.json`) to avoid serving the same dish twice in a row across weeks — the list resets automatically when exhausted
- **Category filtering**: recipe types can be excluded via configuration (e.g. `SOUPE`, `NONE`)
- **Configurable counts**: number of dinners and lunches is configurable
- **JSON output**: the generated menu is saved to `menu_semaine.json`

## Structure

```
java/
├── src/
│   └── main/
│       ├── java/org/mitmit/
│       │   ├── SarahQuestceQuonMange.java       # entry point
│       │   ├── configuration/
│       │   │   └── SqqmProperties.java          # configuration properties
│       │   ├── controller/
│       │   │   └── MenuCommandLineRunner.java   # CLI runner
│       │   ├── model/
│       │   │   ├── Recipe.java                  # recipe model
│       │   │   └── RecipeType.java              # recipe type enum
│       │   └── service/
│       │       └── RecipeService.java           # menu generation logic
│       └── resources/
│           └── banner.txt
├── recipes.json        # recipe database
├── application.yml     # Spring Boot configuration
└── pom.xml
```

## Requirements

- Java 8+
- Maven 3+

## Build & Run

```bash
# Build
mvn clean package

# Run
java -jar target/SQQM-*.jar
```

The application reads `recipes.json` from the working directory and writes `menu_semaine.json` on each run.

## Configuration (`application.yml`)

```yaml
sqqm:
  exclude-recipe-type:   # recipe types to exclude from generation
    - NONE
    - SOUPE
  dinner-number: 6       # number of dinners to generate (default: 6)
  lunch-number: 2        # number of lunches to generate (default: 2)
```

## Recipe types

| Type | Description |
|---|---|
| `MIDI` | Lunch dish |
| `PATES_RIZ` | Pasta / rice |
| `SALADE` | Salad |
| `SOUPE` | Soup |
| `TARTE_QUICHE_CAKE` | Tart / quiche / savoury cake |
| `GRATIN_CROQUE_OEUFS` | Gratin / croque / egg dish |
| `NONE` | Uncategorised |

## Output files

| File | Description |
|---|---|
| `menu_semaine.json` | Generated menu for the current week |
| `recipesWork.json` | Remaining recipes (anti-repetition working list) |
