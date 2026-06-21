# Proposal: Mystery Crafter — Accumulator-Based Discovery

## Intent

GMs need a progressive discovery mechanic for mysteries, investigations, and uncovering secrets. Existing crafters use flat d100 rolls; this one introduces an **accumulator** (boxes that stack per roll, cap 20) so each attempt gets closer to the truth. Definitive answer at 101+.

## Scope

### In Scope
- `com.kleros.mystery` package (5 files, follows CharacterCrafter structure)
- Custom `MysteryScreen` composable (not TableScreen — accumulator is DIY, not via TableRoller)
- `MysteryData.kt`: 7 RANGE discovery entries for 1d100+boxes lookup + 100 DIRECT flavor descriptors
- `MysteryCrafter.check(rollFn, boxes)`: rolls 1d100, adds boxes, if > 100 → definitive, else → RANGE lookup. Also rolls a descriptor.
- `MysteryResult`: `discovery: String`, `descriptor: String`, `isDefinitive: Boolean`, `boxes: Int`, `timestampMillis`
- Boxes start at 0, ++ per check, cap at 20 (state held in screen composable)
- History (10 entries, newest-first) per existing pattern
- Navigation: `Screen.MYSTERY_CRAFTER("Mys Craft", icon)` + `when` branch
- Strict TDD: unit tests + Compose UI test

### Out of Scope
- Editable discovery/descriptor pools
- Weighted descriptor selection
- Multi-mystery parallel sessions
- Animated transitions or custom theming

## Capabilities

### New Capabilities
- `mystery-crafter`: Accumulator-based discovery — progressive roll (1d100 + boxes, cap 20), definitive answer at 101+, 7-tier RANGE discovery table + 100 DIRECT descriptors, capped history

### Modified Capabilities
- None

## Approach

1. **MysteryData.kt**: 7 `RANGE` entries covering 1..100 (discovery tiers) + 100 `DIRECT` flavor descriptors
2. **MysteryCrafter.kt**: `object.check(rollFn, boxes: Int)` — DIY lookup (boxes added inline, no TableRoller). Returns definitive if `roll + boxes > 100`, else RANGE match. Always rolls one descriptor.
3. **MysteryResult.kt**: `data class` with discovery, descriptor, isDefinitive, boxes, timestamp
4. **MysteryScreen.kt**: Composable — current boxes display, "Investigate" button, result card (discovery + descriptor), history list
5. Navigation: `Screen` enum entry + icon + `when` branch in `MainActivity.kt`

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `openspec/specs/mystery-crafter/spec.md` | New | Full spec |
| `app/.../mystery/MysteryData.kt` | New | 7 RANGE discovery + 100 DIRECT descriptors |
| `app/.../mystery/MysteryCrafter.kt` | New | DIY accumulator lookup + generate |
| `app/.../mystery/MysteryResult.kt` | New | Data class |
| `app/.../mystery/MysteryScreen.kt` | New | Composable with accumulator UI |
| `app/src/main/.../MainActivity.kt` | Modified | Screen enum + nav branch + icon |
| `app/src/test/.../mystery/` | New | Unit tests |
| `app/src/androidTest/.../mystery/` | New | Compose UI test |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| TableRoller doesn't support accumulator | Certain | DIY lookup in MysteryCrafter — spec explicitly avoids TableRoller |
| Box accumulation requires mutable screen state | Low | `mutableStateOf<Int>` holds boxes; check() is pure, screen updates state |
| Overflow at 101+ skips RANGE lookup | Intended | isDefinitive flag vs discovery string — both present in result |

## Rollback Plan

Remove `Screen.MYSTERY_CRAFTER` from `MainActivity.kt` and delete `com.kleros.mystery` package. Additive only — zero breakage.

## Dependencies

None — reuses `DiceRoller.roll(DiceType.D100)`, `TableEntry.RANGE`, `TableEntry.DIRECT`. No new Gradle dependencies.

## Success Criteria

- [ ] `MysteryCrafter.check(rollFn, 0)` rolls 1d100, produces valid discovery or definitive
- [ ] Roll + boxes > 100 → `isDefinitive == true`
- [ ] Roll + boxes <= 100 → matches one of 7 RANGE entries
- [ ] Descriptor always drawn from 100-entry DIRECT pool
- [ ] Boxes increment each check, cap at 20
- [ ] History caps at 10 entries, newest-first
- [ ] All unit tests pass
- [ ] Compose UI test passes (screen smoke + accumulator mechanic)
- [ ] No new Gradle dependencies
