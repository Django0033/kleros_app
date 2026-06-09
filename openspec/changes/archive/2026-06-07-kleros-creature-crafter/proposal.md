# Proposal: Creature Crafter — Random Creature Generation

## Intent

GMs need quick creature stats for improvised encounters, extending the NPC-generation pattern (Character Crafter) to non-human entities. Adds creature-specific descriptor/ability tables, behavior rolls, and post-gen mutation buttons for on-the-fly refinement.

## Scope

### In Scope
- `com.kleros.creature` package (6 files, mirrors Character Crafter structure)
- `CreatureData.kt`: 100 weighted descriptors, 100 paired abilities (50×2), 10 behavior_initial, 10 behavior_new — all `TableEntry.DIRECT`
- `CreatureCrafter.generate()`: 2×d100 descriptors + 2×d100 abilities + 1×d10 initial behavior + 1×d10 statistics
- `CreatureResult`: `descriptors: List<String>`, `abilities: List<String>`, `initialBehavior: String`, `statistics: String`, `newBehavior: String? = null`
- Post-gen mutations: "Roll Descriptor" (+1 entry to descriptors), "Roll Ability" (+1 entry to abilities), "New Behavior" (rolls behavior_new, sets `newBehavior`)
- Statistics reused from `CharacterData.statistics`
- `CreatureScreen` composable: Generate button, result card (descriptors + abilities + behavior + stats), mutation buttons, capped immutable history (10, newest-first)
- Navigation: `Screen.CREATURE_CRAFTER("Creature", Icons.Filled.BugReport)` + `when` branch
- Strict TDD: unit tests + Compose UI test

### Out of Scope
- Weighted-probability tuning (raw table data as-is)
- Editable descriptor/ability pool
- Stat blocks beyond behavior + statistics
- Animated transitions or custom theming

## Capabilities

### New Capabilities
- `creature-crafter`: Creature generation — 2 descriptors (d100), 2 abilities (d100), initial behavior (d10), statistics (d10), with post-gen Roll Descriptor, Roll Ability, New Behavior mutations

### Modified Capabilities
- None

## Approach

1. **CreatureData.kt**: 4 compile-time constant tables — descriptor (100 weighted `DIRECT`), ability (100 = 50 paired `DIRECT`), behavior_initial (10), behavior_new (10)
2. **CreatureCrafter.kt**: `object.generate(rollFn)` — 2 descriptor rolls, 2 ability rolls, 1 behavior roll, 1 stat lookup; plus `rollDescriptor()`, `rollAbility()`, `rollNewBehavior()` for post-gen
3. **CreatureResult.kt**: `data class` with `List<String>` for descriptors/abilities, nullable `newBehavior`
4. **CreatureScreen.kt**: Composable with Generate button, result card, 3 mutation buttons, history list
5. **CreatureHistory.kt**: Immutable capped history (10, newest-first)
6. Navigation: `Screen` enum entry + icon + `when` branch in `MainActivity.kt`

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `app/.../creature/CreatureData.kt` | New | 4 tables: descriptors(100), abilities(100), behavior_initial(10), behavior_new(10) |
| `app/.../creature/CreatureCrafter.kt` | New | Generator + 3 mutation methods |
| `app/.../creature/CreatureResult.kt` | New | Data class with list fields + nullable newBehavior |
| `app/.../creature/CreatureHistory.kt` | New | Immutable capped history |
| `app/.../creature/CreatureScreen.kt` | New | Composable with mutation buttons |
| `app/src/main/.../MainActivity.kt` | Modified | Screen enum + nav branch + icon |
| `app/src/test/.../creature/` | New | Unit tests |
| `app/src/androidTest/.../creature/` | New | Compose UI test |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Cross-pkg import of `CharacterData.statistics` | Low | Clean sibling import, no circular deps |
| Post-gen mutation requires mutable state model | Low | `mutableStateOf` holds current result; mutations create new data class copies |
| Descriptor/ability table correctness vs source | Low | User supplied corrected table data in design summary |

## Rollback Plan

Remove `Screen.CREATURE_CRAFTER` from `MainActivity.kt` and delete `com.kleros.creature` package. Additive only — zero breakage to existing features.

## Dependencies

None — reuses `DiceRoller.roll(DiceType)`, `CharacterData.statistics`, `TableEntry.DIRECT`. No new Gradle dependencies.

## Success Criteria

- [ ] `CreatureCrafter.generate()` returns result with exactly 2 descriptors, 2 abilities, 1 initialBehavior, 1 statistics, null newBehavior
- [ ] "Roll Descriptor" appends a 3rd descriptor (copies result, new list)
- [ ] "Roll Ability" appends a 3rd ability
- [ ] "New Behavior" sets newBehavior from behavior_new roll (no-op if already set — explicit GM choice)
- [ ] History caps at 10 entries, newest-first
- [ ] All unit tests pass (generator, data, history)
- [ ] Compose UI test passes (screen smoke + mutation buttons)
- [ ] No new Gradle dependencies
