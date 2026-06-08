# Proposal: Character Crafter

## Intent

GMs need quick NPCs for improv sessions. Random name generation exists but lacks descriptors (who they are, how they think, what they look like, what they're good at) and statistics (relative ability tiers). Character Crafter fills this gap with a single-tap NPC generator reusing the existing dice infrastructure.

## Scope

### In Scope
- New `com.kleros.character` package with 6 files (following NameGenerator pattern)
- Custom `CharacterScreen` composable (not TableScreen — 4 independent 1d100 rolls + stat table)
- 100 shared descriptor entries for 4 slots: Identity, Mind, Body, Talent
- 1d10-driven 5-tier statistics table (50% lower / 25% lower / expected / 25% higher / 50% higher)
- "Generate Character" button → formatted result card per NPC
- Immutable history (last 10) ordered newest-first
- Unit tests + Compose UI test (strict TDD)

### Out of Scope
- Editable descriptor pool (static only)
- Weighted / non-uniform descriptor selection
- Stat customization or point-buy
- Multi-NPC batch generation
- Animated transitions or custom theming

## Capabilities

### New Capabilities
- `character-crafter`: Quick NPC generation — 4 descriptor slots (d100 each) + 1d10 statistics table + history

### Modified Capabilities
- None

## Approach

1. **CharacterData.kt**: 100-entry shared word list as compile-time constant (`@file:Suppress("MagicNumber")`)
2. **CharacterGenerator.kt**: pure object with `generate(rollFn)` — 4x `DiceRoller.roll(D100)` + statistics via `DiceRoller.roll(D10)`
3. **CharacterResult.kt**: data class holding descriptors map, statistics map, timestamp
4. **CharacterHistory.kt**: immutable list, max 10, newest-first (same pattern as NameHistory)
5. **CharacterScreen.kt**: composable with "Generate Character" button, result card (Identity/Mind/Body/Talent + stats), history list
6. **MainActivity.kt**: add `CHARACTER("Character")` to Screen enum + navigation branch

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `openspec/specs/character-crafter/spec.md` | New | Full spec |
| `app/.../character/CharacterData.kt` | New | 100 shared descriptor entries |
| `app/.../character/CharacterGenerator.kt` | New | Generation logic |
| `app/.../character/CharacterResult.kt` | New | Result data class |
| `app/.../character/CharacterHistory.kt` | New | History container |
| `app/.../character/CharacterScreen.kt` | New | Composable screen |
| `app/.../MainActivity.kt` | Modified | Add Screen.CHARACTER |
| `app/.../test/.../character/` | New | Unit tests |
| `app/.../androidTest/.../character/` | New | UI test |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Statistics table needs balanced 5-tier distribution | Low | Exact 1d10 → tier mapping defined in spec |
| Descriptor pool of 100 allows same word in multiple slots | Low | Intentional — spec documents this behavior |

## Rollback Plan

Remove `Screen.CHARACTER` from MainActivity and delete the `com.kleros.character` package. Additive only — zero breakage to existing features.

## Dependencies

None — reuses `DiceRoller.roll(DiceType.D100)` and `DiceRoller.roll(DiceType.D10)`.

## Success Criteria

- [ ] CharacterScreen renders with "Generate Character" button
- [ ] Tapping "Generate" shows 4 descriptors + statistics card
- [ ] Descriptors draw from the 100-entry shared pool independently
- [ ] Statistics use 1d10 → 5-tier mapping correctly
- [ ] History caps at 10 entries, newest first
- [ ] Unit tests pass (generator, data, history)
- [ ] Compose UI test passes (screen smoke test)
- [ ] No new Gradle dependencies
