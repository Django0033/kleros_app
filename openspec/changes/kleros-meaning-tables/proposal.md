# Proposal: Meaning Tables

## Intent

Port two 1d100 word tables (Action, Description) from opm.nvim for scene inspiration, reusing the existing table-roller engine. Currently TableScreen hardcodes DiceType.D20 — these tables need DiceType.D100 for their 50-entry 2-point-range layout.

## Scope

### In Scope

- Add `diceType: DiceType = DiceType.D20` param to `TableScreen`
- Create `com.kleros.meaning` package with MeaningData (two 1d100 tables)
- Create MeaningScreen composable wrapping TableScreen with D100
- Wire Meaning screen into MainActivity Screen enum + AppNavigation
- Unit tests: data integrity (50 entries / 2-point ranges / full 1–100 coverage)
- Compose UI test: smoke test for MeaningScreen rendering

### Out of Scope

- Additional word tables beyond the initial two
- Editable / customizable tables
- Weighted or non-uniform distributions
- Animated transitions or custom styling

## Capabilities

### New Capabilities

- `meaning-tables`: Scene inspiration word tables (Action + Description), 1d100 range-based

### Modified Capabilities

- `table-roller`: TableScreen gains optional `diceType` parameter (spec delta)

## Approach

1. **TableScreen**: add `diceType: DiceType = DiceType.D20` to signature; use it in the roll lambda instead of hardcoded `DiceType.D20`
2. **MeaningData.kt**: two `TableDef` constants, 50 `TableEntry.RANGE(1,2,"Word")` entries each, covering rolls 1–100
3. **MeaningScreen.kt**: thin wrapper calling `TableScreen(tables = meaningTables, diceType = DiceType.D100)`
4. **MainActivity.kt**: add `MEANING("Meaning")` to Screen enum; wire `when` branch

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `openspec/specs/table-roller/spec.md` | Modified | Add diceType NFR |
| `app/.../table/TableScreen.kt` | Modified | Add diceType param |
| `app/.../meaning/MeaningData.kt` | New | Two 1d100 TableDefs |
| `app/.../meaning/MeaningScreen.kt` | New | Thin wrapper |
| `app/.../MainActivity.kt` | Modified | Add Screen.MEANING |
| `app/.../test/.../MeaningDataTest.kt` | New | Data integrity |
| `app/.../androidTest/.../MeaningScreenTest.kt` | New | UI smoke test |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| RANGE entries require precise 2-point coverage 1–100 | Low | Data integrity test verifies all entries |

## Rollback Plan

Revert `TableScreen` diceType change (additive with default — no breakage). Remove `Screen.MEANING` and the meaning package.

## Dependencies

None — reuses `DiceType.D100`, `TableRoller`, `TableScreen`.

## Success Criteria

- [ ] MeaningScreen renders both FilterChips (Action, Description)
- [ ] Rolling with D100 returns valid words from correct range
- [ ] All tests pass (unit + compose UI)
- [ ] No new Gradle dependencies
