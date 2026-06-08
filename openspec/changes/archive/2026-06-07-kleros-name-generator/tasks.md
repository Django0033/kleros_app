# Tasks: Kleros Name Generator

## Phase 1 — Domain Types

### T1: ✅ Define `RollMode` enum

| Field | Value |
|-------|-------|
| **ID** | `roll-mode-def` |
| **Title** | Define RollMode enum (NORMAL, ADVANTAGE, DISADVANTAGE) |
| **Test file** | `app/src/test/java/com/kleros/namegenerator/RollModeTest.kt` |
| **Src file** | `app/src/main/java/com/kleros/namegenerator/RollMode.kt` |
| **TDD** | REQUIRED — RED (test enum values, label property, ordinal) → GREEN (define enum) |
| **Dependencies** | None |
| **Effort** | XS (3–5 lines prod, ~20 lines test) |
| **Acceptance** | `RollMode.valueOf("NORMAL")` returns NORMAL; `RollMode.ADVANTAGE.label == "Advantage"`; all 3 entries match spec |

**Test cases to cover:**
- `NORMAL` is first entry (default)
- `label` property returns the correct display string for each value
- All 3 modes are present (`entries.size == 3`)

**Production:**
```kotlin
enum class RollMode(val label: String) {
    NORMAL("Normal"),
    ADVANTAGE("Advantage"),
    DISADVANTAGE("Disadvantage")
}
```

---

### T2: ✅ Define `NameResult` data class

| Field | Value |
|-------|-------|
| **ID** | `name-result-def` |
| **Title** | Define NameResult data class |
| **Test file** | `app/src/test/java/com/kleros/namegenerator/NameResultTest.kt` |
| **Src file** | `app/src/main/java/com/kleros/namegenerator/NameResult.kt` |
| **TDD** | REQUIRED — RED (test data class fields, default timestamp) → GREEN (define data class) |
| **Dependencies** | T1 (`RollMode`) |
| **Effort** | XS (3–5 lines prod, ~25 lines test) |
| **Acceptance** | `NameResult("Elara", RollMode.NORMAL).name == "Elara"`; `rollMode == NORMAL`; `timestampMillis` is auto-populated |

**Test cases to cover:**
- Constructor assigns fields correctly
- Default `timestampMillis` is populated (> 0)
- `copy()` works (data class behavior)

**Production:**
```kotlin
data class NameResult(
    val name: String,
    val rollMode: RollMode,
    val timestampMillis: Long = System.currentTimeMillis()
)
```

---

### T3: ✅ Define `NameHistory` data class

| Field | Value |
|-------|-------|
| **ID** | `name-history-def` |
| **Title** | Define NameHistory with capped append, newest-first ordering |
| **Test file** | `app/src/test/java/com/kleros/namegenerator/NameHistoryTest.kt` |
| **Src file** | `app/src/main/java/com/kleros/namegenerator/NameHistory.kt` |
| **TDD** | REQUIRED — RED (test append, cap, ordering, empty) → GREEN (define data class) |
| **Dependencies** | T2 (`NameResult`) |
| **Effort** | XS (~10 lines prod, ~40 lines test) |
| **Acceptance** | Empty history returns empty list; append 12 → size 10, oldest evicted; newest is at index 0; `MAX_SIZE == 10` |

**Test cases to cover** (mirrors `RollHistoryTest`):
- `results` is empty for fresh instance
- Append 3 → size 3, newest first
- Append 12 → caps at 10, discards oldest
- Timestamps monotonically decreasing (newest at index 0)
- `MAX_SIZE == 10`

**Production:**
```kotlin
data class NameHistory(val results: List<NameResult> = emptyList()) {
    companion object { const val MAX_SIZE = 10 }
    fun append(result: NameResult): NameHistory =
        copy(results = (listOf(result) + results).take(MAX_SIZE))
}
```

---

## Phase 2 — Name Table

### T4: ✅ Define `NameTableRow` data class and `NameTable` object with 20 rows

| Field | Value |
|-------|-------|
| **ID** | `name-table-def` |
| **Title** | Define NameTableRow + NameTable with static 20-row syllable table |
| **Test file** | `app/src/test/java/com/kleros/namegenerator/NameTableTest.kt` |
| **Src file** | `app/src/main/java/com/kleros/namegenerator/NameTable.kt` |
| **TDD** | REQUIRED — RED (test row count, non-empty columns, row access by index) → GREEN (define 20 rows + `row()` helper) |
| **Dependencies** | None |
| **Effort** | M (~60 lines prod, ~30 lines test) |
| **Acceptance** | `NameTable.rows.size == 20`; every row has non-empty Pattern, Inicio1, Inicio2, Ending; `NameTable.row(1)` returns first row; `row(20)` returns last; `@file:Suppress("MagicNumber")` applied |

**Test cases to cover:**
- `rows.size == 20`
- Every row has all 4 columns non-empty
- `row(n)` maps correctly (1-indexed)
- `row(1)` through `row(20)` do not throw

**Production notes:**
- `NameTableRow` data class with `pattern`, `inicio1`, `inicio2`, `ending`
- `NameTable` object with `val rows: List<NameTableRow>`
- `fun row(index: Int): NameTableRow = rows[index - 1]` (1-indexed to match dice roll)
- `@file:Suppress("MagicNumber")` at file level
- Syllable content adapted from the spec/proposal — each row is a compile-time constant

---

## Phase 3 — Name Generator

### T5: ✅ Implement pattern parser (internal to NameGenerator)

| Field | Value |
|-------|-------|
| **ID** | `pattern-parser` |
| **Title** | Implement pattern parser: digit → column ops, `3-`/`3+` range modifiers, suffix extraction |
| **Test file** | Tests in `NameGeneratorTest.kt` (tested indirectly via controlled-roll `generate()`, or directly as internal visibility allows) |
| **Src file** | `app/src/main/java/com/kleros/namegenerator/NameGenerator.kt` (or internal file) |
| **TDD** | REQUIRED — RED (test all pattern variants via parse results) → GREEN (implement scanner) |
| **Dependencies** | T4 (`NameTable`, `NameTableRow`) |
| **Effort** | M (~30 lines prod, ~40 lines test) |
| **Acceptance** | All pattern variants from spec produce correct operations: `12o` → [INICIO1, INICIO2, Suffix("o")]; `23-a` → [INICIO2, ENDING(FIRST_HALF), Suffix("a")]; `123+` → [INICIO1, INICIO2, ENDING(LAST_HALF)]; `111` → [INICIO1, INICIO1, INICIO1]; `1o3o` → [INICIO1, Suffix("o"), ENDING(ALL), Suffix("o")] |

**Sealed interface:**
```kotlin
sealed interface Operation {
    data class SyllableColumn(val column: Column, val range: Range) : Operation
    data class LiteralSuffix(val text: String) : Operation
}

enum class Column { INICIO1, INICIO2, ENDING }
enum class Range { ALL, FIRST_HALF, LAST_HALF }
```

**Parser logic:**
- Scan string left-to-right
- Digit `1` → `SyllableColumn(INICIO1, ALL)`
- Digit `2` → `SyllableColumn(INICIO2, ALL)`
- Digit `3` followed by `-` → `SyllableColumn(ENDING, FIRST_HALF)` (consume `-`)
- Digit `3` followed by `+` → `SyllableColumn(ENDING, LAST_HALF)` (consume `+`)
- Digit `3` alone → `SyllableColumn(ENDING, ALL)`
- Letter (a-z) → `LiteralSuffix(text)` (accumulate consecutive letters)
- Unknown chars → skip

---

### T6: ✅ Implement `NameGenerator.generate()` with parenthetical resolution and roll-mode skew

| Field | Value |
|-------|-------|
| **ID** | `name-generator-core` |
| **Title** | Implement NameGenerator.generate() with pattern-based syllable concatenation and parenthetical prefix rule |
| **Test file** | `app/src/test/java/com/kleros/namegenerator/NameGeneratorTest.kt` |
| **Src file** | `app/src/main/java/com/kleros/namegenerator/NameGenerator.kt` |
| **TDD** | REQUIRED — RED → GREEN (combines parser, syllable roll, parenthetical resolution, and concatenation) |
| **Dependencies** | T1 (`RollMode`), T4 (`NameTable`), T5 (`pattern-parser`) |
| **Effort** | L (~60 lines prod, ~80 lines test) |
| **Acceptance** | Controlled `rollFn` produces deterministic names; parenthetical `(f)a` resolves to "a" for first syllable, "fa" otherwise; ADVANTAGE/DISADVANTAGE skew verified statistically; 1000 invocations with random rolls produce ≥50 unique names; generation completes in <1ms |

**Test cases to cover:**
- Controlled rollFn with known values → exact expected name
- Pattern `12o` with rolls producing row 1 → verify pattern applied correctly
- Pattern `111` with controlled rolls → all 3 from Inicio1
- Pattern `23-a` with controlled rolls → Ending from first-half range
- Pattern `123+` with controlled rolls → Ending from last-half range
- Parenthetical: `(f)a` as first syllable → "a"
- Parenthetical: `(f)a` as non-first syllable → "fa"
- Parenthetical: non-parenthetical cell (`de`, `ka`, `li`) → always as-is
- ADVANTAGE: 1000 calls → mean row index higher than NORMAL
- DISADVANTAGE: 1000 calls → mean row index lower than NORMAL
- 1000 calls with random rolls → ≥50 unique names
- Generation completes in under 1ms (time measurement, not strict assertion)
- `rollFn` default delegates to `DiceRoller.roll(DiceType.D20)`
- `generate(rollMode)` with no explicit rollFn works (default parameter)

**Production details:**

```kotlin
object NameGenerator {
    fun generate(
        rollMode: RollMode = RollMode.NORMAL,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) }
    ): NameResult {
        // 1. Roll pattern row with mode modifier
        val patternRoll = when (rollMode) {
            RollMode.NORMAL -> rollFn(DiceType.D20)
            RollMode.ADVANTAGE -> maxOf(rollFn(DiceType.D20), rollFn(DiceType.D20))
            RollMode.DISADVANTAGE -> minOf(rollFn(DiceType.D20), rollFn(DiceType.D20))
        }
        val row = NameTable.row(patternRoll)

        // 2. Parse pattern
        val operations = parsePattern(row.pattern)

        // 3. Execute operations with parenthetical resolution
        val syllables = mutableListOf<String>()
        var isFirst = true
        for (op in operations) {
            when (op) {
                is Operation.SyllableColumn -> {
                    val roll = rollFn(DiceType.D20)
                    val rowIndex = mapRollToRow(roll, op.range)
                    val cellValue = getCellValue(rowIndex, op.column)
                    syllables.add(resolveParenthetical(cellValue, isFirst))
                    isFirst = false
                }
                is Operation.LiteralSuffix -> syllables.add(op.text)
            }
        }

        return NameResult(name = syllables.joinToString(""), rollMode = rollMode)
    }
}
```

**Helper functions:**
- `mapRollToRow(roll: Int, range: Range): Int`:
  - `ALL` → roll (1–20)
  - `FIRST_HALF` → `(roll + 1) / 2` (1–10)
  - `LAST_HALF` → `(roll + 1) / 2 + 10` (11–20)
- `getCellValue(rowIndex: Int, column: Column): String` — read from NameTable row
- `resolveParenthetical(value: String, isFirst: Boolean): String` — uses regex `^\((.+)\)(.+)$` to detect parenthetical; if match: emit prefix+base or just base depending on `isFirst`; otherwise emit value as-is

---

## Phase 4 — Name Screen

### T7: Implement `NameScreen` composable

| Field | Value |
|-------|-------|
| **ID** | `name-screen-ui` |
| **Title** | Implement NameScreen with RollMode selector, Generate button, result card, and history list |
| **Test file** | `app/src/androidTest/java/com/kleros/namegenerator/NameScreenTest.kt` |
| **Src file** | `app/src/main/java/com/kleros/namegenerator/NameScreen.kt` |
| **TDD** | REQUIRED — UI test first (RED) → implement composable → UI test passes (GREEN) |
| **Dependencies** | T1 (`RollMode`), T2 (`NameResult`), T3 (`NameHistory`), T6 (`NameGenerator`) |
| **Effort** | L (~120 lines prod, ~80 lines test) |
| **Acceptance** | RollMode selector renders 3 FilterChips; Generate button produces name; result card shows name; history shows newest-first capped at 10; empty history shows "No names yet" placeholder; no new Gradle dependencies |

**Composable layout (top→bottom):**
1. Title: `Text("Name Generator")` using titleMedium
2. RollMode selector: `FlowRow` of `FilterChip` for each mode (Normal / Advantage / Disadvantage), selected chip highlighted
3. Generate button: `FilledTonalButton` with text "Generate", calls `NameGenerator.generate(rollMode)` on click
4. Result card: `ElevatedCard` with the generated name in `displayLarge` or similar prominent text style, using testTag `"resultName"`
5. History section: title "History", then newest-first list; if empty shows "No names yet"; each item uses testTag `"historyItem"` showing name + mode badge

**State (composable-local, no ViewModel):**
```kotlin
var rollMode by remember { mutableStateOf(RollMode.NORMAL) }
var currentResult by remember { mutableStateOf<NameResult?>(null) }
var history by remember { mutableStateOf(NameHistory()) }
```

**UI test cases to cover (in NameScreenTest.kt):**
- Screen renders with "Generate" button and RollMode chips
- Select "Advantage" chip → chip visually selected
- Tap "Generate" → result name displayed (via `"resultName"` testTag)
- Generate 3 times → history shows 3 entries
- Changing mode does not reset history
- History shows "No names yet" when empty

---

## Phase 5 — Integration

### T8: Wire NameScreen into MainActivity

| Field | Value |
|-------|-------|
| **ID** | `main-activity-wiring` |
| **Title** | Add Screen enum, toggle row, and conditional NameScreen rendering in MainActivity |
| **Test file** | `app/src/androidTest/java/com/kleros/ExampleInstrumentedTest.kt` (existing, may update) |
| **Src file** | `app/src/main/java/com/kleros/MainActivity.kt` |
| **TDD** | NOT REQUIRED (integration wiring — minimal logic) |
| **Dependencies** | T7 (`NameScreen`) |
| **Effort** | XS (~15 lines delta) |
| **Acceptance** | App starts to DiceScreen by default; toggle chip "Name Gen" switches to NameScreen; toggle chip "Dice" switches back; no regressions in DiceScreen |

**Changes to `MainActivity.kt`:**
1. Add `enum class Screen { Dice, NameGenerator }` in the file
2. Add `var currentScreen by remember { mutableStateOf(Screen.Dice) }`
3. Add a `Row` of `FilterChip`(s) above the content inside the Scaffold
4. Replace direct `DiceScreen(...)` with `when(currentScreen)` block

**Test notes:**
- No dedicated test file needed (instrumented test for screen switching would overlap with DiceScreenTest)
- Manual verification: run app, toggle between screens, verify both work

---

## Phase 6 — Quality Gates

### T9: Run lint and detekt verification

| Field | Value |
|-------|-------|
| **ID** | `lint-verification` |
| **Title** | Verify detekt and ktlint pass for all new files |
| **Test file** | N/A (lint/detekt are build tasks) |
| **Src file** | N/A |
| **TDD** | NOT REQUIRED |
| **Dependencies** | All prior tasks |
| **Effort** | XS (run `./gradlew detekt ktlintCheck`, fix any violations) |
| **Acceptance** | `./gradlew detekt ktlintCheck` passes with no new warnings; `@file:Suppress("MagicNumber")` on `NameTable.kt` and `NameScreen.kt` |

**Known suppressions needed:**
- `NameTable.kt`: `@file:Suppress("MagicNumber")` (20 rows of constants)
- `NameScreen.kt`: `@file:Suppress("MagicNumber")` (padding/spacing values, same pattern as `DiceScreen.kt`)

---

## Dependency Graph

```
T1 (RollMode) ──┐
                 ├── T6 (NameGenerator) ──┐
T2 (NameResult) ─┤                        │
                 │                        │
T3 (NameHistory) ┤                        ├── T7 (NameScreen) ──┐
                 │                        │                     │
T4 (NameTable) ──┼── T5 (PatternParser) ──┤                     │
                 │                        │                     │
                 └── T6 depends on T1,T4  │                     │
                                           T7 depends on T1,T2, ├── T8 (MainActivity) ── T9 (Lint)
                                           T3,T6                 │
                                                                  T8 depends on T7
```

**Parallelizable groups:**
- **Parallel batch 1**: T1 (RollMode), T2 (NameResult), T4 (NameTable) — no deps between them
- **Parallel batch 2**: T3 (NameHistory, depends on T2), T5 (PatternParser, depends on T4)
- **Sequential**: T6 (depends on T1, T4, T5) → T7 (depends on T1, T2, T3, T6) → T8 (depends on T7) → T9

---

## Delivery Forecast

| Metric | Value |
|--------|-------|
| **Total prod lines** | ~260 (RollMode 5 + NameResult 5 + NameHistory 10 + NameTable 60 + NameGenerator 60 + NameScreen 105 + MainActivity delta 15) |
| **Total test lines** | ~260 (RollMode 20 + NameResult 25 + NameHistory 40 + NameTable 30 + NameGenerator 80 + NameScreen 80) |
| **Total files created** | 7 prod + 5 test = 12 |
| **Total files modified** | 2 (MainActivity.kt, possibly ExampleInstrumentedTest.kt) |
| **Review budget** | ~400 lines (within the 400-line review budget) |
| **PR strategy** | **PR 1 of 2 (chained PRs)** — this batch covers T1-T6 (domain types + NameTable + NameGenerator core). PR 2 covers T7-T9 (NameScreen + MainActivity wiring + lint). |
| **Estimated execution** | 2–3 sessions / ~1–2 hours for an experienced developer following these tasks |

---

## Risks

| # | Risk | Mitigation |
|---|------|------------|
| R1 | **Parenthetical parsing regex edge cases**: cell values like `((x)y)z` could break simple regex | Use simple `indexOf('(')` / `indexOf(')')` based extraction instead of regex; test with all actual table values |
| R2 | **Pattern parser ambiguity**: `3` vs `3-` vs `3+` lookahead could cause off-by-one if not careful | Scan character-by-character with explicit lookahead; test exhaustively with all 20 known patterns |
| R3 | **Detekt false positives beyond MagicNumber**: `NameGenerator` may trigger `TooManyFunctions` or `CyclomaticComplexity` | Run `./gradlew detekt` early after T6 and suppress as needed with inline `@Suppress` |
| R4 | **NameScreen test flakiness**: Compose UI tests with random generation may produce different UI states | Use testTag-based assertions (`"resultName"`, `"historyItem"`) rather than text content assertions |
| R5 | **History not reset on mode change**: Spec says mode change MUST NOT reset history, but easy to accidentally clear state | Test explicitly in UI tests: generate → switch mode → verify history count unchanged |
