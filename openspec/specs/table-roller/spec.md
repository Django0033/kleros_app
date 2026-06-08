# Table Roller Specification

## Purpose

8 upcoming RPG oracles (faction names, plot hooks, NPC traits) each need a roll-on-table engine. This spec defines a generic `TableRoller` and reusable `TableScreen` composable so each oracle is just data — entries and optional interpreter.

## Requirements

### Requirement: TableEntry Types

The system MUST define a sealed class `TableEntry` with three variants:
- `RANGE(min: Int, max: Int, result: String)` — resolved when `min <= roll <= max`
- `DIRECT(index: Int, result: String)` — resolved when `roll == index`
- `RANGE_MODIFIER(min: Int, max: Int, result: String, modifier: Int)` — rolls within range, then applies modifier and clamps to range

#### Scenario: RANGE finds correct entry

- GIVEN a table with entries `RANGE(1, 5, "A")` and `RANGE(6, 10, "B")`
- WHEN rolling 3 with a deterministic rollFn
- THEN the result is `"A"`

#### Scenario: DIRECT resolves by exact index

- GIVEN a table with `DIRECT(1, "Eagle")`, `DIRECT(2, "Lion")`, `DIRECT(3, "Serpent")`
- WHEN rolling 2
- THEN the result is `"Lion"`

#### Scenario: RANGE_MODIFIER applies modifier after roll

- GIVEN `RANGE_MODIFIER(1, 10, "Hit", modifier = 2)`
- WHEN roll produces 4
- THEN effective value is 6, within range, result is `"Hit"`

#### Scenario: RANGE_MODIFIER negative modifier floors at min

- GIVEN `RANGE_MODIFIER(1, 6, "Bump", modifier = -3)`
- WHEN roll produces 2
- THEN effective value is clamped to 1 (min), result is `"Bump"`

#### Scenario: RANGE_MODIFIER large modifier caps at max

- GIVEN `RANGE_MODIFIER(1, 6, "Critical", modifier = 10)`
- WHEN roll produces 4
- THEN effective value is clamped to 6 (max), result is `"Critical"`

### Requirement: TableRoller

The system MUST expose a pure function `TableRoller.roll(table: TableDef, rollFn: () -> Int): TableRollResult` with no Android dependencies. It dispatches by entry type: RANGE finds first matching range, DIRECT matches by index, RANGE_MODIFIER ranges then clamps.

#### Scenario: rollFn injection produces expected result

- GIVEN a deterministic rollFn `{ 7 }` and a RANGE table covering 1..10
- WHEN `TableRoller.roll(table, rollFn)` is called
- THEN the result matches the entry containing 7

#### Scenario: No matching entry returns error

- GIVEN a table with RANGE(1, 5) and a rollFn returning 10
- WHEN `TableRoller.roll` is called
- THEN the result is `TableRollResult.Error("No entry matches roll 10")`

### Requirement: TableDef

The system SHALL define `data class TableDef(val name: String, val entries: List<TableEntry>)` as an entry group. The name is used for FilterChip selection in TableScreen.

### Requirement: TableRollResult

The system SHALL define a sealed class `TableRollResult`:
- `Success(value: String)` — the resolved result text
- `Error(message: String)` — no matching entry

### Requirement: TableHistory

The system SHALL define `TableHistory` as an immutable data class ordered newest-first, capped at 10 entries. Adding beyond capacity MUST evict the oldest.

#### Scenario: Empty history

- GIVEN a fresh `TableHistory`
- WHEN `entries` is accessed
- THEN it is empty

#### Scenario: History caps at 10

- GIVEN `TableHistory` with 10 entries
- WHEN an 11th is added
- THEN `entries` has exactly 10
- AND the oldest entry is removed

### Requirement: TableScreen Composables

The system SHALL provide a `TableScreen(tables: List<TableDef>)` composable with:
- A row of `FilterChip` selectors (one per table), first selected by default
- A roll button
- The current result display
- A capped history list (newest-first)

State MUST be composable-local (`remember` / `mutableStateOf`). No ViewModel. No new Gradle deps.

#### Scenario: Select table via FilterChip and roll

- GIVEN `TableScreen` renders with 3 tables
- WHEN the user taps the second FilterChip, then taps "Roll"
- THEN the selected table is used for the roll
- AND the result is displayed

#### Scenario: Multiple rolls fill history

- GIVEN the user taps "Roll" 5 times
- WHEN checking the history list
- THEN all 5 entries are visible, newest at top

#### Scenario: History scrolls past 10 rolls

- GIVEN the user taps "Roll" 12 times
- WHEN checking the history list
- THEN exactly 10 entries are visible
- AND the oldest 2 rolls are evicted

## Non-Functional Requirements

- `TableRoller.roll` MUST complete in under 1ms
- All pure domain types MUST reside in package `com.kleros.table`
- The domain layer MUST have zero Android dependencies
- rollFn defaults to `{ DiceRoller.roll(DiceType.D20) }` when not provided
- No new Gradle dependencies MAY be added

## Data Definitions

```kotlin
sealed class TableEntry {
    data class RANGE(val min: Int, val max: Int, val result: String) : TableEntry()
    data class DIRECT(val index: Int, val result: String) : TableEntry()
    data class RANGE_MODIFIER(val min: Int, val max: Int, val result: String, val modifier: Int) : TableEntry()
}

data class TableDef(val name: String, val entries: List<TableEntry>)

sealed class TableRollResult {
    data class Success(val value: String) : TableRollResult()
    data class Error(val message: String) : TableRollResult()
}

data class TableHistory(
    val entries: List<TableRollResult.Success> = emptyList(),
    val maxSize: Int = 10
)

object TableRoller {
    fun roll(table: TableDef, rollFn: () -> Int = { DiceRoller.roll(DiceType.D20) }): TableRollResult
}

// UI — Composable, not shown here
// @Composable fun TableScreen(tables: List<TableDef>)
```

## Package Structure

```
com.kleros.table/
├── TableEntry.kt         // Sealed class
├── TableDef.kt           // Data class
├── TableRoller.kt        // Pure roll() function
├── TableRollResult.kt    // Sealed result
├── TableHistory.kt       // Capped list
└── TableScreen.kt        // Composable UI
```
