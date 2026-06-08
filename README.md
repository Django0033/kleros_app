# Kleros

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-2026.02.01-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/API-24%2B-4CAF50?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/studio)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![Build](https://img.shields.io/badge/Build-passing-4caf50?style=flat-square)](https://github.com/Django0033/kleros_app/tree/main)

Toolkit for tabletop roleplaying sessions — dice rolling and random name generation, built with Kotlin and Jetpack Compose for Android.

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

## Screens

The app has two screens toggled via `FilterChip` navigation:

| Screen | Description |
|--------|-------------|
| **Dice Roll** | Select a dice type, roll, and see animated results with history |
| **Name Gen** | Choose roll mode, generate fantasy names, and browse history |

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

## Architecture

The app follows a simple single-module structure with composable-local state management — no ViewModels, DI, or navigation library.

```
app/src/main/java/com/kleros/
├── MainActivity.kt           # Activity + Screen toggle navigation
├── dice/                      # Dice roll feature
│   ├── DiceType.kt            # Enum: D4–D100 with face count
│   ├── DiceRollResult.kt      # Result data class
│   ├── DiceRoller.kt          # Pure random roll function
│   ├── RollHistory.kt         # Immutable capped history (max 10)
│   └── DiceScreen.kt          # Dice roll composable
├── namegenerator/             # Name generator feature
│   ├── RollMode.kt            # NORMAL / ADVANTAGE / DISADVANTAGE
│   ├── NameResult.kt          # Generated name data class
│   ├── NameHistory.kt         # Immutable capped history (max 10)
│   ├── NameTable.kt           # 20-row syllable table
│   ├── NameGenerator.kt       # Pattern parser + generation engine
│   └── NameScreen.kt          # Name generator composable
└── ui/theme/                  # Material3 theming
    ├── Color.kt
    ├── Theme.kt               # KlerosTheme with dynamic color support
    └── Type.kt
```

### Key design decisions

- **Pure functions**: `DiceRoller.roll()` and `NameGenerator.generate()` are pure Kotlin with no Android dependencies — trivially testable
- **Reusable random engine**: Name generator calls `DiceRoller.roll(DiceType.D20)` instead of duplicating random logic
- **Immutable state**: History types (RollHistory, NameHistory) return new instances on append — no mutation
- **Composable-local state**: `remember { mutableStateOf(...) }` for screen state — no ViewModel overhead for this scope

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

# Full report
./gradlew jacocoTestReport
# Open: app/build/reports/jacoco/jacocoTestReport/html/index.html
```

> [!TIP]
> Pre-commit hooks are configured to run `ktlintCheck` and `detekt` automatically. Bypass with `SKIP_CHECKS=1 git commit`.

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
