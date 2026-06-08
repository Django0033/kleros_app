# Tasks: Meaning Tables + TableScreen diceType Fix

## Overview

Two bundled changes: (1) add optional `diceType` param to `TableScreen` for
D100 support, (2) create `com.kleros.meaning` package with two 1d100 word
tables, a composable wrapper, and navigation wiring. ~80 prod lines + ~60 test
lines. Single PR, ~140 line delta.

## Phase 0: Spec Housekeeping

| ID | Title | Description | Files | TDD | Deps | Effort |
|----|-------|-------------|-------|-----|------|--------|
| T0 | Update table-roller spec with diceType NFR | Add NFR to `openspec/specs/table-roller/spec.md`: "TableScreen SHALL accept an optional `diceType: DiceType` parameter defaulting to `DiceType.D20`". Update the `@Composable` signature in the Data Definitions block. | `openspec/specs/table-roller/spec.md` | No (doc-only) | None | XS |

---

## Phase 1: TableScreen diceType Fix (TDD)

This is the enabling change. **Start with existing tests to prove RED/GREEN**
since the existing `TableScreenTest` will break if `TableScreen` signature
changes without a default — but the design adds a default so existing tests
keep compiling. The real TDD cycle is for the new `diceType` behaviour in the
onClick lambda.

| ID | Title | Description | Files | TDD | Deps | Effort |
|----|-------|-------------|-------|-----|------|--------|
| T1 | Add `diceType` param to `TableScreen` | Add `diceType: DiceType = DiceType.D20` to `TableScreen` composable params (after `title`). Change the onClick lambda from hardcoded `DiceRoller.roll(DiceType.D20)` to `DiceRoller.roll(diceType)`. Import `DiceType`. Verify existing `TableScreenTest` suite still passes (backward compat). | `app/.../table/TableScreen.kt` | T3 (test first) | None | XS |

---

## Phase 2: Meaning Data (TDD)

| ID | Title | Description | Files | TDD | Deps | Effort |
|----|-------|-------------|-------|-----|------|--------|
| T2 | Write MeaningData data-integrity tests | Create `MeaningDataTest.kt` in `app/src/test/java/com/kleros/meaning/`. Tests: (1) `MeaningData.tables` has exactly 2 entries (Action + Description). (2) Each table has exactly 50 entries. (3) Every entry is `TableEntry.RANGE`. (4) Each entry has `max - min == 1` (2-point span). (5) `entries[0].min == 1` and `entries.last().max == 100`. (6) No gaps: for sorted entries, `entries[i].max + 1 == entries[i+1].min`. These tests will RED-compile-fail because `MeaningData` doesn't exist yet. **Compile-time RED is acceptable per TDD skill rules** — the failure is a missing implementation, not a broken test setup. | `app/src/test/java/com/kleros/meaning/MeaningDataTest.kt` | Yes — RED via compile failure | None | S |
| T3 | Create MeaningData with two 1d100 TableDefs | Create `app/src/main/java/com/kleros/meaning/MeaningData.kt`, package `com.kleros.meaning`. Define `object MeaningData { val tables: List<TableDef> }` with two `TableDef` constants: `MEANING_ACTION` (verbs: Attain, Benefit, Carry, etc. — 50 `RANGE` entries 1-2, 3-4, ..., 99-100) and `MEANING_DESCRIPTION` (adjectives: Artificial, Beautiful, Cold, etc.). Import `TableDef` / `TableEntry` from `com.kleros.table`. Run `T2` tests to prove GREEN. | `app/.../meaning/MeaningData.kt` | Yes — GREEN for T2 | T2 | M |

### Word table contents (50 entries each)

**Action** (verbs, 2-point ranges 1-100):
1-2=Attain, 3-4=Benefit, 5-6=Carry, 7-8=Change, 9-10=Communicate,
11-12=Construct, 13-14=Create, 15-16=Deceive, 17-18=Defend, 19-20=Delay,
21-22=Destroy, 23-24=Disrupt, 25-26=Escape, 27-28=Explore, 29-30=Gather,
31-32=Guide, 33-34=Heal, 35-36=Hide, 37-38=Influence, 39-40=Learn,
41-42=Lead, 43-44=Manipulate, 45-46=Move, 47-48=Obscure, 49-50=Observe,
51-52=Oppose, 53-54=Organize, 55-56=Perform, 57-58=Persuade, 59-60=Protect,
61-62=Pursue, 63-64=Question, 65-66=Recruit, 67-68=Reveal, 69-70=Reward,
71-72=Ruin, 73-74=Search, 75-76=Secure, 77-78=Seize, 79-80=Separate,
81-82=Support, 83-84=Survive, 85-86=Threaten, 87-88=Transform, 89-90=Trap,
91-92=Travel, 93-94=Undermine, 95-96=Worship, 97-98=Yield, 99-100=Uncertain

**Description** (adjectives, 2-point ranges 1-100):
1-2=Artificial, 3-4=Beautiful, 5-6=Bleak, 7-8=Bright, 9-10=Broken,
11-12=Chaotic, 13-14=Corrupted, 15-16=Cursed, 17-18=Dark, 19-20=Dead,
21-22=Desolate, 23-24=Distant, 25-26=Divine, 27-28=Fading, 29-30=Familiar,
31-32=Forgotten, 33-34=Fragile, 35-36=Frozen, 37-38=Glorious, 39-40=Grand,
41-42=Hidden, 43-44=Hostile, 45-46=Impossible, 47-48=Innocent, 49-50=Lost,
51-52=Luminous, 53-54=Massive, 55-56=Mysterious, 57-58=Natural, 59-60=Neglected,
61-62=Peaceful, 63-64=Perilous, 65-66=Primal, 67-68=Profane, 69-70=Pure,
71-72=Ruined, 73-74=Sacred, 75-76=Silent, 77-78=Sinister, 79-80=Sorrowful,
81-82=Strange, 83-84=Sunken, 85-86=Thorny, 87-88=Twisted, 89-90=Unstable,
91-92=Violent, 93-94=Wandering, 95-96=Wild, 97-98=Withered, 99-100=Weird

---

## Phase 3: MeaningScreen (TDD)

| ID | Title | Description | Files | TDD | Deps | Effort |
|----|-------|-------------|-------|-----|------|--------|
| T4 | Write MeaningScreen instrumented smoke tests | Create `MeaningScreenTest.kt` in `app/src/androidTest/java/com/kleros/meaning/`. Tests: (1) screen renders `tableSelector_Action` and `tableSelector_Description` FilterChips. (2) Tap roll button displays `rollResult` tag. (3) Roll three times on Action, switch to Description, roll twice — `historyItem` count is 5. Model after `NameScreenTest.kt` and `TableScreenTest.kt` patterns. These tests will RED-compile-fail until `MeaningScreen` exists. | `app/src/androidTest/java/com/kleros/meaning/MeaningScreenTest.kt` | Yes — RED via compile failure | T1 | S |
| T5 | Create MeaningScreen composable | Create `app/src/main/java/com/kleros/meaning/MeaningScreen.kt`, package `com.kleros.meaning`. Single `@Composable fun MeaningScreen(modifier: Modifier = Modifier)` that calls `TableScreen(tables = MeaningData.tables, diceType = DiceType.D100)`. No internal state — all state lives in TableScreen. Run `T4` tests to prove GREEN. | `app/.../meaning/MeaningScreen.kt` | Yes — GREEN for T4 | T3, T1 | XS |

---

## Phase 4: Navigation Wiring

| ID | Title | Description | Files | TDD | Deps | Effort |
|----|-------|-------------|-------|-----|------|--------|
| T6 | Wire Meaning screen into MainActivity | Add `MEANING("Meaning")` to the `Screen` enum in `MainActivity.kt`. Add `Screen.MEANING -> MeaningScreen()` branch in the `when` block. Import `MeaningScreen` from `com.kleros.meaning`. Run `./gradlew assembleDebug` to verify compilation. Manual verification: build and confirm the "Meaning" chip appears in app navigation. | `app/.../MainActivity.kt` | No (compilation check) | T5 | XS |

---

## Phase 5: Verification

| ID | Title | Description | Files | TDD | Deps | Effort |
|----|-------|-------------|-------|-----|------|--------|
| T7 | Run full test suite and verify | Execute `./gradlew test` (unit tests) and `./gradlew connectedAndroidTest` (instrumented, if emulator available). Confirm all existing + new tests pass. Verify JaCoCo coverage threshold passes. | — | N/A | T1–T6 | XS |

---

## Dependency Graph

```
T0 (spec doc)     — standalone
T2 (data test)    — standalone (compile-RED)
  └─> T3 (data)   — GREEN for T2
       └─> T5 (screen)  — needs T3 for MeaningData + T1 for TableScreen
T4 (screen test)  — depends-on T1 (needs diceType param signature)
  └─> T5 (screen) — GREEN for T4
T1 (diceType)     — standalone (enabling change)
  ├─> T4 (screen test) — needs diceType for compile-GREEN
  └─> T5 (screen) — needs diceType for compile-GREEN
T6 (navigation)   — depends-on T5
T7 (verification) — depends-on all
```

**Merge order:** T0 → T1 → (T2 → T3) || T4 → T5 → T6 → T7

Phases 2 and 3 (data + screen) can be developed in parallel after T1
completes. T2/T3 data track is pure unit-testable; T4/T5 screen track needs T1
for the diceType param but can start immediately after.

---

## Delivery Forecast

| Metric | Value |
|--------|-------|
| Total files changed | 7 (2 modified, 4 new, 1 doc) |
| Production delta | ~80 lines |
| Test delta | ~60 lines |
| Total delta | ~140 lines |
| New gradle deps | 0 |
| Phases | 0–5 (6 phases) |
| Parallelizable | T2/T3 (data) vs T4/T5 (screen) after T1 |
| Min sequential steps | 4 (T0 → T1 → T5 → T6) |
| Risk level | Low — additive changes, backward-compat default, no migrations |
