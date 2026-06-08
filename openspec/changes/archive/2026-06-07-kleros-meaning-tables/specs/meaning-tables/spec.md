# Meaning Tables Specification

## Purpose

Two 1d100 word tables (Action, Description) for scene inspiration in RPG oracles. The screen wraps `TableScreen` with `DiceType.D100` and presents both tables as selectable FilterChips.

## Requirements

### Requirement: Action Table Data

The system MUST define a static `TableDef` constant `MEANING_ACTION` with 50 `TableEntry.RANGE` entries covering roll values 1 through 100. Each entry MUST span exactly 2 consecutive integers (1-2, 3-4, ..., 99-100).

#### Scenario: Action table covers full range

- GIVEN the `MEANING_ACTION` table
- WHEN enumerating all entry ranges
- THEN every integer from 1 to 100 is covered exactly once

#### Scenario: Action table has 50 entries

- GIVEN the `MEANING_ACTION` table
- WHEN counting entries
- THEN there are exactly 50

### Requirement: Description Table Data

The system MUST define a static `TableDef` constant `MEANING_DESCRIPTION` with 50 `TableEntry.RANGE` entries covering roll values 1 through 100. Each entry MUST span exactly 2 consecutive integers.

#### Scenario: Description table covers full range

- GIVEN the `MEANING_DESCRIPTION` table
- WHEN enumerating all entry ranges
- THEN every integer from 1 to 100 is covered exactly once

#### Scenario: Description table has 50 entries

- GIVEN the `MEANING_DESCRIPTION` table
- WHEN counting entries
- THEN there are exactly 50

### Requirement: MeaningScreen Composable

The system SHALL expose a `MeaningScreen` composable wrapping `TableScreen` with both meaning tables and `diceType = DiceType.D100`. Both tables MUST be visible as selectable FilterChips.

#### Scenario: Both FilterChips visible

- GIVEN MeaningScreen is composed
- WHEN inspecting the FilterChip row
- THEN "Action" and "Description" chips are both visible

#### Scenario: Rolling returns valid word

- GIVEN MeaningScreen with Action table selected
- WHEN the user taps "Roll"
- THEN the displayed result is one of the 50 Action table words

#### Scenario: History accumulates across table switches

- GIVEN the user rolls Action 3 times, then switches to Description and rolls twice
- WHEN inspecting the history list
- THEN all 5 entries are visible, newest at top

### Requirement: Package Structure

The system MUST place `MeaningData` constants in package `com.kleros.meaning`. MeaningScreen MUST reside in `com.kleros.meaning` (UI) or a sub-package thereof.

#### Scenario: Domain constants in correct package

- GIVEN the source file `MeaningData.kt`
- WHEN checking its package declaration
- THEN it SHALL be `com.kleros.meaning`

## Non-Functional Requirements

- All meaning table data MUST be compile-time constants (no runtime initialization)
- Word entries MUST NOT exceed 30 characters each
- No new Gradle dependencies MAY be added
