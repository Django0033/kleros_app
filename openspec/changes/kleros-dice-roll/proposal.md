# Proposal: Kleros Dice Roll

## Intent

Users need a quick, fair random number generator for on-chain governance tie-breakers, juror games, or gamification. Replaces the template "Hello Android!" greeting with a dice-rolling screen.

## Scope

### In Scope
- Dice type selector: D4, D6, D8, D10, D12, D20, D100
- Pure-function roller returning correct-range values
- `animateIntAsState` scaling animation on result
- Roll history (capped at 10, newest first)
- Unit tests (DiceRoller, RollHistory) + Compose UI tests (DiceScreen)
- Replace `Greeting()` with `DiceScreen()` in MainActivity

### Out of Scope
- 3D/physics dice animation, sound/haptics, ViewModel layer, history persistence, accessibility announcements

## Capabilities

### New
- `dice-roll`: Random number generation for tabletop dice (D4–D100). Covers selection, roll, result animation, and rolling history.

### Modified
- None (first feature on fresh template).

## Approach

Pure-function `DiceRoller.roll(DiceType): Int` keeps business logic testable without Android deps. `RollHistory` is a list-backed data class with max-size invariant. `DiceScreen` uses `remember`/`mutableStateOf` — no ViewModel for MVP. Result animates via `animateIntAsState` (spring). Package: `com.kleros.dice`.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `MainActivity.kt` | Modified | `Greeting()` → `DiceScreen()` |
| `dice/DiceType.kt` | New | Enum with face counts |
| `dice/DiceRollResult.kt` | New | Data class: diceType, value, timestamp |
| `dice/DiceRoller.kt` | New | `roll(DiceType): Int` |
| `dice/RollHistory.kt` | New | Max-size list of results |
| `dice/DiceScreen.kt` | New | Selector + button + result + history |
| `test/…/DiceRollerTest.kt` | New | Range validation per dice type |
| `test/…/RollHistoryTest.kt` | New | Max size, ordering |
| `androidTest/…/DiceScreenTest.kt` | New | Selection, roll, history assertions |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| UI test flakiness from animation | Low | `animateIntAsState` is deterministic; use `waitForIdle` |
| detekt/ktlint blocks new code | Med | Run lint before PR; baseline already configured |
| JaCoCo threshold misses gaps | Low | Manual review of test assertions in verify phase |

## Rollback Plan

Revert all `com/kleros/dice/` files and restore `Greeting()` in MainActivity. Additive only — no migration, no schema change.

## Dependencies

None. Template already includes Compose UI, Material3, JUnit 4, and Compose UI Test. No new Gradle deps.

## Success Criteria

- [ ] `./gradlew test` passes (unit + UI tests green)
- [ ] `./gradlew detekt` passes with zero new issues
- [ ] All 7 dice types produce documented ranges over 1000 rolls each
- [ ] History caps at 10, newest first
- [ ] DiceScreen replaces template greeting in running app
