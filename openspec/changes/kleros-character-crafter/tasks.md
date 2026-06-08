# Tasks: Character Crafter

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~350 (200 prod + 150 test) |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | single-pr |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

**TDD**: Strict RED → GREEN per layer. Every test task MUST compile and fail before its corresponding implementation task.

## Phase 1: Data Layer — CharacterData

- [ ] 1.1 **Write `CharacterDataTest`** (RED) — assert 100 descriptors accessible at indices 1-100, each non-empty; assert stat tier ranges cover 1-10 without gaps mapping to tiers 0-4. Run `./gradlew test` to confirm RED.
- [ ] 1.2 **Create `CharacterData.kt`** (GREEN) — `@file:Suppress("MagicNumber")`, `object CharacterData` with `descriptor(index: Int): String`, `statisticTier(roll: Int): StatTier`, and `StatTier(label: String, tier: Int)`. 100 shared `(Slot, String)` entries. Run tests to GREEN.

## Phase 2: Model Layer — CharacterResult + CharacterHistory

- [ ] 2.1 **Write `CharacterResultTest` + `CharacterHistoryTest`** (RED) — verify field assignment, timestamp auto-population, copy immutability; verify append ordering newest-first, cap at 10, immutability on overflow. Run tests to confirm RED.
- [ ] 2.2 **Create `CharacterResult.kt` + `CharacterHistory.kt`** (GREEN) — `data class CharacterResult(identity, mind, body, talent, statistics, timestampMillis)`; `data class CharacterHistory` with `append()` returning new instance capped at 10, newest-first. Run tests to GREEN.

## Phase 3: Generator Layer — CharacterCrafter

- [ ] 3.1 **Write `CharacterCrafterTest`** (RED) — deterministic rollFn produces expected descriptors + stat tier; duplicate descriptor across slots is allowed; stat boundary mapping (d10 1-2→tier0, 3-4→tier1, etc.); 1000 invocations produce variety. Run tests to confirm RED.
- [ ] 3.2 **Create `CharacterCrafter.kt`** (GREEN) — `object CharacterCrafter` with `generate(rollFn: (DiceType) -> Int = { DiceRoller.roll(it) }): CharacterResult` — 4× `rollFn(DiceType.D100)` for descriptors + 1× `rollFn(DiceType.D10)` for statistic tier. Run tests to GREEN.

## Phase 4: Screen Layer — CharacterScreen

- [ ] 4.1 **Write `CharacterScreenTest`** (RED, Compose UI) — button renders (`assertIsDisplayed` on `"generateCharacterButton"`); after tap, descriptor text visible; before tap, no result card. Run `./gradlew connectedCheck` to confirm RED.
- [ ] 4.2 **Create `CharacterScreen.kt`** (GREEN) — `@Composable fun CharacterScreen(modifier)` with title "Character Crafter", "Generate Character" `FilledTonalButton`, result `Card` (4 descriptor labels + statistics tier), `HistorySection` (capped 10, newest-first). Uses `remember`/`mutableStateOf`. Run UI test to GREEN.

## Phase 5: Navigation — MainActivity Wiring

- [ ] 5.1 **Add `CHARACTER("Character")`** to `Screen` enum + `when` branch → `CharacterScreen()` in `MainActivity.kt`. Verify build with `./gradlew assembleDebug`.

## Phase 6: Lint + Full Verification

- [ ] 6.1 **Run `./gradlew check`** — fix any lint or type errors. Run full test suite (`./gradlew test` + `./gradlew connectedCheck`). Verify all 6 new test files pass.
