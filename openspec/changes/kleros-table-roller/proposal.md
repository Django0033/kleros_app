# Proposal: TableRoller + TableScreen

## Intent

8 upcoming RPG oracles (faction names, plot hooks, NPC traits) each need roller + screen. Factor the common engine + composable so each oracle is just data (table entries + optional interpreter).

## Scope

### In Scope

- `TableRoller` engine: 3 entry types — RANGE, DIRECT, RANGE_MODIFIER
- `TableEntry` sealed hierarchy, injectable `rollFn`, defaults to `DiceRoller.roll(D20)`
- `TableScreen` composable: FilterChip selector, roll button, result display, capped history
- Reuses existing `DiceRoller` — no duplicate RNG
- Unit tests (TableRoller) + Compose UI tests (TableScreen)

### Out of Scope

- The 8 specific oracles (future SDD changes)
- NameGenerator refactor (too specialized)
- New Gradle deps, history persistence

## Capabilities

### New

- `table-roller`: Generic table-driven result engine. Covers 3 entry types, injectable roll function, sealed result model.

### Modified

- None.

## Approach

Package `com.kleros.table`, two layers:

**Domain** — `TableRoller.roll(table, rollFn)` returns `TableRollResult`. `TableEntry` sealed class (RANGE | DIRECT | RANGE_MODIFIER). `TableDef` groups name + entries. `TableHistory` capped list (newest first). Android-free.

**UI** — `TableScreen(tables: List<TableDef>)` composable. `remember`/`mutableStateOf` for selected table, result, history. FilterChip row + roll button. No ViewModel.

`rollFn` is `() -> Int`, default `{ DiceRoller.roll(DiceType.D20) }`. Tests inject deterministic lambda.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `com/kleros/table/TableEntry.kt` | New | Sealed class: RANGE, DIRECT, RANGE_MODIFIER |
| `com/kleros/table/TableDef.kt` | New | Name + entries group |
| `com/kleros/table/TableRoller.kt` | New | Pure `roll()` dispatching by type |
| `com/kleros/table/TableRollResult.kt` | New | Result data class |
| `com/kleros/table/TableHistory.kt` | New | Capped list, newest first |
| `com/kleros/table/TableScreen.kt` | New | Generic composable |
| `test/…/table/TableRollerTest.kt` | New | 3 types, edge cases, rollFn injection |
| `test/…/table/TableHistoryTest.kt` | New | Max size, ordering, eviction |
| `androidTest/…/table/TableScreenTest.kt` | New | Selection, roll, history |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| TableEntry too rigid for future oracles | Low | Entries are data carriers; interpreters are optional |
| TableScreen overfitted | Med | Keep params generic; styling deferred to each oracle |
| detekt/ktlint blocks | Med | Run lint before PR; existing baseline applies |

## Rollback Plan

Revert all `com/kleros/table/` files. Additive only — no existing code changed, no migration.

## Dependencies

- `DiceRoller` (`com.kleros.dice`) — no code change needed
- No new Gradle dependencies

## Success Criteria

- [ ] `./gradlew test` passes
- [ ] `./gradlew detekt` passes with zero new issues
- [ ] All 3 entry types resolve correctly with deterministic rollFn
- [ ] RANGE_MODIFIER applies modifier after range roll
- [ ] TableHistory caps at 10, newest first
- [ ] TableScreen renders FilterChips per table, roll updates result
