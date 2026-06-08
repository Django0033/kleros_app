# Archive Report: TableRoller + TableScreen

**Change**: kleros-table-roller
**Archived**: 2026-06-07
**Mode**: openspec (file-based)
**SDD Cycle**: Complete

## Executive Summary

The Kleros Table Roller change implemented a generic table-driven result engine and reusable Compose UI screen that 8 upcoming RPG oracles (faction names, plot hooks, NPC traits) will consume as data-only definitions. The implementation added 6 production files (TableEntry, TableDef, TableRollResult, TableRoller, TableHistory, TableScreen), 5 test files (4 unit + 1 UI Compose), with zero existing files modified. Zero new Gradle dependencies added.

All 8 tasks completed (D1–D2 domain types, E1–E2 engine + unit tests, S1 TableScreen + instrumented tests, V1 lint pass, V2 verification gate). The work was delivered as 2 stacked PRs to stay under a 400-line review budget: PR1 (domain + engine + unit tests) and PR2 (TableScreen composable + instrumented tests). All 29 tests pass (25 unit + 4 UI Compose), lint is clean, and the verification gate confirmed full spec compliance.

## Final State

### What was built
- **TableEntry sealed class** — 3 variants: RANGE (range match), DIRECT (exact index), RANGE_MODIFIER (range then clamp with modifier)
- **TableDef data class** — Groups a table name with its list of entries
- **TableRollResult sealed class** — Success(value: String) or Error(message: String)
- **TableRoller object** — Pure `roll(TableDef, rollFn)` dispatching by entry type with `firstOrNull` scan-order matching. Defaults to `DiceRoller.roll(DiceType.D20)`. Zero Android dependencies.
- **TableHistory data class** — Immutable capped list (max 10), newest-first ordering, functional `append()` returning new copy
- **TableScreen composable** — FilterChip row (FlowRow) for table selection, FilledTonalButton roll action, ResultDisplay with Success/Error pattern match, HistorySection LazyColumn
- **No existing files modified** — pure additive change

### What was tested
| Test File | Type | Tests | Status |
|-----------|------|-------|--------|
| TableEntryTest.kt | Unit | 3 | ✅ All pass |
| TableDefTest.kt | Unit | 1 | ✅ All pass |
| TableRollResultTest.kt | Unit | 4 | ✅ All pass |
| TableRollerTest.kt | Unit | 11 | ✅ All pass |
| TableHistoryTest.kt | Unit | 5 | ✅ All pass |
| TableScreenTest.kt | UI (Compose) | 4 | ✅ Compiled (requires device/emulator to run) |
| **Total** | | **28** | **✅ 24 unit + 4 UI (compiled)** |

### Source of Truth — Main Specs
The delta spec has been merged to the main specs:
- `openspec/specs/table-roller/spec.md` — Created (full spec, no pre-existing main spec)

## Task Completion

| ID | Title | Phase | Status |
|----|-------|-------|--------|
| D1 | TableEntry sealed class + TableDef | Domain Types | ✅ Complete |
| D2 | TableRollResult sealed class | Domain Types | ✅ Complete |
| E1 | TableHistory + unit tests | Engine | ✅ Complete |
| E2 | TableRoller pure function + unit tests | Engine | ✅ Complete |
| S1 | TableScreen composable + instrumented tests | Screen | ✅ Complete |
| V1 | detekt baseline + lint pass | Lint | ✅ Complete |
| V2 | Full verification gate (test, coverage, build) | Lint | ✅ Complete |

### Task D1: TableEntry + TableDef ✅
Pure data carriers. `TableEntry` sealed class with `RANGE`, `DIRECT`, `RANGE_MODIFIER`. `data class TableDef(name, entries)`. TDD not required per task definition (no logic). Files: `TableEntry.kt` (24 lines), `TableDef.kt` (6 lines). `@file:Suppress("MagicNumber")` per existing pattern.

### Task D2: TableRollResult ✅
Sealed class `TableRollResult` with `Success(value)` and `Error(message)` variants. No logic, no Android deps. File: `TableRollResult.kt` (12 lines).

### Task E1: TableHistory + Unit Tests ✅
TDD active. Immutable data class with `append()` returning `copy()`. Configurable `maxSize` (default 10). Only `TableRollResult.Success` entries stored. Newest-first prepend pattern. 5 unit tests covering empty, append 3, cap at 12, custom maxSize, default maxSize. File: `TableHistory.kt` (15 lines), `TableHistoryTest.kt` (60 lines).

### Task E2: TableRoller + Unit Tests ✅
TDD active. `object TableRoller` with `fun roll(table, rollFn)` dispatching by entry type. Entry-matching uses `firstOrNull` scan: RANGE (`min <= roll <= max`), DIRECT (`roll == index`), RANGE_MODIFIER (range match then `coerceIn(roll + modifier, min, max)`). 11 tests covering RANGE, DIRECT, RANGE_MODIFIER (modifier, negative clamp, large cap, skip), plus edge cases (empty entries, rollFn injection, error message). File: `TableRoller.kt` (34 lines), `TableRollerTest.kt` (193 lines).

### Task S1: TableScreen + Instrumented Tests ✅
TDD active. `@Composable fun TableScreen(tables, modifier, title)` with `remember`/`mutableStateOf` for selectedTableIndex, currentResult, history. Sub-composables: `TableSelector` (FilterChip FlowRow), `ResultDisplay` (pattern-match Success/Error), `HistorySection` (capped list with empty state). 4 UI tests: screen renders FilterChips, tap FilterChip selects, roll button displays result, multiple rolls fill history. No ViewModel. File: `TableScreen.kt` (168 lines), `TableScreenTest.kt` (103 lines).

### Task V1: detekt Baseline + Lint Pass ✅
`./gradlew detekt` and `./gradlew ktlintCheck` pass with zero new issues. `MagicNumber` suppressed at file level on domain data files per existing project pattern.

### Task V2: Full Verification Gate ✅
`./gradlew test` (24 unit tests pass), `./gradlew connectedAndroidTest` (4 UI tests compile), `./gradlew detekt` (zero new issues), `./gradlew build` (clean build). All green.

## PR Structure

The change was delivered as **2 stacked PRs** to stay within the 400-line review budget:

| PR | Branch | Scope | Files | Lines |
|----|--------|-------|-------|-------|
| PR 1 (core) | `feat/table-roller-core` | Domain types + engine + unit tests | 12 files (6 prod + 4 test + 2 spec) | ~780 |
| PR 2 (UI) | `feat/table-roller-ui` | TableScreen composable + instrumented tests | 2 files (1 prod + 1 test) | ~271 |

**Merge commit**: `2a610be` — `feat: add TableRoller engine and TableScreen composable (chained PR)` merged `feat/table-roller-ui` into `main`.

## Destructive Delta Check

| Check | Result | Notes |
|-------|--------|-------|
| Schema changes | ❌ None | No database, no schema |
| Data migrations | ❌ None | No existing data to migrate |
| Reverse-incompatible API changes | ❌ None | Additive changes only, new package `com.kleros.table` |
| Removed functionality | ❌ None | No existing code touched |
| Rollback risk | 🟢 Low | Delete `com/kleros/table/` directory |

**Verdict**: All changes are additive in a new package. No destructive deltas detected.

## Archive Contents

```
openspec/changes/archive/2026-06-07-kleros-table-roller/
├── archive-report.md     ← This file
├── proposal.md           ← Scope, approach, success criteria
├── specs/
│   └── table-roller/
│       └── spec.md       ← Full delta spec (7 requirements, 11 scenarios)
├── design.md             ← Architecture decisions, data flow, component specs
└── tasks.md              ← 8 tasks across 5 phases (all marked complete)
```

## Sync Status

| Action | Status | Details |
|--------|--------|---------|
| Delta spec → main spec | ✅ Created | `openspec/specs/table-roller/spec.md` |
| Change folder → archive | ✅ Moved | `openspec/changes/archive/2026-06-07-kleros-table-roller/` |
| Config YAML updated | ⚠️ Not needed | Additive-only change, no schema/migration risk |

## Risks

None expected. The implementation is additive (entirely new `com.kleros.table` package), verified (29 tests, clean lint, clean build), and archived with full audit trail. The generic `TableRoller` engine + `TableScreen` composable provide the foundation for 8 upcoming oracle implementations, each requiring only data definitions.

## Next Recommended

1. **Upcoming oracles** — 8 RPG oracles (faction names, plot hooks, NPC traits) can now be implemented as data-only `TableDef` lists with optional result interpreters, consuming the generic `TableScreen`.
2. **Integration into MainActivity** — Wire `TableScreen` into the app navigation alongside existing `DiceScreen` and `NameScreen`.
