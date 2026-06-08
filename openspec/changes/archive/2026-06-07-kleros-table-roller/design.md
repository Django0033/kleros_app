# Design: TableRoller + TableScreen

## Technical Approach

Generic table-roller engine (`com.kleros.table`) with three `TableEntry` variants, injectable `() -> Int` rollFn, and a reusable `TableScreen` composable. Pure additive — no existing code changed. Each future oracle supplies data only (TableDef list). Engine is pure Kotlin with zero Android dependencies.

## Architecture Decisions

### Decision: rollFn as `() -> Int` (not `(DiceType) -> Int`)

| Option | Tradeoff |
|--------|----------|
| `() -> Int` defaulting to `DiceRoller.roll(D20)` | Simpler call site; TableDef carries no diceType |
| `(DiceType) -> Int` per NameGenerator pattern | More flexible; each TableDef could specify its own die |

**Decision**: `() -> Int` — the spec is explicit. TableDef is `(name, entries)` only. Callers override rollFn if they need a different die. Default is `DiceRoller.roll(DiceType.D20)`.

### Decision: TableRollResult as sealed class with Error variant

| Option | Tradeoff |
|--------|----------|
| Sealed class: `Success(value)` + `Error(message)` | No nulls; caller forced to handle both cases |
| Nullable return with `error()` throw | Implicit; caller can forget |

**Decision**: Sealed class per spec. Roller returns `Result.Error` when no entry matches the roll. UI pattern-matches to show result or error. History stores only `Success` entries.

### Decision: RANGE_MODIFIER clamps effective value to [min, max]

**Rationale**: Per spec scenarios — negative modifier floors at min, large modifier caps at max. After matching the entry by raw roll, effective = `coerceIn(roll + modifier, min, max)`. The result string is always the entry's display value.

### Decision: Immutable history with configurable maxSize

**Rationale**: Follows existing `RollHistory`/`NameHistory` pattern — data class with `append()` returning new copy. Spec adds `maxSize` as constructor param (default 10). Only `Success` entries stored.

## Data Flow

```
User taps FilterChip
  → selectedTable state updated
  → Chip highlights

User taps "Roll"
  → TableRoller.roll(tableDef, rollFn)
      → rollFn() → raw Int
      → match against entries: RANGE | DIRECT | RANGE_MODIFIER
      → RANGE_MODIFIER: effective = (raw + modifier).coerceIn(min, max)
      → return Success(value=entry.result) or Error(message)
  → pattern match on result:
      Success → display + append to history
      Error   → display error message, skip history
  → Compose recomposes ResultCard + HistorySection
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `com/kleros/table/TableEntry.kt` | Create | Sealed class: RANGE, DIRECT, RANGE_MODIFIER |
| `com/kleros/table/TableDef.kt` | Create | `data class TableDef(name, entries)` |
| `com/kleros/table/TableRoller.kt` | Create | Pure `roll()` dispatching by entry type |
| `com/kleros/table/TableRollResult.kt` | Create | Sealed class: Success(value), Error(message) |
| `com/kleros/table/TableHistory.kt` | Create | Capped list, newest first, only Success entries |
| `com/kleros/table/TableScreen.kt` | Create | FilterChips → roll button → result → history |
| `test/.../table/TableRollerTest.kt` | Create | 3 entry types, rollFn injection, clamping, edge cases |
| `test/.../table/TableHistoryTest.kt` | Create | Max size, ordering, eviction |
| `androidTest/.../table/TableScreenTest.kt` | Create | Selection, roll, history, switch table |

No existing files modified. No new Gradle dependencies.

## Interfaces / Contracts

```kotlin
// Package: com.kleros.table

sealed class TableEntry {
    data class RANGE(
        val min: Int, val max: Int, val result: String
    ) : TableEntry()
    data class DIRECT(
        val index: Int, val result: String
    ) : TableEntry()
    data class RANGE_MODIFIER(
        val min: Int, val max: Int,
        val result: String, val modifier: Int
    ) : TableEntry()
}

data class TableDef(
    val name: String,
    val entries: List<TableEntry>,
)

sealed class TableRollResult {
    data class Success(val value: String) : TableRollResult()
    data class Error(val message: String) : TableRollResult()
}

data class TableHistory(
    val entries: List<TableRollResult.Success> = emptyList(),
    val maxSize: Int = 10,
) {
    fun append(result: TableRollResult.Success): TableHistory
}

object TableRoller {
    fun roll(
        table: TableDef,
        rollFn: () -> Int = { DiceRoller.roll(DiceType.D20) },
    ): TableRollResult
}

// TableScreen — composable, com.kleros.table
@Composable
fun TableScreen(
    tables: List<TableDef>,
    modifier: Modifier = Modifier,
)
```

### RANGE_MODIFIER resolution

1. `rollFn()` → raw value
2. If raw in [min, max]: `effective = (raw + modifier).coerceIn(min, max)`
3. Return `Success(entry.result)` — effective value is computed but not part of result per spec
4. If raw outside [min, max]: continue scanning entries

### Entry matching order

Entries are scanned in declaration order. First match wins. For RANGE and RANGE_MODIFIER, this allows catch-all entries at the end (e.g., `RANGE(1, 20, "Wildcard")`).

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | RANGE match | deterministic rollFn returning values in/out of range |
| Unit | DIRECT exact index | rollFn == index → Success; != → skip or Error |
| Unit | RANGE_MODIFIER clamping | Negative modifier floors at min, large caps at max |
| Unit | No matching entry | rollFn producing value outside all ranges → Error |
| Unit | Entry order (first match wins) | Overlapping ranges, first entry with match |
| Unit | Default rollFn | Verify delegates to DiceRoller (integration check) |
| Unit | History ordering | Newest first via append() |
| Unit | History cap | 12 appends → size maxSize, oldest evicted |
| Unit | History Success-typed | Only Success entries accepted by append |
| Instrumented | Screen renders | FilterChips displayed for each table |
| Instrumented | Select + roll | Tap chip, tap Roll → result displayed |
| Instrumented | History accumulation | 3 rolls → 3 history items |
| Instrumented | Switch table | History preserved across table switches |

`./gradlew test` for unit + `./gradlew connectedAndroidTest` for instrumented.

## Migration / Rollout

Additive only — files can be committed in any order. No migration. No feature flags.

## Open Questions

None — spec and proposal are consistent enough to proceed.
