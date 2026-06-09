# Tasks: Creature Crafter

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~400 (200 prod + 200 test) |
| 400-line budget risk | Low — exact Character Crafter pattern, additive package, no migration |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | single-pr |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Full Creature Crafter feature | PR 1 | Single PR, 11 files, base = main |

## Phase 1: CreatureData + Test

- [x] 1.1 Create `app/src/main/java/com/kleros/creature/CreatureData.kt` with `descriptors` (100 DIRECT entries), `abilities` (100 DIRECT, 50 paired), `behaviorInitial` (10 DIRECT), `behaviorNew` (10 DIRECT)
- [x] 1.2 Write `app/src/test/java/com/kleros/creature/CreatureDataTest.kt` — RED: table sizes 100/100/10/10, unique indices 1..100, non-empty results, at least 2 adjacent duplicates in descriptors for weighting

## Phase 2: CreatureResult + CreatureHistory + Tests

- [x] 2.1 Create `app/src/main/java/com/kleros/creature/CreatureResult.kt` — data class with `descriptors: List<String>`, `abilities: List<String>`, `initialBehavior: String`, `statistics: String`, `newBehavior: String?`, `timestampMillis`
- [x] 2.2 Write `app/src/test/java/com/kleros/creature/CreatureResultTest.kt` — RED: constructor assigns fields, copy immutability, timestamp auto-populated > 0
- [x] 2.3 Create `app/src/main/java/com/kleros/creature/CreatureHistory.kt` — data class with immutable `append()`, caps at 10, newest-first
- [x] 2.4 Write `app/src/test/java/com/kleros/creature/CreatureHistoryTest.kt` — RED: empty → size 0, append 3 → newest-first, append 12 → cap 10 evicts oldest, MAX_SIZE=10

## Phase 3: CreatureCrafter + Test

- [x] 3.1 Create `app/src/main/java/com/kleros/creature/CreatureCrafter.kt` — object with `generate()`: 2×d100 descriptors, 2×d100 abilities, 1×d10 initBehavior, 1×d10 stat (reuses `CharacterData.statistics`)
- [x] 3.2 Add `rollDescriptor(result, rollFn)` — append one descriptor to `result.descriptors`, return new `CreatureResult` copy
- [x] 3.3 Add `rollAbility(result, rollFn)` — append one ability, return new copy
- [x] 3.4 Add `rollNewBehavior(result, rollFn)` — set `newBehavior` if null, no-op if non-null (don't invoke rollFn)
- [x] 3.5 Write `app/src/test/java/com/kleros/creature/CreatureCrafterTest.kt` — RED: deterministic rolls match expected, mutation tests (2→3 size, original unchanged, newBehavior null→sets/non-null→no-op), 1000 random all non-empty, timestamp > 0, stat boundaries 1..10

## Phase 4: CreatureScreen + UI Test

- [x] 4.1 Create `app/src/main/java/com/kleros/creature/CreatureScreen.kt` — composable: generate button, result card (descriptors/abilities/behavior/statistics), 3 mutation buttons (roll descriptor/ability/new behavior), history list; testTags per design
- [x] 4.2 Write `app/src/androidTest/java/com/kleros/creature/CreatureScreenTest.kt` — RED: generate button displayed, tap→labels visible, mutation buttons visible, history accumulates on repeated taps

## Phase 5: MainActivity Wiring

- [x] 5.1 Add `CREATURE_CRAFTER("Creature", Icons.Filled.BugReport)` to `Screen` enum in `MainActivity.kt`
- [x] 5.2 Add `Screen.CREATURE_CRAFTER -> CreatureScreen(modifier = Modifier.padding(innerPadding))` to `when` block
- [x] 5.3 Add `import com.kleros.creature.CreatureScreen` and `import androidx.compose.material.icons.filled.BugReport`

## Phase 6: Lint + Verify

- [x] 6.1 Run `./gradlew lint` — fix any lint violations in creature package
- [x] 6.2 Run `./gradlew test` — all unit tests pass
- [x] 6.3 Run `./gradlew assembleDebug` — build succeeds
