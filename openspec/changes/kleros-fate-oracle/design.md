# Design: Fate Oracle

## Technical Approach

New `com.kleros.fate` package following the CharacterCrafter pattern: `FateData` (9 `OddsLevel` entries with fate chart thresholds), `FateRoller` (pure `roll()` resolving D100 against chart), `FateResult` (sealed class — 4 outcome variants + double detection), `FateHistory` (immutable capped list), `FateScreen` (FilterChip odds selector + roll button + result display + random event badge + history). Wired into `MainActivity` drawer as `Screen.FATE_ORACLE`.

## Architecture Decisions

| Option | Tradeoffs | Decision |
|--------|-----------|----------|
| `sealed class FateResult` vs data class wrapping enum | Sealed = idiomatic `when` exhaustiveness, matches Kotlin sealed pattern; wrapping enum adds indirection | **sealed class FateResult** — direct `when (result)` per user request |
| Thresholds as `IntRange` vs cumulative upper bounds | `IntRange` uses existing `TableEntry.RANGE` pattern; cumulative bounds simpler for direct comparison | **4 Int upper-bound fields** (exYesMax, yesMax, noMax) — compare roll ≤ bound |
| `DiceRoller.roll(D100)` vs custom D100 inline | Reuse existing roller avoids duplication | **Reuse `DiceRoller.roll(DiceType.D100)`** via injectable `rollFn` |
| Double detection: `roll % 11 == 0 \|\| roll == 100` vs string check | Modulo is fast, no allocation | **Modulo + 100 check** — covers 11,22,33,44,55,66,77,88,99,100 |

## Data Flow

```
User taps FilterChip → selectedIndex updates (OddsLevel)
    ↓
User taps Roll → FateRoller.roll(odds, rollFn)
    ├─ DiceRoller.roll(D100) → 1..100
    ├─ Compare roll ≤ exYesMax → ExceptionalYes
    ├─ Compare roll ≤ yesMax   → Yes
    ├─ Compare roll ≤ noMax    → No
    └─ Else                   → ExceptionalNo
    └─ Check double: roll % 11 == 0 || roll == 100 → isDouble
    └─ Returns FateResult (sealed variant)
    ↓
State update → currentResult = fateResult, history = history.append(fateResult)
    ↓
Recomposition → ResultCard + (if isDouble) "⚡ Random Event" badge + HistorySection
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `app/src/main/java/com/kleros/fate/FateData.kt` | Create | `object FateData` with `enum class OddsLevel` (9 entries) + fate chart threshold map |
| `app/src/main/java/com/kleros/fate/FateResult.kt` | Create | Sealed class `FateResult` with 4 data class variants (ExceptionalYes, Yes, No, ExceptionalNo) — each carries `roll`, `oddsLevel`, `isDouble` |
| `app/src/main/java/com/kleros/fate/FateRoller.kt` | Create | `object FateRoller` with `fun roll(oddsLevel, rollFn): FateResult` — resolve threshold, detect double |
| `app/src/main/java/com/kleros/fate/FateHistory.kt` | Create | `data class FateHistory` with capped `append()` (MAX_SIZE=10) |
| `app/src/main/java/com/kleros/fate/FateScreen.kt` | Create | Composable: FilterChip row (9 odds) + Roll button + result card + event badge + history |
| `app/src/main/java/com/kleros/MainActivity.kt` | Modify | Add `Screen.FATE_ORACLE` entry with icon + `when` branch |
| `app/src/test/java/com/kleros/fate/FateRollerTest.kt` | Create | Deterministic roll tests, boundary tests for all 9 odds × 4 outcomes, double detection |
| `app/src/test/java/com/kleros/fate/FateHistoryTest.kt` | Create | Append, cap-at-10, immutability |
| `app/src/test/java/com/kleros/fate/FateDataTest.kt` | Create | Verify all 9 levels have valid thresholds |
| `app/src/androidTest/java/com/kleros/fate/FateScreenTest.kt` | Create | Screen renders chips, roll shows result, double event badge, history items |

## Interfaces / Contracts

```kotlin
// FateData.kt
enum class OddsLevel(val label: String, val exYesMax: Int, val yesMax: Int, val noMax: Int) {
    Impossible("Impossible", 0, 0, 50),
    NoWay("No Way", 0, 5, 80),
    Unlikely("Unlikely", 0, 20, 85),
    Something("Something", 5, 45, 85),
    Even("Even", 10, 55, 85),
    Likely("Likely", 15, 65, 85),
    Probable("Probable", 20, 75, 90),
    AlmostCertain("Almost Certain", 30, 85, 95),
    Certain("Certain", 50, 95, 100),
}
// Note: exNo is always the remainder (noMax+1 .. 100).
// Chart: roll ≤ exYesMax → ExYes | roll ≤ yesMax → Yes | roll ≤ noMax → No | else → ExNo
```

```kotlin
// FateResult.kt
sealed class FateResult {
    abstract val roll: Int
    abstract val oddsLevel: OddsLevel
    abstract val isDouble: Boolean
    abstract val timestampMillis: Long

    data class ExceptionalYes(
        override val roll: Int,
        override val oddsLevel: OddsLevel,
        override val isDouble: Boolean,
        override val timestampMillis: Long = System.currentTimeMillis(),
    ) : FateResult()

    data class Yes(
        override val roll: Int,
        override val oddsLevel: OddsLevel,
        override val isDouble: Boolean,
        override val timestampMillis: Long = System.currentTimeMillis(),
    ) : FateResult()

    data class No(
        override val roll: Int,
        override val oddsLevel: OddsLevel,
        override val isDouble: Boolean,
        override val timestampMillis: Long = System.currentTimeMillis(),
    ) : FateResult()

    data class ExceptionalNo(
        override val roll: Int,
        override val oddsLevel: OddsLevel,
        override val isDouble: Boolean,
        override val timestampMillis: Long = System.currentTimeMillis(),
    ) : FateResult()
}
```

```kotlin
// FateRoller.kt
object FateRoller {
    fun roll(
        oddsLevel: OddsLevel,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): FateResult {
        val roll = rollFn(DiceType.D100)
        val isDouble = roll % 11 == 0 || roll == 100
        return when {
            roll <= oddsLevel.exYesMax -> FateResult.ExceptionalYes(roll, oddsLevel, isDouble)
            roll <= oddsLevel.yesMax -> FateResult.Yes(roll, oddsLevel, isDouble)
            roll <= oddsLevel.noMax -> FateResult.No(roll, oddsLevel, isDouble)
            else -> FateResult.ExceptionalNo(roll, oddsLevel, isDouble)
        }
    }
}
```

```kotlin
// FateHistory.kt — identical pattern to CharacterHistory/CreatureHistory
data class FateHistory(val results: List<FateResult> = emptyList()) {
    companion object { const val MAX_SIZE = 10 }
    fun append(result: FateResult): FateHistory { ... }
}
```

```kotlin
// FateScreen.kt — FilterChip row + Roll button + result + random event badge + history
// testTag convention: "oddsChip_${level.name}", "rollButton", "resultText",
// "eventBadge", "historyList", "historyItem"
```

## Screen Layout (ASCII)

```
┌────────────────────────────────────┐
│  Fate Oracle                       │
│                                    │
│  [Impossible] [No Way] [Unlikely]  │  ← FilterChip row
│  [Something] [Even] [Likely]       │
│  [Probable] [Almost Certain]       │
│  [Certain]                         │
│                                    │
│  [ Roll Fate ]                     │  ← FilledTonalButton
│                                    │
│  ┌─ Result ─────────────────────┐  │
│  │ Even: Yes                    │  │  ← outcome label
│  │ ⚡ Random Event!             │  │  ← double badge (conditional)
│  └──────────────────────────────┘  │
│                                    │
│  History                           │
│  ───────────────────────────────── │
│  Even: Yes ⚡                      │
│  Likely: No                        │
│  ...                               │
└────────────────────────────────────┘
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|---------|
| Unit — FateRoller | Deterministic `roll` returns expected variant for each odds×roll combo | Inject `rollFn` returning exact values; test all 4 outcomes per odds level |
| Unit — FateRoller | Boundary values: 1, exYesMax, exYesMax+1, yesMax, yesMax+1, noMax, noMax+1, 100 | 3-value boundary per level (bottom of range, top, first outside) |
| Unit — FateRoller | Double detection: 11,22,33,44,55,66,77,88,99,100 → isDouble=true; 1,10,12,20 → false | Deterministic rollFn |
| Unit — FateHistory | Append, cap at 10, newest-first ordering | Direct assertions |
| Unit — FateData | All 9 levels have valid thresholds (exYesMax ≤ yesMax ≤ noMax ≤ 100) | Data validation |
| E2E — FateScreen | Chips render, roll shows result, event badge appears on double, history accumulates | Compose test rules |

## Migration / Rollout

No migration required. Pure additive feature — new package, no existing code changed outside `MainActivity.kt` Screen enum.

## Open Questions

- [ ] Icon for Screen.FATE_ORACLE — proposes `Icons.Filled.Visibility` or `Icons.Filled.AutoAwesome`. Final choice during implementation.

## Summary

- **Approach**: 5 files in `com.kleros.fate` package following CharacterCrafter pattern, 4 test files, 1 modified `MainActivity.kt`
- **Key Decisions**: Sealed class FateResult (4 variants), cumulative upper-bound threshold comparison, modulo-based double detection, injectable rollFn
- **Files Affected**: 6 new + 1 modified + 4 new test files
- **Testing Strategy**: Unit (FateRoller boundaries + doubles + FateHistory) + E2E (FateScreen compose)
- **Open Questions**: Screen icon selection
