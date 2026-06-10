# Tasks: Adventure Crafter — Plot Action Tables

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~650 (500 data-only entries, ~150 logic/test) |
| 400-line budget risk | Medium |
| Chained PRs recommended | No |
| Suggested split | Single PR (data-heavy but logic-trivial) |
| Delivery strategy | single-pr |
| Chain strategy | size-exception |

Decision needed before apply: Yes
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Medium

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | AdventureCrafter complete | PR 1 | Single PR — bulk is repetitive DIRECT data; logic is ~150 lines |

## Phase 1: Data Unit Test (RED)

- [x] 1.1 Create `app/src/test/java/com/kleros/adventure/AdventureDataTest.kt` — 5 tests, one per table, asserting exactly 100 `TableEntry.DIRECT` entries each, plus guards for index 1 and 100 coverage (first/last entry bounds). Follow `MeaningDataTest` pattern but test DIRECT instead of RANGE.

## Phase 2: Adventure Data (GREEN)

- [x] 2.1 Create `app/src/main/java/com/kleros/adventure/AdventureData.kt` — `object AdventureData` with 5 `TableDef` vals (`action`, `tension`, `mystery`, `social`, `personal`), each containing 100 `TableEntry.DIRECT(index, result)` entries for indices 1–100. Expose `val tables: List<TableDef>`. Package `com.kleros.adventure`.

## Phase 3: Adventure Screen (GREEN)

- [x] 3.1 Create `app/src/main/java/com/kleros/adventure/AdventureScreen.kt` — `@Composable fun AdventureScreen(modifier)` delegating to `TableScreen(tables = AdventureData.tables, diceType = DiceType.D100, title = "Adventure Crafter")`. Follow `MeaningScreen` pattern.

## Phase 4: Screen UI Test (RED)

- [x] 4.1 Create `app/src/androidTest/java/com/kleros/adventure/AdventureScreenTest.kt` — compose smoke test asserting roll button + 5 table FilterChips ("Action", "Tension", "Mystery", "Social", "Personal") are displayed. Follow `MeaningScreenTest` pattern with `@RunWith(AndroidJUnit4::class)` and `createComposeRule`.

## Phase 5: Navigation Wiring

- [x] 5.1 `MainActivity.kt` — add `ADVENTURE_CRAFTER("Adv Craft", Icons.Filled.AutoAwesome)` to `Screen` enum. Add import `com.kleros.adventure.AdventureScreen`. Wire `when` branch: `Screen.ADVENTURE_CRAFTER -> AdventureScreen(modifier = Modifier.padding(innerPadding))`.

## Phase 6: Lint Verification

- [x] 6.1 Run `./gradlew lint` — fix any warnings. Verify all new files pass lint with no `MagicNumber` or `FunctionNaming` suppression issues.
