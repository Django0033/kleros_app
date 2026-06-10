# Proposal: Adventure Crafter — Plot Action Tables

## Intent

Add 5 1d100 plot prompt tables (Action, Tension, Mystery, Social, Personal) for story inspiration. Pure data addition — no new infrastructure. Follows MeaningData/MeaningScreen pattern exactly. Each table uses 100 `TableEntry.DIRECT` entries (one per index 1–100), unlike Meaning's 50 RANGE entries.

## Scope

### In Scope

- `AdventureCrafterData` object with 5 × 100-entry TableDefs
- `AdventureCrafterScreen` composable wrapping TableScreen with D100
- Wire into MainActivity Screen enum + AppNavigation (new drawer item)
- Unit tests: DIRECT coverage 1–100 per table, first/last entry bounds
- Compose UI test: screen renders all 5 FilterChips

### Out of Scope

- Editable / user-defined tables
- Weighted rolls or non-uniform distributions
- Cross-table combination or "plot generation" beyond single-roll lookup
- Meaning or Character Crafter integration

## Capabilities

### New Capabilities

- `adventure-crafter`: 5 plot prompt tables (Action, Tension, Mystery, Social, Personal), 1d100 DIRECT, D100 dice type

### Modified Capabilities

None — `table-roller`, `TableScreen`, `DiceType.D100` already support this pattern unchanged.

## Approach

1. **AdventureCrafterData.kt**: 5 `TableDef` constants in `com.kleros.adventure` package; each has 100 `TableEntry.DIRECT(index, result)`, one per integer 1–100
2. **AdventureCrafterScreen.kt**: thin `@Composable` calling `TableScreen(tables = adventureCrafterData, diceType = DiceType.D100, title = "Adventure Crafter")`
3. **MainActivity.kt**: add `ADVENTURE_CRAFTER("Adv Craft", Icons.Filled.AutoAwesome)` to Screen enum; wire `when` branch; import AdventureCrafterScreen
4. Tests follow existing data-integrity + compose UI patterns

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `app/.../adventure/AdventureCrafterData.kt` | New | 5 TableDefs × 100 DIRECT entries |
| `app/.../adventure/AdventureCrafterScreen.kt` | New | Thin TableScreen wrapper |
| `app/.../MainActivity.kt` | Modified | Add Screen + nav item + when branch |
| `app/.../test/.../AdventureCrafterDataTest.kt` | New | DIRECT 1–100 coverage test |
| `app/.../androidTest/.../AdventureCrafterScreenTest.kt` | New | UI smoke test (FilterChips) |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Manual entry errors on 500 DIRECT values | Med | Data-integrity test verifies index sequence 1–100 with no gaps |

## Rollback Plan

Remove `ADVENTURE_CRAFTER` from Screen enum, delete adventure package, revert test files. Pure additive change — no breakage if undone first.

## Dependencies

None. Reuses `TableScreen`, `DiceType.D100`, `TableEntry.DIRECT`, `TableRoller`.

## Success Criteria

- [ ] AdventureCrafterScreen renders 5 FilterChips (Action, Tension, Mystery, Social, Personal)
- [ ] Rolling on any table returns the correct entry for that DIRECT index
- [ ] Each table has exactly 100 entries covering indices 1–100 (no gaps, no overlaps)
- [ ] All tests pass: `./gradlew test` + `./gradlew connectedCheck`
