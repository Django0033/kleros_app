# Delta for table-roller

Change: kleros-meaning-tables — TableScreen gains optional `diceType` parameter to support non-D20 tables.

## MODIFIED Requirements

### Requirement: TableScreen Composables

The system SHALL provide a `TableScreen(tables: List<TableDef>, diceType: DiceType = DiceType.D20)` composable with:
- A row of `FilterChip` selectors (one per table), first selected by default
- A roll button that SHALL use `DiceRoller.roll(diceType)` internally
- The current result display
- A capped history list (newest-first)

State MUST be composable-local (`remember` / `mutableStateOf`). No ViewModel. No new Gradle deps.

(Previously: TableScreen accepted only `tables`, hardcoded `DiceType.D20`)

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

#### Scenario: Default D20 roll

- GIVEN `TableScreen` with a 20-entry table and no explicit diceType
- WHEN the user taps "Roll"
- THEN the result falls within 1..20

#### Scenario: Explicit D100 roll

- GIVEN `TableScreen` with a 100-entry table and `diceType = DiceType.D100`
- WHEN the user taps "Roll"
- THEN the result falls within 1..100
