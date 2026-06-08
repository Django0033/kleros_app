# Tasks: Kleros Dice Roll

> Strict TDD is ACTIVE. Every production-code task is preceded by a corresponding test task (RED before GREEN).
> detekt and ktlint must pass after each phase.
> No new Gradle dependencies — use what the template already provides (JUnit 4, Compose UI Test).
> Review budget: 400 lines. Total estimated delta: ~340 added, ~20 modified — fits a single PR.

---

## Phase 1: Domain Data Types

### 1.1 — Write DiceTypeTest (RED)

| Field | Value |
|-------|-------|
| **Description** | Write unit test for `DiceType` enum. Verify each enum value has the correct `faces` count: D4→4, D6→6, D8→8, D10→10, D12→12, D20→20, D100→100. Verify all 7 values exist via `DiceType.entries`. Test that `name` returns the expected string label. |
| **Files** | `app/src/test/java/com/kleros/dice/DiceTypeTest.kt` — **create** |
| **TDD** | Run `./gradlew test` → FAIL (DiceType does not exist → compile-time RED). This is a valid RED gate (compile-time failure caused by missing production code). |
| **Depends on** | — |
| **Effort** | Small (~15 lines) |

### 1.2 — Implement DiceType enum (GREEN)

| Field | Value |
|-------|-------|
| **Description** | Create `DiceType` enum in `com.kleros.dice` with 7 values (D4, D6, D8, D10, D12, D20, D100). Each value carries a `faces: Int` property. Include `label: String` for display (e.g., `"D20"`). Package structure matches the flat `com.kleros.dice` convention. |
| **Files** | `app/src/main/java/com/kleros/dice/DiceType.kt` — **create** |
| **TDD** | Run `./gradlew test` → GREEN (DiceTypeTest now passes). |
| **Depends on** | 1.1 |
| **Effort** | Small (~10 lines) |

### 1.3 — Write DiceRollResultTest (RED)

| Field | Value |
|-------|-------|
| **Description** | Write unit test for `DiceRollResult` data class. Verify construction with `DiceType.D20` and `value=15`. Verify `timestampMillis` is populated (non-zero). Verify structural equality (`copy` with changed `value` produces a different instance). |
| **Files** | `app/src/test/java/com/kleros/dice/DiceRollResultTest.kt` — **create** |
| **TDD** | Run `./gradlew test` → compile-time RED (DiceRollResult not found). |
| **Depends on** | 1.2 (uses DiceType) |
| **Effort** | Small (~15 lines) |

### 1.4 — Implement DiceRollResult data class (GREEN)

| Field | Value |
|-------|-------|
| **Description** | Create `DiceRollResult` data class with fields: `diceType: DiceType`, `value: Int`, `timestampMillis: Long = System.currentTimeMillis()`. All properties immutable. |
| **Files** | `app/src/main/java/com/kleros/dice/DiceRollResult.kt` — **create** |
| **TDD** | Run `./gradlew test` → GREEN. |
| **Depends on** | 1.3, 1.2 |
| **Effort** | Small (~8 lines) |

---

## Phase 2: DiceRoller (Pure Function)

### 2.1 — Write DiceRollerTest (RED) ✅

| Field | Value |
|-------|-------|
| **Description** | Write comprehensive unit test for `DiceRoller.roll()`. For each of the 7 dice types, invoke `roll()` 1000 times and assert: every result is in `1..type.faces`. Additionally verify that every value in the range appears at least once (proves uniform distribution over 1000 samples). Edge cases: D4 (smallest range), D100 (largest range). Performance assertion: 7000 invocations must complete in under 100ms. |
| **Files** | `app/src/test/java/com/kleros/dice/DiceRollerTest.kt` — **create** |
| **TDD** | Run `./gradlew test` → compile-time RED (DiceRoller not found). ✅ **Confirmed** — 10 `Unresolved reference 'DiceRoller'` errors. |
| **Depends on** | 1.2 (uses DiceType) |
| **Effort** | Medium (~40 lines) |

### 2.2 — Implement DiceRoller object (GREEN) ✅

| Field | Value |
|-------|-------|
| **Description** | Create `DiceRoller` singleton object with a single pure function `fun roll(type: DiceType): Int`. Implementation: `Random.nextInt(1, type.faces + 1)`. No Android dependencies — pure Kotlin only. Package `com.kleros.dice`. |
| **Files** | `app/src/main/java/com/kleros/dice/DiceRoller.kt` — **create** |
| **TDD** | Run `./gradlew test` → GREEN (DiceRollerTest passes, including range and performance assertions). ✅ **Confirmed** — BUILD SUCCESSFUL, all 10 tests pass. |
| **Depends on** | 2.1, 1.2 |
| **Effort** | Small (~8 lines) |

---

## Phase 3: RollHistory (Capped List)

### 3.1 — Write RollHistoryTest (RED)

| Field | Value |
|-------|-------|
| **Description** | Write unit test for `RollHistory`. Scenarios: (1) empty history — `rolls` is empty list. (2) append 3 results — history size = 3, newest first. (3) append 12 results — history size = 10 (capped), newest first, oldest discarded. (4) ordering — timestamps are monotonically decreasing (newest at index 0). (5) `maxSize` is 10. |
| **Files** | `app/src/test/java/com/kleros/dice/RollHistoryTest.kt` — **create** |
| **TDD** | Run `./gradlew test` → compile-time RED (RollHistory not found). |
| **Depends on** | 1.2, 1.4 (uses DiceType, DiceRollResult) |
| **Effort** | Medium (~35 lines) |

### 3.2 — Implement RollHistory data class (GREEN)

| Field | Value |
|-------|-------|
| **Description** | Create `RollHistory` data class with `val results: List<DiceRollResult> = emptyList()` and a companion `const val MAX_SIZE = 10`. Implement `fun append(result: DiceRollResult): RollHistory` — returns a new instance with result prepended and list capped at 10. Immutable: never mutates `results`. |
| **Files** | `app/src/main/java/com/kleros/dice/RollHistory.kt` — **create** |
| **TDD** | Run `./gradlew test` → GREEN (RollHistoryTest passes). |
| **Depends on** | 3.1, 1.4 |
| **Effort** | Small (~15 lines) |

---

## Phase 4: DiceScreen Composable

### 4.1 — Write DiceScreenTest — UI Tests (RED) ✅

| Field | Value |
|-------|-------|
| **Description** | Write Compose UI Test for DiceScreen. Scenarios from spec: (1) App launches to dice screen — verify no "Hello Android!" text, verify DiceScreen renders. (2) Select D20 chip — verify D20 selector is displayed. (3) Tap Roll button — wait for idle, verify a numeric result is displayed. (4) Roll 3 times — verify history shows exactly 3 entries. Uses `ComposeTestRule` with `createComposeRule()`. |
| **Files** | `app/src/androidTest/java/com/kleros/dice/DiceScreenTest.kt` — **create** |
| **TDD** | Run `./gradlew connectedCheck` or `./gradlew :app:connectedDebugAndroidTest` → compile-time RED (DiceScreen composable not found). ✅ **Confirmed** — 4 `Unresolved reference 'DiceScreen'` errors on `compileDebugAndroidTestKotlin`. |
| **Depends on** | 1.2, 1.4, 2.2, 3.2 (uses DiceType, DiceRollResult, DiceRoller, RollHistory) |
| **Effort** | Medium (~50 lines) |

### 4.2 — Implement DiceScreen composable (GREEN) ✅

| Field | Value |
|-------|-------|
| **Description** | Create full `DiceScreen` composable in `com.kleros.dice`. Layout (top→bottom): (1) Dice type selector — `FlowRow` of `FilterChip`s, one per DiceType. (2) Roll button — `FilledTonalButton`, enabled when type selected. (3) Result card — `ElevatedCard` with animated integer + dice type label. (4) History — `LazyColumn` (max 10 items), each row showing dice type icon + value. State: `remember { mutableStateOf<DiceType>(DiceType.D6) }`, `mutableStateOf<DiceRollResult?>(null)`, `mutableStateOf(RollHistory())`. Animation: `animateIntAsState` with spring for result value; `Animatable(1f)` for scale pulse (1.0 → 1.2 → 1.0). Use `Modifier.graphicsLayer` for scale transform. |
| **Files** | `app/src/main/java/com/kleros/dice/DiceScreen.kt` — **create** |
| **TDD** | Run `./gradlew connectedDebugAndroidTest` → GREEN (DiceScreenTest passes). Also run `./gradlew test` → GREEN (existing unit tests still pass). ✅ **Confirmed** — `./gradlew test` PASS, `./gradlew compileDebugAndroidTestKotlin` PASS, `./gradlew detekt` PASS, `./gradlew ktlintCheck` PASS. |
| **Depends on** | 4.1, 1.2, 1.4, 2.2, 3.2 |
| **Effort** | Large (~100 lines) |

---

## Phase 5: MainActivity Integration

### 5.1 — Modify MainActivity (GREEN — no new test needed)

| Field | Value |
|-------|-------|
| **Description** | Replace `Greeting(name = "Android")` call with `DiceScreen()`. Remove the `Greeting` composable function and its `GreetingPreview`. Import `com.kleros.dice.DiceScreen`. The existing `Scaffold` remains unchanged. No test needed — DiceScreenTest.kt from 4.1 already verifies that DiceScreen is the displayed content. |
| **Files** | `app/src/main/java/com/kleros/MainActivity.kt` — **modify** (replace ~10 lines, remove ~10 lines) |
| **TDD** | Run `./gradlew connectedDebugAndroidTest` → GREEN (DiceScreenTest still passes). Run `./gradlew test` → GREEN. |
| **Depends on** | 4.2 |
| **Effort** | Small (~5 lines changed, ~10 lines removed) |

---

## Phase 6: Lint & Verification

### 6.1 — Run lint and fix issues

| Field | Value |
|-------|-------|
| **Description** | Run `./gradlew detekt ktlintCheck` across the project. Fix any style issues introduced by new files (detekt config is in `config/detekt/detekt.yml`). Common issues: wildcard imports, missing KDoc, formatting. Also run `./gradlew check` to confirm jacoco verification passes (threshold is 0.0, so this should pass trivially). |
| **Files** | None — fixes applied to existing files if lint fails |
| **TDD** | — |
| **Depends on** | 5.1 (all source changes complete) |
| **Effort** | Small (~5 min) |

---

## Dependency Graph

```
Phase 1 ──────────────────────────────────────────────────
  1.1 (DiceTypeTest) → 1.2 (DiceType enum)
  1.3 (DiceRollResultTest) → 1.4 (DiceRollResult)
                               │
Phase 2 ───────────────────────┤
  2.1 (DiceRollerTest) ────────┤
        → 2.2 (DiceRoller)     │
                               │
Phase 3 ───────────────────────┤
  3.1 (RollHistoryTest) ───────┤
        → 3.2 (RollHistory)    │
                               ├── All needed before Phase 4
Phase 4 ───────────────────────┤
  4.1 (DiceScreenTest) ←───────┤
        → 4.2 (DiceScreen) ────┤
                               │
Phase 5 ───────────────────────┘
  5.1 (MainActivity) ──────────  depends on 4.2

Phase 6 ── 6.1 (Lint) ────────  depends on 5.1

Parallelism:
  - 1.2 → 1.3 (serial: DiceType needed before DiceRollResultTest)
  - 2.2 and 3.2 can be implemented in parallel after 1.2 and 1.4
  - 4.2 blocks on 2.2, 3.2
  - 5.1 blocks on 4.2
```

---

## Delivery Forecast

| Metric | Estimate |
|--------|----------|
| **Files created** | 10 (5 production + 4 test + 1 UI test) |
| **Files modified** | 1 (`MainActivity.kt`) |
| **Total new lines** | ~340 |
| **Modified lines** | ~15 changed + ~10 removed |
| **Total delta** | ~365 lines |
| **Review budget** | 400 lines |
| **PR strategy** | **Single PR** — total delta fits under the 400-line review budget. All changes are in the same feature boundary with no dependencies on external reviews. |
| **Commit strategy** | 1 commit per TDD cycle (RED → GREEN), then a final refactor/lint commit. Max ~6 commits. |
| **Risk level** | **Low** — all changes are additive or one-line modifications. Rollback plan: revert `MainActivity.kt` and delete `app/src/main/java/com/kleros/dice/`. |

### Lint Configuration Note

detekt and ktlint are active. Before merging, run:
```bash
./gradlew detekt ktlintCheck
```

Common pitfalls:
- detekt may flag large composable functions — `DiceScreen` at ~100 lines may trigger `TooManyFunctions` or `LongMethod`. If so, extract sub-composables (`DiceTypeSelector`, `RollButton`, `ResultCard`, `HistoryList`).
- ktlint enforces 2-space indent and no wildcard imports — ensure IDE settings match.

---

## Summary of Test Coverage

| Test file | Type | What it covers | RED gate |
|-----------|------|----------------|----------|
| `DiceTypeTest.kt` | Unit | Enum values, face counts | Compile-time (class not found) |
| `DiceRollResultTest.kt` | Unit | Construction, timestamp, equality | Compile-time (class not found) |
| `DiceRollerTest.kt` | Unit | Range validation (1000×7 rolls), uniform distribution | Compile-time (object not found) |
| `RollHistoryTest.kt` | Unit | Append, max-size cap, ordering, empty | Compile-time (class not found) |
| `DiceScreenTest.kt` | UI (Compose) | Select dice type, roll, result display, history | Compile-time (composable not found) |
