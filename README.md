# Kleros

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-2026.02.01-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/API-24%2B-4CAF50?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/studio)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![Build](https://img.shields.io/badge/Build-passing-4caf50?style=flat-square)](https://github.com/Django0033/kleros_app/tree/main)

Toolkit for tabletop roleplaying sessions — dice rolling, random name generation, scene inspiration, NPC, creature, adventure, location, and mystery creation, plus a Fate oracle. Built with Kotlin and Jetpack Compose for Android.

## Features

### Dice Roll

Roll dice from D4 to D100 with animated results and rolling history.

- **7 dice types**: D4, D6, D8, D10, D12, D20, D100
- **Smooth animations**: Result value transitions with spring physics and a scale pulse on each roll
- **Roll history**: Last 10 results, newest first

### Name Generator

Generate fantasy names using a table-driven syllable composition system.

- **3 roll modes**: Normal (1d20), Advantage (max 2d20 — feminine skew), Disadvantage (min 2d20 — masculine skew)
- **20 name patterns**: Each row has a unique syllable pattern (e.g. `12o`, `123-a`, `111`) that determines column selection
- **Syllable composition**: Each digit in the pattern maps to an independent 1d20 roll on Syllable 1, Syllable 2, or Suffix columns
- **Parenthetical prefixes**: Entries like `(f)a` resolve to `fa` or `a` depending on syllable position
- **Generation history**: Last 10 generated names preserved per session

> [!NOTE]
> The name generator reuses `DiceRoller` from the dice feature for all random rolls, keeping the random logic in one place.

### Meaning Tables

Roll on 1d100 word tables for scene inspiration — action and description word pairs.

- **Action table**: 50 words (Attain, Betray, Create, Fight, Help, Surprise, ...)
- **Description table**: 50 words (Beautiful, Dangerous, Mysterious, Powerful, Strange, ...)
- **2-point ranges**: 1-2, 3-4, ..., 99-100 for fine-grained results
- **Cross-table rolling**: Switch between Action and Description with a tap

### Character Crafter

Generate quick NPCs with descriptors and statistics — four independent rolls on a shared word pool.

- **4 descriptor slots**: Identity, Mind, Body, Talent — each rolled independently on a 100-word pool
- **Statistics tier**: 1d10 range table (50% lower to 50% higher)
- **Single tap generation**: One button rolls all descriptors and stats at once
- **Character card**: Formatted result showing all 4 slots and statistics tier
- **Generation history**: Last 10 NPCs preserved per session

### Creature Crafter

Generate creatures with descriptors, abilities, behaviors, and statistics — roll individually or all at once.

- **Descriptors table**: ~100 weighted entries (1d100) for appearance and traits
- **Abilities table**: 50 paired entries (1d100) for special powers and features
- **Behavior tables**: Initial behavior (1d10) and New behavior (1d10) for encounter dynamics
- **Statistics**: Reuses the same tier table as Character Crafter (1d10)
- **Incremental rolling**: Roll descriptors, abilities, initial behavior, new behavior, and statistics independently — no need to regenerate everything
- **Generation history**: Last 10 creatures preserved per session

### Adventure Crafter

Roll on plot theme tables for story inspiration — pick a theme or go random.

- **5 plot themes**: Action, Tension, Mystery, Social, Personal — each with 100 entries (1d100)
- **Manual selection**: Pick a theme from FilterChips and roll
- **Random Theme**: One tap picks a random theme and rolls automatically
- **Roll history**: Last 10 plot words preserved per session

### Fate Oracle

Ask Yes/No questions with 9 odds levels and random events on doubles.

- **9 odds levels**: Impossible → Nearly Impossible → Very Unlikely → Unlikely → 50/50 → Likely → Very Likely → Nearly Certain → Certain
- **4 outcome types**: Exceptional Yes, Yes, No, Exceptional No — each with probability-weighted ranges
- **Random events**: Doubles (11, 22, ..., 99, 100) trigger a random event indicator
- **Quick interaction**: Pick odds, tap Ask Oracle, see the result
- **Roll history**: Last 10 oracle answers preserved per session

### Mystery Crafter

Investigate mysteries with progressive discovery checks and accumulated boxes.

- **7 discovery tiers**: From "Nothing useful found" to "Definitive clue" — rolled as 1d100 + accumulated boxes
- **Boxes accumulator**: Advances only on meaningful discoveries (new suspects, new clues, definitive answer)
- **100 mystery descriptors**: (1d100) for flavor — Accident, Betray, Hidden, Motive, Witness, ...
- **Progressive difficulty**: Higher boxes shift results toward connections and definitive answers
- **Roll history**: Last 10 discovery results preserved per session

## Screens

Navigation uses a Material3 drawer — tap the hamburger icon or swipe from the left edge to choose between 9 tools.

| Screen | Icon | Description |
|--------|------|-------------|
| **Dice Roll** | Casino | Select a dice type, roll, and see animated results with history |
| **Name Gen** | Badge | Choose roll mode, generate fantasy names, and browse history |
| **Meaning** | Psychology | Roll on Action or Description word tables for scene inspiration |
| **Char Craft** | Face | Generate NPCs with descriptors and statistics |
| **Creature** | BugReport | Generate creatures with descriptors, abilities, and behaviors |
| **Adv Craft** | Star | Roll on plot theme tables for story inspiration |
| **Fate** | Visibility | Ask the oracle with 9 odds levels |
| **Loc Craft** | Map | Explore procedural locations with PP progression |
| **Mystery** | Search | Investigate mysteries with progressive discovery checks |

## Architecture

The app follows a simple single-module structure with composable-local state management — no ViewModels, DI, or navigation library. Screen switching uses a Material3 `ModalNavigationDrawer` with a `TopAppBar` and hamburger icon. Shared infrastructure is extracted into reusable components: `TableRoller` (generic table lookup engine) and `TableScreen` (reusable composable with table selector, roll button, result display, and history).

```
app/src/main/java/com/kleros/
├── MainActivity.kt           # Activity + ModalNavigationDrawer nav
├── table/                     # Shared infrastructure
│   ├── TableEntry.kt          # Sealed class: RANGE, DIRECT, RANGE_MODIFIER
│   ├── TableDef.kt            # Table definition (name + entries)
│   ├── TableRoller.kt         # Generic table lookup engine
│   ├── TableRollResult.kt     # Sealed: Success, Error
│   ├── TableHistory.kt        # Immutable capped history
│   └── TableScreen.kt         # Reusable composable
├── dice/                      # Dice roll feature
│   ├── DiceType.kt            # Enum: D4–D100 with face count
│   ├── DiceRollResult.kt      # Result data class
│   ├── DiceRoller.kt          # Pure random roll function
│   ├── RollHistory.kt         # Immutable capped history
│   └── DiceScreen.kt          # Dice roll composable
├── namegenerator/             # Name generator feature
│   ├── RollMode.kt            # NORMAL / ADVANTAGE / DISADVANTAGE
│   ├── NameResult.kt          # Generated name data class
│   ├── NameHistory.kt         # Immutable capped history
│   ├── NameTable.kt           # 20-row syllable table
│   ├── NameGenerator.kt       # Pattern parser + generation engine
│   └── NameScreen.kt          # Name generator composable
├── meaning/                   # Meaning tables feature
│   ├── MeaningData.kt         # 100 word entries (50 action + 50 description)
│   └── MeaningScreen.kt       # TableScreen wrapper with D100
├── character/                 # Character Crafter feature
│   ├── CharacterData.kt       # 100 descriptor + 5 statistics entries
│   ├── CharacterCrafter.kt    # NPC generation engine
│   ├── CharacterResult.kt     # Data class with 4 slots + stat
│   ├── CharacterHistory.kt    # Immutable capped history
│   └── CharacterScreen.kt     # NPC generation composable
├── creature/                  # Creature Crafter feature
│   ├── CreatureData.kt        # 100 descriptors + 50 abilities + behavior tables
│   ├── CreatureCrafter.kt     # Creature generation + incremental mutation methods
│   ├── CreatureResult.kt      # Data class with lists for descriptors/abilities
│   ├── CreatureHistory.kt     # Immutable capped history
│   └── CreatureScreen.kt      # Creature generation composable
├── adventure/                 # Adventure Crafter feature
│   ├── AdventureData.kt       # 5 plot tables × 100 DIRECT entries each
│   └── AdventureScreen.kt     # Custom screen with theme selector + random button
├── fate/                      # Fate Oracle feature
│   ├── FateData.kt            # OddsLevel enum with 9 probability thresholds
│   ├── FateRoller.kt          # 1d100 fate chart resolution engine
│   ├── FateResult.kt          # Sealed class: ExceptionalYes, Yes, No, ExceptionalNo
│   ├── FateHistory.kt         # Immutable capped history
│   └── FateScreen.kt          # Oracle screen with odds selector + random event
├── mystery/                   # Mystery Crafter feature
│   ├── MysteryData.kt         # 7 discovery RANGE + 100 descriptors DIRECT
│   ├── MysteryCrafter.kt      # Accumulator check + descriptor roll
│   ├── MysteryResult.kt       # discoveryResult, descriptor, boxes, isDefinitive
│   ├── MysteryHistory.kt      # Immutable capped history
│   └── MysteryScreen.kt       # Mystery screen with boxes counter
└── ui/theme/                  # Material3 theming
    ├── Color.kt
    ├── Theme.kt               # KlerosTheme with dynamic color support
    └── Type.kt
```

### Key design decisions

- **Pure functions**: `DiceRoller.roll()`, `NameGenerator.generate()`, and `TableRoller.roll()` are pure Kotlin with no Android dependencies — trivially testable
- **Reusable engine**: `TableRoller` handles 3 entry types (RANGE, DIRECT, RANGE_MODIFIER) and serves as the foundation for all table-driven features
- **Two screen patterns**: `TableScreen` for single-table-at-a-time features (Meaning Tables); custom composables with multi-roll results for richer features (Name Generator, Character Crafter, Creature Crafter, Adventure Crafter, Fate Oracle)
- **Incremental mutation**: Creature Crafter supports rolling individual elements (descriptors, abilities, behaviors, statistics) independently after the initial generation — each mutation returns a new copy preserving immutability
- **Injectable randomness**: All generators accept a `rollFn: (DiceType) -> Int` parameter, defaulting to `DiceRoller.roll()`. Tests inject deterministic lambdas for predictable results
- **Drawer navigation**: Material3 `ModalNavigationDrawer` with `TopAppBar` replaces FilterChip nav — scales to any number of screens without layout changes
- **Immutable state**: All history types return new instances on append — no mutation
- **Composable-local state**: `remember { mutableStateOf(...) }` for screen state — no ViewModel overhead for this scope

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 17
- An Android device or emulator (API 24+)

### Build & Run

```bash
# Clone the repository
git clone https://github.com/Django0033/kleros_app.git
cd kleros_app

# Build and install on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Full verification (tests + lint + static analysis + coverage)
./gradlew check
```

## Testing

| Layer | Tool | Location |
|-------|------|----------|
| Unit | JUnit 4.13.2 | `src/test/` |
| UI (Compose) | Compose UI Test 4 | `src/androidTest/` |
| Coverage | JaCoCo 0.8.12 | `./gradlew jacocoTestReport` |

```bash
# All unit tests
./gradlew test

# Compose UI tests (requires emulator or device)
./gradlew connectedDebugAndroidTest

# View coverage report
./gradlew jacocoTestReport
# Open: app/build/reports/jacoco/jacocoTestReport/html/index.html
```

> [!TIP]
> Pre-commit hooks run `ktlintCheck` and `detekt` automatically. Bypass with `SKIP_CHECKS=1 git commit`.

## Code Quality

| Tool | Purpose | Command |
|------|---------|---------|
| [ktlint](https://github.com/pinterest/ktlint) | Kotlin formatting | `./gradlew ktlintFormat` |
| [detekt](https://detekt.dev) | Static analysis | `./gradlew detekt` |
| Android Lint | Android-specific checks | Built into `./gradlew check` |

## Tech Stack

- **Language**: Kotlin 2.2.10
- **UI**: Jetpack Compose with Material3
- **Build**: Gradle 9.4.1 / AGP 9.2.1
- **Min SDK**: 24 / Target: 36
- **Theme**: KlerosTheme with dynamic color (API 31+) and purple fallback palette
- **Testing**: JUnit 4, Compose UI Test, Espresso, JaCoCo
- **Quality**: detekt, ktlint, Android Lint
