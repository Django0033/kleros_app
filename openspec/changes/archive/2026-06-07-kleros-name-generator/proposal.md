# Proposal: Kleros Name Generator

## Intent

Kleros jurors create cases, parties, and evidence references that need thematic placeholder names (e.g., fantasy-medieval or sci-fi). Currently no in-app name generator exists — users must invent names externally. This change adds a 1d20-table-driven random name generator following the same patterns as DiceRoller: pure-function core, composable-local state, enum-based navigation toggle.

## Scope

### In Scope
- Static 20-row syllable table (Pattern + Inicio1 + Inicio2 + Ending) embedded as a data class
- Pure `NameGenerator.generate(): String` — 1d20 table roll → pattern parse → per-digit syllable rolls → prefix rule → concatenation
- `NameResult` data class wrapping generated name + display timestamp
- `NameHistory` (immutable, newest-first, cap 10) mirroring `RollHistory`
- `NameScreen` composable with Generate button, result display, history list
- Screen navigation toggle in `MainActivity` (enum + switch alongside DiceScreen)
- Strict TDD: unit tests for generation logic + UI tests for screen rendering

### Out of Scope
- Favorites/bookmarks for generated names
- Export/share generated names
- Custom syllable editing or table configuration
- Multiple name styles/locales (single table only)
- Animation on name generation
- History persistence across app restarts

## Capabilities

### New Capabilities
- `name-generator`: Random name generation via 1d20-table algorithm with pattern parsing, syllable concatenation, and parenthetical-prefix resolution

### Modified Capabilities
- `dice-roll`: MINOR — DiceScreen no longer the sole screen; `MainActivity` gets a screen toggle selector (enum-driven route)

## Approach

Flat package `com.kleros.namegenerator` mirroring `com.kleros.dice`. No ViewModel, no DI, no navigation library. A `Screen` enum in `MainActivity` (current + new name = `NameGenerator`) drives which composable renders.

### DiceRoller Integration
`NameGenerator` **reuses** `DiceRoller.roll(DiceType.D20)` instead of having its own random source. All dice rolls (initial pattern roll + per-digit syllable rolls) go through `DiceRoller`, which is already tested with 1000+ invocations per dice type.

### Advantage / Disadvantage
A `RollMode` enum (NORMAL, ADVANTAGE, DISADVANTAGE) controls the initial pattern-selection roll:
- **NORMAL**: 1d20
- **ADVANTAGE**: max(2d20) — sesgo hacia números altos → nombres más femeninos
- **DISADVANTAGE**: min(2d20) — sesgo hacia números bajos → nombres más masculinos

Only the pattern-selection roll is modified. All subsequent syllable rolls are normal 1d20.

### Algorithm
`NameGenerator.generate(rollMode, rollFn): String` — pure Kotlin, zero Android dependencies:
1. Roll pattern (with advantage/disadvantage if selected) → pick row → get Pattern
2. Parse Pattern into operations (column + range modifier) + suffix letters
3. For each operation, roll 1d20 on the corresponding column using `DiceRoller`
4. Apply parenthetical-prefix rule: `(f)a` → "a" if first syllable, "fa" otherwise
5. Concatenate syllables + append suffix letters

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `com.kleros.namegenerator/NameTable.kt` | New | 20-row static syllable table |
| `com.kleros.namegenerator/NameGenerator.kt` | New | Pure generation function |
| `com.kleros.namegenerator/NameResult.kt` | New | Generated name + timestamp model |
| `com.kleros.namegenerator/NameHistory.kt` | New | Immutable capped history |
| `com.kleros.namegenerator/NameScreen.kt` | New | Compose screen (generate button, result, history) |
| `com.kleros.MainActivity` | Modified | Add `Screen.NameGenerator` enum entry + conditional rendering |
| `com.kleros.namegenerator/*Test.kt` | New | Unit + UI tests per TDD |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Pattern parser edge cases (invalid patterns) | Low | Single static table — patterns are known-at-compile-time; parse failure tested exhaustively |
| Detekt `MagicNumber` violations from 20-row table | Med | Use `@file:Suppress("MagicNumber")` (same pattern as `DiceScreen`) |

## Rollback Plan

Revert `MainActivity.kt` to its current `DiceScreen`-only state and delete the `namegenerator/` package. Specs remain in `openspec/` for future reimplementation.

## Dependencies

None. Uses only `kotlin.random.Random`, Kotlin stdlib, and existing Compose dependencies.

## Success Criteria

- [ ] `NameGenerator.generate()` produces a non-empty String matching the pattern structure
- [ ] 1000 invocations produce at least 50 unique names from the 20-row table
- [ ] Pattern parser correctly handles all pattern variants (`12o`, `111`, `23-a`, `123+`, etc.)
- [ ] Parenthetical prefix rule applied correctly (`(f)a` → "a" first, "fa" otherwise)
- [ ] Advantage skews results toward higher rows, disadvantage toward lower rows
- [ ] All unit tests pass (`./gradlew test`)
- [ ] UI tests verify screen renders, roll mode selector, and generate button (`./gradlew connectedCheck`)
- [ ] No new Gradle dependencies (reuses existing `DiceRoller` and Compose deps)
