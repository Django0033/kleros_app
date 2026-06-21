# Proposal: Location Crafter — Procedural Location Generation with PP Progression

## Intent

GMs need quick location prompts (not just NPCs/creatures) for improvised exploration. Location Crafter adds procedural location generation with a stateful PP (progress points) system — each area generated consumes and advances PP, making later areas richer in element variety.

## Scope

### In Scope
- `com.kleros.location` package (6 files, mirrors Character/Creature Crafter)
- `LocationData`: 100 descriptors (`DIRECT`, 1d100) + 10 element ranges (`RANGE`, 2d10+PP lookup)
- `LocationCrafter.generate()`: 1 descriptor (d100) + 3 elements (2d10+PP each) → PP++
- Region size selector: Small (3 PP), Average (5 PP), Large (7 PP)
- `LocationScreen` composable: region selector, PP counter, generate button, result card, history
- PP state persists across generations within session
- Immutable history (10, newest-first)
- Navigation: `Screen.LOCATION_CRAFTER` + material icon
- Unit tests + Compose UI test (strict TDD)

### Out of Scope
- Editable descriptor/element pool
- Weighted rolls or custom PP rules
- Batch generation (single area at a time)
- Animated transitions or custom theming
- Save/load PP state across app restarts

## Capabilities

### New Capabilities
- `location-crafter`: Location generation — 1 descriptor (d100) + 3 elements (2d10+PP) per area, stateful PP progression with region size selection

### Modified Capabilities
- None

## Approach

1. **LocationData.kt**: 100 `DIRECT` descriptors (1..100) + 10 `RANGE` element entries covering the 2d10+PP span
2. **LocationCrafter.kt**: pure object with `generate(regionSize, rollFn)` — uses `DiceRoller.roll(D10) × 2` for 2d10, adds current PP, looks up descriptor via direct index + 3 element range lookups; returns result + incremented PP
3. **LocationResult.kt**: data class with `descriptor`, `elements: List<String>`, `ppUsed: Int`, `timestampMillis`
4. **LocationHistory.kt**: immutable capped list (10, newest-first) — same pattern as CharacterHistory
5. **LocationScreen.kt**: composable with region size selector (3 chips), live PP counter, generate button, result card (descriptor + 3 elements + PP used), history list
6. Navigation: `Screen.LOCATION_CRAFTER` enum entry + `when` branch in `MainActivity.kt`

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `app/.../location/LocationData.kt` | New | 100 DIRECT + 10 RANGE entries |
| `app/.../location/LocationCrafter.kt` | New | Generator with PP progression |
| `app/.../location/LocationResult.kt` | New | Result data class |
| `app/.../location/LocationHistory.kt` | New | Capped history (10) |
| `app/.../location/LocationScreen.kt` | New | Composable with PP state |
| `app/src/main/.../MainActivity.kt` | Modified | Screen enum + nav branch + icon |
| `app/src/test/.../location/` | New | Unit tests (generator, data, history, PP logic) |
| `app/src/androidTest/.../location/` | New | Compose UI test |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| 2d10+PP may exceed element RANGE coverage as PP climbs | Low | Ranges defined across full 2..30+ span; each entry covers a segment; roll always hits one |
| Region size change mid-session resets PP | Low | Intentional — documented behavior; changing selector resets to new starting value |

## Rollback Plan

Remove `Screen.LOCATION_CRAFTER` from `MainActivity.kt` and delete `com.kleros.location` package. Additive only — zero breakage to existing features.

## Dependencies

None. Reuses `DiceRoller.roll(DiceType.D10)`, `TableEntry.DIRECT`, `TableEntry.RANGE`. No new Gradle dependencies.

## Success Criteria

- [ ] LocationScreen renders region size selector (Small/Average/Large), PP counter, generate button
- [ ] Selecting Small/Average/Large sets starting PP to 3/5/7
- [ ] Generate produces exactly 1 descriptor + 3 elements
- [ ] PP increments by 1 after each generation
- [ ] Changing region size resets PP to the new starting value
- [ ] History caps at 10 entries, newest-first
- [ ] Unit tests pass (generator, data, history, PP progression)
- [ ] Compose UI test passes (screen smoke)
- [ ] No new Gradle dependencies
