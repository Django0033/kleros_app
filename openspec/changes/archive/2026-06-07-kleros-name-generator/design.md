# Design: Kleros Name Generator

## Technical Approach

Pure-function domain layer (`NameGenerator`, `NameTable`, `NameHistory`, `RollMode`) in `com.kleros.namegenerator`, Android-free. Pattern parsing is internal to `NameGenerator`. UI layer (`NameScreen`) uses `remember`/`mutableStateOf` — no ViewModel. Existing `DiceRoller.roll(DiceType.D20)` is reused for all dice rolls via an injectable `rollFn` parameter for testability. Screen navigation adds a `Screen` enum in `MainActivity` with a `FilterChip` row toggle.

## Architecture Decisions

| Option | Tradeoffs | Decision |
|--------|-----------|----------|
| RollMode in its own file vs inline enum | Own file mirrors DiceType pattern, isolates concern | Separate `RollMode.kt` |
| Pattern parser as private function in NameGenerator vs separate class | Private keeps API surface minimal; separate makes it independently testable | Private function — tested via `generate()` with controlled rolls |
| `rollFn` injection via lambda vs interface | Lambda is lighter, matches Kotlin idioms; interface adds ceremony | `generate(rollMode, rollFn: (DiceType) -> Int = { DiceRoller.roll(it) })` |
| Parenthetical prefix resolution inline vs separate | Inline suffices — single rule, single table | Inline in generation loop |
| Suffix letters as separate operations vs post-concatenation | Parsing into operations keeps pattern logic uniform | `LiteralSuffix` operation appended to final string |

## Data Flow

```
User tap → RollMode chip selected
                ↓
User tap → Generate button onClick
                ↓
    NameGenerator.generate(rollMode, rollFn)
                ↓
  1. rollFn(D20) × (1 or 2) → pattern row
     (ADVANTAGE = max(2d20), DISADVANTAGE = min(2d20))
                ↓
  2. Pattern string → parsePattern() → List<Operation>
                ↓
  3. For each SyllableColumn op:
     rollFn(D20) → pick random row → get cell value
     → resolve parenthetical → collect syllable
                ↓
  4. Concatenate syllables + LiteralSuffix entries
                ↓
     NameResult(name, rollMode, timestamp)
                ↓
  currentResult = result
  history = history.append(result)
```

## Component Specifications

### `RollMode` — enum (`RollMode.kt`)
```kotlin
enum class RollMode(val label: String) {
    NORMAL("Normal"),
    ADVANTAGE("Advantage"),
    DISADVANTAGE("Disadvantage")
}
```

### `NameResult` — data class (`NameResult.kt`)
```kotlin
data class NameResult(
    val name: String,
    val rollMode: RollMode,
    val timestampMillis: Long = System.currentTimeMillis()
)
```

### `NameHistory` — data class (`NameHistory.kt`)
```kotlin
data class NameHistory(val results: List<NameResult> = emptyList()) {
    companion object { const val MAX_SIZE = 10 }
    fun append(result: NameResult): NameHistory =
        copy(results = (listOf(result) + results).take(MAX_SIZE))
}
```

### `NameTable` — object (`NameTable.kt`)
```kotlin
data class NameTableRow(
    val pattern: String,
    val inicio1: String,
    val inicio2: String,
    val ending: String
)

@file:Suppress("MagicNumber")
object NameTable {
    val rows: List<NameTableRow> = listOf(
        // 20 rows of pattern + syllable strings
        // Each column (inicio1, inicio2, ending) is a single string per row
    )

    fun row(index: Int): NameTableRow = rows[index - 1]  // 1-indexed
}
```

### `NameGenerator` — object (`NameGenerator.kt`)
```kotlin
object NameGenerator {
    fun generate(
        rollMode: RollMode = RollMode.NORMAL,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) }
    ): NameResult
}
```

Internal structure:
- `parsePattern(pattern: String): List<Operation>` — sealed interface with `SyllableColumn(column, range)` and `LiteralSuffix(text)`
- `resolveCellValue(value: String, isFirst: Boolean): String` — handles `(f)a` → "a" if first, "fa" otherwise
- `rollRow(rollFn, range): Int` — maps 1d20 to row (ALL=1-20, FIRST_HALF=1-10 via `(roll+1)/2`, LAST_HALF=11-20 via `(roll+1)/2+10`)

### `NameScreen` — `@Composable` (`NameScreen.kt`)

State:
- `rollMode: MutableState<RollMode>` — default NORMAL
- `currentResult: MutableState<NameResult?>` — null initially
- `history: MutableState<NameHistory>` — empty

Layout (top→bottom):
1. RollMode selector: `FilterChip` row (Normal / Advantage / Disadvantage)
2. Generate button: `FilledTonalButton`, calls `NameGenerator.generate(rollMode)`
3. Result card: `ElevatedCard` with the generated name text
4. History section: newest-first list, max 10 items

### `MainActivity` — modified

```kotlin
enum class Screen { Dice, NameGenerator }

// In setContent:
var currentScreen by remember { mutableStateOf(Screen.Dice) }

Scaffold { innerPadding ->
    Column(modifier = Modifier.padding(innerPadding)) {
        // Screen toggle row
        Row {
            FilterChip("Dice", selected = currentScreen == Screen.Dice)
            FilterChip("Name Gen", selected = currentScreen == Screen.NameGenerator)
        }
        when (currentScreen) {
            Screen.Dice -> DiceScreen()
            Screen.NameGenerator -> NameScreen()
        }
    }
}
```

## Pattern Parsing Details

Input pattern string scanned left-to-right. Each character classified:

| Char class | Output | Examples |
|---|---|---|
| Digit `1` | `SyllableColumn(INICIO1, ALL)` | `1` in `12o` |
| Digit `2` | `SyllableColumn(INICIO2, ALL)` | `2` in `23-a` |
| Digit `3` | `SyllableColumn(ENDING, ALL)` | `3` in `123+` |
| `3-` (digit + minus) | `SyllableColumn(ENDING, FIRST_HALF)` | `3-` in `23-a` |
| `3+` (digit + plus) | `SyllableColumn(ENDING, LAST_HALF)` | `3+` in `123+` |
| Letter (a-z) | `LiteralSuffix(text)` | `o` in `12o`, `a` in `23-a` |

Examples:
- `"12o"` → `[INICIO1, INICIO2, Suffix("o")]`
- `"23-a"` → `[INICIO2, ENDING(FIRST_HALF), Suffix("a")]`
- `"123+"` → `[INICIO1, INICIO2, ENDING(LAST_HALF)]`
- `"1o3o"` → `[INICIO1, Suffix("o"), ENDING(ALL), Suffix("o")]`

### DiceRoller Integration

Every syllable roll uses `rollFn(DiceType.D20)`:
- **ALL**: result 1-20 → row index directly
- **FIRST_HALF**: result 1-20 → `(roll + 1) / 2` → 1-10
- **LAST_HALF**: result 1-20 → `(roll + 1) / 2 + 10` → 11-20

This keeps all rolls through `DiceRoller.roll(D20)` — no new DiceType needed.

### Parenthetical Resolution

Cell value scanned for `^(\(.+\))(.+)$`:
- `"(f)a"` → prefix="f", base="a"
- If this is the first syllable generated → emit `base`
- Otherwise → emit `prefix + base`

## DiceRoller Integration

`NameGenerator.generate()` takes `rollFn: (DiceType) -> Int` defaulting to `{ DiceRoller.roll(it) }`. The pattern roll uses the mode modifier, then all syllable rolls pass `DiceType.D20` to `rollFn`.

Test injection: pass a controlled `rollFn` that returns known values to verify exact output without randomness.

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `app/src/main/java/com/kleros/namegenerator/RollMode.kt` | Create | Enum: NORMAL, ADVANTAGE, DISADVANTAGE |
| `app/src/main/java/com/kleros/namegenerator/NameTable.kt` | Create | 20-row static table + `NameTableRow` data class |
| `app/src/main/java/com/kleros/namegenerator/NameGenerator.kt` | Create | Pure generation with pattern parser |
| `app/src/main/java/com/kleros/namegenerator/NameResult.kt` | Create | Data class wrapping name + timestamp |
| `app/src/main/java/com/kleros/namegenerator/NameHistory.kt` | Create | Capped list (10), newest first |
| `app/src/main/java/com/kleros/namegenerator/NameScreen.kt` | Create | Compose screen (mode selector, generate, result, history) |
| `app/src/main/java/com/kleros/MainActivity.kt` | Modify | Add `Screen` enum + toggle row between Dice and NameGenerator |
| `app/src/test/java/com/kleros/namegenerator/NameGeneratorTest.kt` | Create | Pattern parsing, controlled-roll generation, advantage/disadvantage skew |
| `app/src/test/java/com/kleros/namegenerator/NameHistoryTest.kt` | Create | Max size, ordering, empty edge case |
| `app/src/androidTest/java/com/kleros/namegenerator/NameScreenTest.kt` | Create | Compose UI test: select mode, generate, assert result + history |

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | `NameGenerator.generate()` with controlled `rollFn` | Inject rolls that produce specific patterns → assert exact name output |
| Unit | Pattern parsing edge cases | Test all pattern variants: `12o`, `111`, `23-a`, `123+`, `1o3o`, `3+` |
| Unit | Parenthetical prefix | Cell `(f)a` as first → "a"; not first → "fa" |
| Unit | Advantage/Disadvantage skew | 500+ calls with ADVANTAGE → avg row > NORMAL; DISADVANTAGE → avg row < NORMAL |
| Unit | Name uniqueness | 1000 calls → ≥50 unique names |
| Unit | `NameHistory.append()` | Append 12 → size 10, newest first. Append 0 → empty. |
| UI | `NameScreen` | Compose UI Test: select roll mode chip, tap Generate, assert result visible. Generate 3× → history count = 3. |
| Lint | detekt/ktlint | `./gradlew detekt ktlintCheck` must pass. `@file:Suppress("MagicNumber")` on NameTable.kt. |

## Sequence Diagram

```
User         NameScreen           NameGenerator        DiceRoller       NameHistory
 │                │                     │                   │                │
 │ tap Normal     │                     │                   │                │
 │───────────────>│ rollMode=ADVANTAGE   │                   │                │
 │                │                     │                   │                │
 │ tap Generate   │                     │                   │                │
 │───────────────>│                     │                   │                │
 │                │ generate(ADV,D20Fn) │                   │                │
 │                │────────────────────>│                   │                │
 │                │                     │ roll(D20) [2×]   │                │
 │                │                     │──────────────────>│                │
 │                │                     │ max(15,8)=15      │                │
 │                │                     │<──────────────────│                │
 │                │                     │                   │                │
 │                │                     │ parse("12o")     │                │
 │                │                     │ [INICIO1,INICIO2, │                │
 │                │                     │  Suffix("o")]     │                │
 │                │                     │                   │                │
 │                │                     │ roll(D20) [2×]   │                │
 │                │                     │──────────────────>│                │
 │                │                     │ 7, 12             │                │
 │                │                     │<──────────────────│                │
 │                │                     │                   │                │
 │                │                     │ → "Mor"+"gan"+"o" │                │
 │                │                     │ = "Morgano"       │                │
 │                │                     │                   │                │
 │                │           NameResult("Morgano")         │                │
 │                │<────────────────────│                   │                │
 │                │                     │                   │                │
 │                │ append(result)                          │                │
 │                │───────────────────────────────────────────────────────>│
 │                │              NameHistory(size=1)                       │
 │                │<───────────────────────────────────────────────────────│
 │                │                     │                   │                │
 │ sees name +    │                     │                   │                │
 │ history grows  │                     │                   │                │
 │<───────────────│                     │                   │                │
```

## Migration / Rollout

No migration required. Additive files in new package `com.kleros.namegenerator` + small change in `MainActivity.kt`. Rollback: revert `MainActivity.kt`, delete `app/src/main/java/com/kleros/namegenerator/`.

## Open Questions

- None.
