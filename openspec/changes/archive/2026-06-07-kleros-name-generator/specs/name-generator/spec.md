# Name Generator Specification

## Purpose

Jurors need thematic placeholder names for cases, parties, and evidence references in Kleros workflows. This spec defines a 1d20-table-driven random name generator using pattern-parsed syllable concatenation with advantage/disadvantage support.

## Requirements

### Requirement: Syllable Table

The system MUST embed a static 20-row syllable table with columns `Pattern`, `Inicio1`, `Inicio2`, and `Ending`. The table MUST be a compile-time constant with no mutable state.

#### Scenario: Table has 20 rows

- GIVEN the syllable table is loaded
- WHEN counting rows
- THEN there MUST be exactly 20 rows

#### Scenario: Each row has all four columns

- GIVEN any row in the table
- THEN it MUST contain non-empty values for Pattern, Inicio1, Inicio2, and Ending

### Requirement: RollMode Selection

The system SHALL provide a roll mode selector with three modes: `NORMAL`, `ADVANTAGE`, `DISADVANTAGE`. NORMAL MUST be the default. Changing the mode MUST NOT reset history.

#### Scenario: User switches roll mode

- GIVEN the user is on the NameScreen with NORMAL selected
- WHEN the user selects ADVANTAGE
- THEN the mode indicator shows "ADVANTAGE"
- AND subsequent generation uses max(2d20) for the pattern roll

### Requirement: Name Generation

The system SHALL expose a pure function `NameGenerator.generate(rollMode: RollMode, rollFn: (DiceType) -> Int): String` with no Android dependencies. The function MUST produce names matching the parsed pattern structure.

#### Scenario: Normal roll produces a name

- GIVEN RollMode.NORMAL and a deterministic rollFn that returns predictable values
- WHEN `generate()` is called
- THEN a non-empty String is returned

#### Scenario: Advantage skews toward higher rows

- GIVEN RollMode.ADVANTAGE
- WHEN `generate()` is invoked 1000 times
- THEN the mean row index is higher than under NORMAL
- AND no errors occur

#### Scenario: Disadvantage skews toward lower rows

- GIVEN RollMode.DISADVANTAGE
- WHEN `generate()` is invoked 1000 times
- THEN the mean row index is lower than under NORMAL
- AND no errors occur

#### Scenario: 1000 invocations produce at least 50 unique names

- GIVEN the syllable table with 20 rows
- WHEN `generate()` is called 1000 times with NORMAL mode and random rolls
- THEN at least 50 distinct strings are produced

### Requirement: Pattern Parsing

The system MUST parse pattern strings into a sequence of operations. Each digit (1, 2, 3) selects a column. `3-` rolls Ending among first 10 rows only. `3+` rolls Ending among last 10 rows only. Non-digit letters after digits are suffix letters appended at the end.

#### Scenario: Pattern `12o` parses correctly

- GIVEN pattern string "12o"
- WHEN parsed
- THEN operations are: roll Inicio1, roll Inicio2, suffix "o"

#### Scenario: Pattern `3-` rolls only first 10 endings

- GIVEN pattern string "3-"
- WHEN parsed
- THEN the Ending roll is constrained to rows 1..10

#### Scenario: Pattern `123+` rolls only last 10 endings

- GIVEN pattern string "123+"
- WHEN the third operation parses
- THEN the Ending roll is constrained to rows 11..20

#### Scenario: Pattern `111` uses Inicio1 three times

- GIVEN pattern string "111"
- WHEN parsed
- THEN three consecutive Inicio1 rolls occur
- AND each is rolled independently

### Requirement: Parenthetical Prefix Rule

The system MUST resolve parenthetical tokens in syllable values: `(f)a` resolves to `"a"` when it is the first syllable generated, and to `"fa"` otherwise.

#### Scenario: Parenthetical token is first syllable

- GIVEN a syllable value of "(f)a"
- WHEN this is the first syllable being concatenated
- THEN the resolved value is "a"

#### Scenario: Parenthetical token is not first syllable

- GIVEN a syllable value of "(f)a"
- WHEN this is NOT the first syllable being concatenated
- THEN the resolved value is "fa"

### Requirement: NameResult

The system SHALL define `data class NameResult(val name: String, val timestamp: Long, val rollMode: RollMode)` to represent a single generated name with its display metadata.

#### Scenario: NameResult stores all fields

- GIVEN a name "Elara" generated with NORMAL mode at time T
- WHEN a NameResult is created
- THEN `name` is "Elara", `timestamp` is T, `rollMode` is NORMAL

### Requirement: NameHistory

The system SHALL maintain an immutable `NameHistory` data class ordered newest-first with a maximum capacity of 10 entries. Adding an 11th entry MUST evict the oldest.

#### Scenario: Empty history

- GIVEN a fresh NameHistory
- WHEN `rolls` is accessed
- THEN it is empty

#### Scenario: History caps at 10

- GIVEN NameHistory already contains 10 entries
- WHEN an 11th entry is added
- THEN `rolls` has exactly 10 entries
- AND the oldest entry is removed

### Requirement: NameScreen

The system SHALL display a `NameScreen` composable containing: a roll mode selector (NORMAL/ADVANTAGE/DISADVANTAGE), a "Generate" button, the current generated name, and the history list. The screen MUST use only existing Compose dependencies.

#### Scenario: Generate button produces name

- GIVEN the user is on the NameScreen
- WHEN the user taps "Generate"
- THEN a name is displayed
- AND the history shows 1 entry

#### Scenario: History list updates

- GIVEN the user has generated 5 names
- WHEN a 6th name is generated
- THEN the history shows 6 entries
- AND the newest entry is at the top

## Non-Functional Requirements

- The generation function MUST complete in under 1ms
- All new code MUST reside in package `com.kleros.namegenerator`
- No new Gradle dependencies MAY be added beyond what the template provides (reuses DiceRoller)
- The syllable table MUST use `@file:Suppress("MagicNumber")` to avoid detekt violations
