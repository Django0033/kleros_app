# Tasks: Location Crafter

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~500 (320 prod + 180 test) |
| 400-line budget risk | Medium |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | single-pr |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Medium

**TDD**: Strict RED → GREEN per layer. Every test task MUST compile and fail before its corresponding implementation task.

## Phase 1: Data Layer — LocationData + RegionSize

- [ ] 1.1 **Write `LocationDataTest`** (RED) — assert 100 DIRECT entries at indices 1-100 (non-empty), assert exactly 10 RANGE entries covering 2..30+ without gaps. Run `./gradlew test` to confirm RED.
- [ ] 1.2 **Create `LocationData.kt`** (GREEN) — `@file:Suppress("MagicNumber")`, `object LocationData` with `val descriptors: List<TableEntry.DIRECT>` (100 entries, indices 1-100) and `val elements: List<TableEntry.RANGE>` (10 entries, spans 2-4, 5-9, 10-10, 11-14, 15-15, 16-16, 17-19, 20-20, 21-21, 22-30). Add `enum class RegionSize(val label: String, val startingPP: Int)` → `SMALL("Small", 3), AVERAGE("Average", 0), LARGE("Large", -3)`. Run tests to GREEN.

## Phase 2: Model Layer — LocationResult + LocationHistory

- [ ] 2.1 **Write `LocationResultTest` + `LocationHistoryTest`** (RED) — verify field assignment, timestamp auto-pop, copy immutability; verify append ordering newest-first, cap at 10, immutability on overflow, empty state. Run tests to confirm RED.
- [ ] 2.2 **Create `LocationResult.kt` + `LocationHistory.kt`** (GREEN) — `data class LocationResult(descriptor, elements: List<String>, ppUsed: Int, timestampMillis)`; `data class LocationHistory` with immutable `append()` capped at 10 newest-first. Run tests to GREEN.

## Phase 3: Generator Layer — LocationCrafter

- [ ] 3.1 **Write `LocationCrafterTest`** (RED) — deterministic rollFn: generate with AVERAGE returns descriptor from DIRECT index and 3 elements from RANGE; PP increments by 1 per call (PP=0→1→2 over 3 calls); SMALL starts at PP=3; PP resets when switching region size; `isComplete` returns true when PP pushes roll into 22-30 range. Run tests to confirm RED.
- [ ] 3.2 **Create `LocationCrafter.kt`** (GREEN) — `object LocationCrafter` with `generate(regionSize, rollFn: (DiceType) -> Int)` → rolls 1× d100 for descriptor lookup, 3× (2d10 + PP) for element lookups via RANGE, returns `LocationResult` + incremented PP. `rollDescriptor()` and `rollElement(pp)` private helpers. Uses `regionSize.startingPP` as initial PP via mutable state accumulator. Run tests to GREEN.

## Phase 4: Screen Layer — LocationScreen

- [ ] 4.1 **Write `LocationScreenTest`** (RED, Compose UI) — chips for Small/Average/Large render; PP counter shows 0 for default (Average); generate button renders; after tap, result card with descriptor + 3 elements visible. Run `./gradlew compileDebugAndroidTestKotlin` to confirm RED.
- [ ] 4.2 **Create `LocationScreen.kt`** (GREEN) — `@Composable fun LocationScreen(modifier)`: `remember`/`mutableStateOf` for PP (Int), regionSize (RegionSize), currentResult, history. Chip row for region size (changing resets PP to `regionSize.startingPP`). Generate button → `LocationCrafter.generate(regionSize, rollFn)` updates result and history. Result card shows descriptor + 3 elements + PP used. History section (capped 10 newest-first). Run UI test to GREEN.

## Phase 5: Navigation — MainActivity Wiring

- [ ] 5.1 **Add `LOCATION_CRAFTER("Loc Craft", Icons.Filled.Place)`** to `Screen` enum + import `Icons.Filled.Place` + `when` branch → `LocationScreen()` in `MainActivity.kt`. Verify build with `./gradlew assembleDebug`.

## Phase 6: Lint + Full Verification

- [ ] 6.1 **Run `./gradlew check`** — fix any lint or type errors. Run full test suite (`./gradlew test` + `./gradlew compileDebugAndroidTestKotlin`). Verify all new test files pass.
