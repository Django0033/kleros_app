# Tasks: Fate Oracle

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~250 (140 prod + 110 test) |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | single-pr |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

**TDD**: Strict RED → GREEN per layer. Every test task MUST compile and fail before its corresponding implementation task.

## Phase 1: Data Layer — FateData

- [x] 1.1 **Write `FateDataTest`** (RED) — assert 9 `OddsLevel` entries exist; each has valid thresholds (exYesMax ≤ yesMax ≤ noMax ≤ 100). Run `./gradlew test` to confirm RED.
- [x] 1.2 **Create `FateData.kt`** (GREEN) — `enum class OddsLevel(label, exYesMax, yesMax, noMax)` with 9 entries (Impossible → Certain). Run tests to GREEN.

## Phase 2: Model Layer — FateResult + FateHistory

- [x] 2.1 **Write `FateResultTest` + `FateHistoryTest`** (RED) — verify sealed variant roll/oddsLevel/isDouble/timestamp field assignment; verify append ordering newest-first, cap at 10, immutability on overflow. Run tests to confirm RED.
- [x] 2.2 **Create `FateResult.kt` + `FateHistory.kt`** (GREEN) — sealed class `FateResult` with 4 data class variants (ExceptionalYes, Yes, No, ExceptionalNo); `data class FateHistory` with `append()` returning new instance capped at 10, newest-first. Run tests to GREEN.

## Phase 3: Generator Layer — FateRoller

- [x] 3.1 **Write `FateRollerTest`** (RED) — deterministic rollFn: all 4 outcomes per odds level; boundary values (1, exYesMax, exYesMax+1, yesMax, yesMax+1, noMax, noMax+1, 100); double detection (11,22,33,44,55,66,77,88,99,100 → true; 1,10,12,20 → false). Run tests to confirm RED.
- [x] 3.2 **Create `FateRoller.kt`** (GREEN) — `object FateRoller` with `fun roll(oddsLevel, rollFn): FateResult` — resolve threshold, detect double via `roll % 11 == 0 || roll == 100`. Run tests to GREEN.

## Phase 4: Screen Layer — FateScreen

- [x] 4.1 **Write `FateScreenTest`** (RED, Compose UI) — FilterChip row renders 9 chips (`"oddsChip_${level.name}"`); roll button exists (`"rollButton"`); after tap result text appears (`"resultText"`); event badge conditional on double (`"eventBadge"`); history list accumulates (`"historyList"`). Run `./gradlew compileDebugAndroidTestKotlin` to confirm RED.
- [x] 4.2 **Create `FateScreen.kt`** (GREEN) — `@Composable fun FateScreen(modifier)` with title "Fate Oracle", FilterChip row (9 odds), `FilledTonalButton("Roll Fate")`, result card with double badge, `HistorySection` (capped 10, newest-first). Uses `remember`/`mutableStateOf`. Run UI test to GREEN.

## Phase 5: Navigation — MainActivity Wiring

- [x] 5.1 **Add `FATE_ORACLE("Fate")`** to `Screen` enum + `when` branch → `FateScreen()` in `MainActivity.kt`. Verify build with `./gradlew assembleDebug`.

## Phase 6: Lint + Full Verification

- [x] 6.1 **Run `./gradlew check`** — fix any lint or type errors. Run full test suite (`./gradlew test` + `./gradlew compileDebugAndroidTestKotlin`). Verify all new test files pass.
