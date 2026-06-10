# Adventure Crafter Specification

## Purpose

Five 1d100 plot-prompt tables (Action, Tension, Mystery, Social, Personal) for story inspiration. Wraps `TableScreen` with `DiceType.D100`. Pure data addition — no new infrastructure.

## Requirements

### Requirement: Data Completeness — 5 Tables × 100 DIRECT Entries

The system MUST define a static `List<TableDef>` constant `ADVENTURE_CRAFTER_DATA` containing exactly 5 tables named "Action", "Tension", "Mystery", "Social", and "Personal". Each table MUST have exactly 100 `TableEntry.DIRECT` entries, one per integer index 1 through 100.

#### Scenario: All tables have 100 entries

- GIVEN the `ADVENTURE_CRAFTER_DATA` constant
- WHEN counting entries in each table
- THEN each of the 5 tables has exactly 100 entries

#### Scenario: Each table covers indices 1–100 with no gaps

- GIVEN `ADVENTURE_CRAFTER_DATA`
- WHEN enumerating all DIRECT indices per table
- THEN every integer from 1 to 100 appears exactly once in each table
- AND no index is duplicated or missing

### Requirement: AdventureCrafterScreen Composable

The system SHALL expose an `AdventureCrafterScreen` composable wrapping `TableScreen` with all 5 tables and `diceType = DiceType.D100`. All 5 tables MUST be visible as selectable FilterChips.

#### Scenario: Five FilterChips visible

- GIVEN AdventureCrafterScreen is composed
- WHEN inspecting the FilterChip row
- THEN "Action", "Tension", "Mystery", "Social", and "Personal" chips are all visible

#### Scenario: Roll returns correct DIRECT result

- GIVEN AdventureCrafterScreen with Action table selected and `rollFn` returning 42
- WHEN the user taps "Roll"
- THEN the displayed result matches the DIRECT entry at index 42

#### Scenario: History accumulates across table switches

- GIVEN the user rolls Action twice, then switches to Mystery and rolls twice
- WHEN inspecting the history list
- THEN all 4 entries are visible, newest at top

### Requirement: Navigation Integration

The system SHALL add `ADVENTURE_CRAFTER("Adv Craft", Icons.Filled.AutoAwesome)` to the `Screen` enum and wire a `when` branch in `AppNavigation` that imports and renders `AdventureCrafterScreen`.

#### Scenario: Adventure Crafter in navigation drawer

- GIVEN the app is running
- WHEN opening the navigation drawer
- THEN an "Adv Craft" item is present with the AutoAwesome icon
- AND tapping it renders AdventureCrafterScreen

## Non-Functional Requirements

- All data MUST be compile-time constants (no runtime initialization)
- `AdventureCrafterData` MUST reside in package `com.kleros.adventure`
- No new Gradle dependencies MAY be added
