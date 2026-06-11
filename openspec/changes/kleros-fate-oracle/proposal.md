# Proposal: Fate Oracle

## Intent

Yes/No oracle for solo RPG — pick odds level (Impossible → Certain), roll 1d100, get fate answer. No text input. Doubles trigger random event hint.

## Scope

### In Scope
- 9 odds levels via FilterChip (reuses DiceScreen/TableScreen pattern)
- 1d100 → FATE_CHART → Exceptional Yes / Yes / No / Exceptional No
- Double detection (11, 22...) with "Random Event" indicator
- Roll history capped at 10, newest-first
- Pure domain layer, zero Android deps
- Screen wired into MainActivity drawer

### Out of Scope
- Text input / custom modifiers
- Inline meaning rolls (user navigates to Meaning screen)
- User-customizable odds
- Animated transitions beyond spring effect

## Capabilities

### New Capabilities
- `fate-oracle`: Yes/No oracle with 9 odds levels, fate chart resolution, doubling detection

### Modified Capabilities
None — additive feature consuming `DiceRoller.roll(DiceType.D100)`.

## Approach
1. **FateData.kt** — `enum class OddsLevel` (9 constants) + `FATE_CHART` mapping each level to 4 IntRange thresholds (exno, no, yes, exyes)
2. **FateRoller.kt** — pure `roll(odds): FateResult`, calls `DiceRoller.roll(D100)`, resolves chart, checks double
3. **FateResult.kt** — sealed class: `ExceptionalYes`, `Yes`, `No`, `ExceptionalNo` + `isDouble` flag
4. **FateHistory.kt** — immutable capped list (max 10), newest-first
5. **FateScreen.kt** — FilterChip row + Roll button + result (with double badge) + history. State via `remember`, no ViewModel
6. **MainActivity.kt** — add `Screen.FATE_ORACLE`, wire `when` branch

## Affected Areas

| Area | Impact |
|------|--------|
| `app/.../fate/FateData.kt` | New |
| `app/.../fate/FateRoller.kt` | New |
| `app/.../fate/FateResult.kt` | New |
| `app/.../fate/FateHistory.kt` | New |
| `app/.../fate/FateScreen.kt` | New |
| `app/.../test/.../fate/` (4 test files) | New |
| `app/.../androidTest/.../fate/FateScreenTest.kt` | New |
| `app/.../MainActivity.kt` | Modified |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Chart thresholds off-by-one | Low | Unit test every (odds, roll) for expected result |
| Double detection on 100 | Low | Unit test 11..99 and non-doubles |

## Rollback Plan

Revert `MainActivity` Screen entry. Remove `com.kleros.fate` package. No existing feature depends on fate-oracle.

## Dependencies

- `DiceType.D100` / `DiceRoller` from dice-roll (existing)

## Success Criteria

- [ ] All 9 odds levels produce correct result for every roll 1–100
- [ ] Doubles detected and surfaced
- [ ] History caps at 10 newest-first
- [ ] All tests pass, no new Gradle deps
