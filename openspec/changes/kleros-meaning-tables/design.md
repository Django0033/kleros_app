# Design: Meaning Tables + TableScreen diceType Fix

## Technical Approach

Two isolated changes: (1) add optional `diceType: DiceType = DiceType.D20` to `TableScreen` and use it in the roll lambda instead of the hardcoded `DiceRoller.roll(DiceType.D20)`; (2) create `com.kleros.meaning` package with 50-entry 1d100 word tables (Action + Description), a thin `MeaningScreen` wrapper, and navigation wiring. The spec at `openspec/specs/table-roller/spec.md` gets an additional NFR documenting the new parameter.

## Architecture Decisions

### Decision: diceType as TableScreen param vs overloaded composable

| Option | Tradeoff |
|--------|----------|
| `diceType: DiceType = DiceType.D20` param | One composable; default preserves backward compat; callers like NameScreen unchanged |
| Separate `TableScreenD100` overload | Duplicates composable body; diverges over time |

**Decision**: Add `diceType: DiceType = DiceType.D20` to `TableScreen`'s constructor. The roll lambda becomes `{ DiceRoller.roll(diceType) }` — `diceType` from state, default `D20` matches existing behavior exactly.

### Decision: MeaningData as object vs file-level constants

**Choice**: `object MeaningData { val tables: List<TableDef> = listOf(...) }` inside `MeaningData.kt`
**Rationale**: Namespaced access (`MeaningData.tables`), follows Kotlin convention for constant collections, avoids top-level function-style declarations. The two `TableDef` values are internal; only `tables` is public.

### Decision: MeaningScreen as own file vs inline in MainActivity

**Choice**: Separate `MeaningScreen.kt` with `@Composable fun MeaningScreen(modifier: Modifier = Modifier)`
**Rationale**: Testable independently via `createComposeRule`, follows composable-per-file pattern of TableScreen and NameScreen, no import overhead in MainActivity.

### Decision: Word table data selection

**Choice**: Two 50-entry tables (Action: verbs, Description: adjectives), each entry `RANGE(n, n+1, "Word")` covering rolls 1–100 with no gaps or overlaps.
**Rationale**: 2-point ranges are the most compact RANGE encoding for 50 entries over a 100-face die. The data integrity test validates full coverage. Words are generic TTRPG scene inspiration tokens.

## Data Flow

```
User taps "Meaning" FilterChip in AppNavigation bar
  → currentScreen = Screen.MEANING
  → MeaningScreen renders:
      TableScreen(tables = MeaningData.tables, diceType = DiceType.D100)

User taps FilterChip (Action / Description)
  → selectedTableIndex updated in TableScreen local state

User taps "Roll"
  → DiceRoller.roll(DiceType.D100) → raw value (1–100)
  → TableRoller.roll(tableDef, rollFn) → match against 50 RANGE entries
  → Success(word) or Error
  → Display result + append to history
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `app/.../table/TableScreen.kt` | Modify | Add `diceType: DiceType = DiceType.D20` param; change onClick lambda to `{ DiceRoller.roll(diceType) }` |
| `app/.../meaning/MeaningData.kt` | Create | `object MeaningData` with `val tables: List<TableDef>` — Action + Description, each 50 `TableEntry.RANGE` entries |
| `app/.../meaning/MeaningScreen.kt` | Create | `@Composable fun MeaningScreen(modifier)` → calls `TableScreen(tables = MeaningData.tables, diceType = DiceType.D100)` |
| `app/.../MainActivity.kt` | Modify | Add `MEANING("Meaning")` to `Screen` enum; add `Screen.MEANING -> MeaningScreen()` branch |
| `openspec/specs/table-roller/spec.md` | Modify | Add NFR: "TableScreen SHALL accept an optional `diceType` parameter defaulting to `DiceType.D20`" |
| `app/src/test/.../meaning/MeaningDataTest.kt` | Create | Data integrity: 50 entries, all 2-point, full 1–100 coverage, no overlaps |
| `app/src/androidTest/.../meaning/MeaningScreenTest.kt` | Create | Smoke test: renders both FilterChips, roll button produces result |

Full test paths:
- `app/src/test/java/com/kleros/meaning/MeaningDataTest.kt`
- `app/src/androidTest/java/com/kleros/meaning/MeaningScreenTest.kt`

## Interfaces / Contracts

```kotlin
// Modified: com.kleros.table
@Composable
fun TableScreen(
    tables: List<TableDef>,
    modifier: Modifier = Modifier,
    title: String = "",
    diceType: DiceType = DiceType.D20,            // NEW
)

// New: com.kleros.meaning
object MeaningData {
    val tables: List<TableDef>     // Action (verbs) + Description (adjectives)
}

@Composable
fun MeaningScreen(modifier: Modifier = Modifier)  // calls TableScreen with D100
```

Each `TableEntry.RANGE` entry: `RANGE(min = n, max = n + 1, result = "Word")` for `n in 1..99 step 2`.

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | Data integrity: count | Assert `MeaningData.tables` has exactly 2 entries |
| Unit | Data integrity: Action entries | Assert 50 entries, all `RANGE`, each `max - min == 1` |
| Unit | Data integrity: Description entries | Assert 50 entries, all `RANGE`, each `max - min == 1` |
| Unit | Data integrity: coverage | Assert union of entry ranges is exactly `1..100` for each table |
| Unit | Data integrity: no gaps | Assert `entries[i].max + 1 == entries[i+1].min` for sorted entries |
| Instrumented | MeaningScreen renders | Assert `tableSelector_Action` and `tableSelector_Description` displayed |
| Instrumented | Roll produces result | Assert `rollResult` testTag visible after tap |

Run: `./gradlew test` for unit, `./gradlew connectedAndroidTest` for instrumented.

## Migration / Rollout

No migration. `diceType` is additive with a default; existing callers (NameScreen) are unaffected. Rollback: revert TableScreen.kt change, remove `Screen.MEANING` and `com.kleros.meaning` package.

## Open Questions

None — proposal and spec provide sufficient clarity.
