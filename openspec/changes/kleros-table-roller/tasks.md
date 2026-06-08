# Tasks: TableRoller + TableScreen

## Overview

| | |
|---|---|
| **Change** | kleros-table-roller |
| **Total effort** | ~5–8 hours |
| **Estimated lines** | ~400 (prod: ~190, test: ~210) |
| **PR strategy** | Single PR (under 400 line review budget) or 2 stacked PRs |
| **Strict TDD** | Active — production code tasks have matching test tasks |
| **Package** | `com.kleros.table` (new) |
| **Dependencies** | `DiceRoller` (`com.kleros.dice`) — imported, no code changes |

---

## Phase 1 — Domain Types (pure Kotlin, no Android deps)

### Task D1: TableEntry + TableDef ✅
| Field | Value |
|-------|-------|
| **ID** | `kleros-table-roller-D1` |
| **Title** | Create TableEntry sealed class and TableDef data class |
| **TDD** | ❌ Not required (pure data carriers, no logic) |
| **Effort** | XS (~30 min) |
| **Depends on** | — |
| **Files** | `app/src/main/java/com/kleros/table/TableEntry.kt` (create) |
| | `app/src/main/java/com/kleros/table/TableDef.kt` (create) |
| **Description** | Create `TableEntry` sealed class with three variants: `RANGE(min, max, result)`, `DIRECT(index, result)`, `RANGE_MODIFIER(min, max, result, modifier)`. Create `data class TableDef(name, entries: List<TableEntry>)`. Pure data, no logic, no Android imports. File-level `@Suppress("MagicNumber")` per existing pattern. |

---

### Task D2: TableRollResult ✅
| Field | Value |
|-------|-------|
| **ID** | `kleros-table-roller-D2` |
| **Title** | Create TableRollResult sealed class |
| **TDD** | ❌ Not required (pure sealed class, no logic) |
| **Effort** | XS (~15 min) |
| **Depends on** | — |
| **Files** | `app/src/main/java/com/kleros/table/TableRollResult.kt` (create) |
| **Description** | Create sealed class `TableRollResult` with `Success(value: String)` and `Error(message: String)` variants. Used by `TableRoller` return type and `TableHistory` storage (Success entries only). Pure sealed class — no methods, no Android imports. |

---

## Phase 2 — Engine (pure Kotlin, no Android deps)

### Task E1: TableHistory + Unit Tests ✅
| Field | Value |
|-------|-------|
| **ID** | `kleros-table-roller-E1` |
| **Title** | Create TableHistory with immutable capped list + tests |
| **TDD** | ✅ **Active** — write test first (RED), then production code (GREEN) |
| **Effort** | S (~1 hour) |
| **Depends on** | D2 (`TableRollResult.Success` type) |
| **Files** | `app/src/main/java/com/kleros/table/TableHistory.kt` (create) |
| | `app/src/test/java/com/kleros/table/TableHistoryTest.kt` (create) |
| **Pattern** | Mirror `RollHistory`/`NameHistory`: `data class`, `append()` returning `copy()`, `companion object` with `const val MAX_SIZE = 10`, newest-first ordering. Design adds configurable `maxSize` constructor param (default 10). Only `TableRollResult.Success` entries stored. |
| **TDD sub-steps** | 1. Write `TableHistoryTest.kt` with test cases for empty, cap at 10, ordering, eviction of oldest — **verify RED** (doesn't compile). |
| | 2. Write `TableHistory.kt` — **verify GREEN** (all tests pass). |
| **Test cases** | - Empty history returns empty list |
| | - Append 3 entries → size 3, newest at index 0 |
| | - Append 12 entries → size 10, oldest 2 evicted |
| | - `maxSize` constructor param overrides default |
| | - Only `Success` entries accepted by `append` |
| | - History stores newest first (prepend pattern) |

---

### Task E2: TableRoller + Unit Tests ✅
| Field | Value |
|-------|-------|
| **ID** | `kleros-table-roller-E2` |
| **Title** | Create TableRoller pure roll function + tests |
| **TDD** | ✅ **Active** — write test first (RED), then production code (GREEN) |
| **Effort** | M (~1.5–2 hours) |
| **Depends on** | D1 (`TableEntry`, `TableDef`), D2 (`TableRollResult`) |
| **Files** | `app/src/main/java/com/kleros/table/TableRoller.kt` (create) |
| | `app/src/test/java/com/kleros/table/TableRollerTest.kt` (create) |
| **Design** | `object TableRoller` with `fun roll(table: TableDef, rollFn: () -> Int = { DiceRoller.roll(DiceType.D20) }): TableRollResult`. Entry matching: scan in declaration order, first match wins. RANGE: `min <= roll <= max`. DIRECT: `roll == index`. RANGE_MODIFIER: match by raw roll, then `effective = (roll + modifier).coerceIn(min, max)`, result string is always entry's display value. |
| **TDD sub-steps** | 1. Write `TableRollerTest.kt` with deterministic `rollFn` — **verify RED** (doesn't compile). |
| | 2. Write `TableRoller.kt` — **verify GREEN** (all tests pass). |
| **Test cases (16+ tests)** | **RANGE**: match within range, no match outside range, first-match with overlapping ranges, catch-all entry at end. **DIRECT**: exact index match, non-matching index skips. **RANGE_MODIFIER**: positive modifier, negative modifier clamped at min, large modifier capped at max, modifier doesn't affect non-matching range. **Edge cases**: roll outside all entries → `Error`, empty entries list → `Error`, first entry wins with duplicate ranges. |

---

## Phase 3 — Screen (Compose UI)

### Task S1: TableScreen + Instrumented Tests
| Field | Value |
|-------|-------|
| **ID** | `kleros-table-roller-S1` |
| **Title** | Create TableScreen composable + instrumented tests |
| **TDD** | ✅ **Active** — write instrumented test first (RED), then composable (GREEN) |
| **Effort** | L (~2–3 hours) |
| **Depends on** | D1, D2, E1, E2 |
| **Files** | `app/src/main/java/com/kleros/table/TableScreen.kt` (create) |
| | `app/src/androidTest/java/com/kleros/table/TableScreenTest.kt` (create) |
| **Design** | Generic `@Composable fun TableScreen(tables: List<TableDef>, modifier: Modifier = Modifier)`. Composable-local state via `remember`/`mutableStateOf`. No ViewModel. `FilterChip` row (FlowRow) for table selection — first selected by default. `FilledTonalButton("Roll")` triggers `TableRoller.roll` with `rollFn = { DiceRoller.roll(DiceType.D20) }`. Result display (pattern-match `Success`/`Error`). History list with `HistorySection` composable. Follows `DiceScreen`/`NameScreen` patterns: `Modifier.testTag`, `KlerosTheme` in tests, `@RunWith(AndroidJUnit4::class)`, `createComposeRule`. |
| **TDD sub-steps** | 1. Write `TableScreenTest.kt` with test cases — **verify RED** (doesn't compile). |
| | 2. Write `TableScreen.kt` — **verify GREEN** (all tests pass). |
| **Test cases** | - Screen renders with FilterChips for each `TableDef` (check counts) |
| | - First table selected by default |
| | - Select second table via FilterChip, then Roll → result displayed |
| | - Multiple rolls fill history (newest first) |
| | - History capped at 10 (overflow test with 12 rolls) |
| | - Error result handled gracefully (no matching entry) |

---

## Phase 4 — Lint & Verification

### Task V1: detekt Baseline + Lint Pass
| Field | Value |
|-------|-------|
| **ID** | `kleros-table-roller-V1` |
| **Title** | Run detekt and ktlint, update baseline |
| **TDD** | ❌ Not required |
| **Effort** | XS (~15 min) |
| **Depends on** | All above tasks complete |
| **Files** | `app/config/detekt/detekt.yml` (may update baseline) |
| **Description** | Run `./gradlew detekt` and `./gradlew ktlintCheck`. Suppress `MagicNumber` on domain data files per existing pattern. Update baseline if needed. Zero new issues required. |

### Task V2: Full Verification Gate
| Field | Value |
|-------|-------|
| **ID** | `kleros-table-roller-V2` |
| **Title** | Run full test suite, coverage, and build |
| **TDD** | ❌ Not required (verification only) |
| **Effort** | XS (~15 min) |
| **Depends on** | V1 |
| **Description** | Run `./gradlew test` (unit tests pass), `./gradlew connectedAndroidTest` (instrumented tests pass), `./gradlew detekt` (zero new issues), `./gradlew build` (clean build). All green required. |

---

## Dependency Graph

```
D1 (TableEntry + TableDef) ──────────┐
                                     ├──→ E2 (TableRoller + tests) ──┐
D2 (TableRollResult) ─────────────────┤                               │
                                     └──→ E1 (TableHistory + tests) ──┤
                                                                       ├──→ S1 (TableScreen + tests) ──→ V1 (lint) ──→ V2 (verify)
```

**Parallel paths:**
- D1 + D2: independent, can run in parallel
- E1 + E2: depend on D1/D2 but not each other, can run in parallel
- S1: depends on all prior phases, sequential
- V1 → V2: sequential verification

---

## Delivery Forecast

### Line Count Estimate

| File | Lines | Type |
|------|-------|------|
| `TableEntry.kt` | ~20 | Production |
| `TableDef.kt` | ~5 | Production |
| `TableRollResult.kt` | ~12 | Production |
| `TableHistory.kt` | ~18 | Production |
| `TableRoller.kt` | ~45 | Production |
| `TableScreen.kt` | ~110 | Production |
| `TableHistoryTest.kt` | ~60 | Test |
| `TableRollerTest.kt` | ~90 | Test |
| `TableScreenTest.kt` | ~80 | Test |
| **Total** | **~440** | |

### PR Strategy Recommendation

**Recommended: Single PR** — at ~440 lines total (including tests), this is manageable. Production code is ~210 lines. If the 400-line review budget is strict:

**Alternative: 2 stacked PRs:**
1. **PR 1** (`~210 lines`): Tasks D1, D2, E1, E2 — domain types + engine + history + unit tests
2. **PR 2** (`~190 lines`): Tasks S1, V1, V2 — TableScreen composable + instrumented tests + lint

Stacked PRs keep each under 200 lines for a faster review cycle. The engine PR can be reviewed independently of the UI.

---

## Risks & Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| `DiceType.D20` import in default rollFn makes domain layer depend on dice package | Low | Low | Import is in `TableRoller.kt` which stays in `com.kleros.table` package; `DiceRoller` is already a stable module |
| Compose test `waitForIdle()` flakiness with `TableRoller` wrapping `DiceRoller` | Low | Med | Use deterministic `rollFn` in tests; real RNG only in production default |
| `RANGE_MODIFIER` clamping logic edge cases | Low | Med | Covered by unit tests with deterministic rollFn; `coerceIn` is well-tested stdlib |
| detekt `MagicNumber` on test constants | Med | Low | Suppress at file level on domain files per existing pattern; test constants are exempt |
| PR exceeds 400-line review budget | Med | Low | Stack into 2 PRs if needed |

---

## Skill Resolution

```
skill_resolution:
  loaded_skills:
    - sdd-tasks
    - tdd-workflow
    - coding-standards
    - frontend-patterns
  rationale: >
    sdd-tasks is the orchestrator-requested skill for this phase.
    tdd-workflow loaded because Strict TDD is active across all production tasks.
    coding-standards loaded to match existing RollHistory/NameHistory/DiceScreen patterns.
    frontend-patterns loaded for Compose UI patterns (FilterChip, FlowRow, testTag, etc).
```
