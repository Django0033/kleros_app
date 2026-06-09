# Creature Crafter Specification

## Purpose

Quick creature generation for GMs. Rolls 2× d100 descriptors (weighted pool), 2× d100 abilities (50 paired entries), 1× d10 initial behavior, and 1× d10 statistics (reused from CharacterData). Post-gen mutations refine on the fly.

## Data Definitions

| Symbol | Domain | Description |
|--------|--------|-------------|
| `DescriptorTable` | `List<String>(100)` | 100 weighted compile-time entries (1d100)* |
| `AbilityTable` | `List<String>(100)` | 50 paired compile-time entries, each appearing at (2n-1, 2n) |
| `BehaviorInitial` | `List<String>(10)` | 10 entries (1d10), e.g. index 1=Inert, 2-3=Moving... |
| `BehaviorNew` | `List<String>(10)` | 10 entries (1d10), 1-6=Acts as expected, 10=Exhibits Ability |
| `StatTable` | reused from `CharacterData.statistics` | 5-tier d10 (1=50p_lower...10=50p_higher) |
| `CreatureResult` | data class | `(descriptors, abilities, initialBehavior, statistics, newBehavior?, timestamp)` |
| `History` | `List<CreatureResult>` | Immutable, max 10, newest-first |

\* Weights achieved by duplicate entries at adjacent indices (e.g. Loud at 2,3).

## Interface Definition

| Symbol | Signature | Description |
|--------|-----------|-------------|
| `CreatureCrafter.generate` | `(rollFn) -> CreatureResult` | 2×d100 descriptors + 2×d100 abilities + 1×d10 behavior + 1×d10 stat |
| `CreatureCrafter.rollDescriptor` | `(CreatureResult, rollFn) -> CreatureResult` | Appends 1 descriptor, returns new copy |
| `CreatureCrafter.rollAbility` | `(CreatureResult, rollFn) -> CreatureResult` | Appends 1 ability, returns new copy |
| `CreatureCrafter.rollNewBehavior` | `(CreatureResult, rollFn) -> CreatureResult` | Sets `newBehavior` if null, else no-op |
| `CreatureHistory.add` | `(CreatureResult) -> History` | Returns new list, prepends, caps at 10 |
| `CreatureScreen` | `@Composable (history, onGenerate, onRollDescriptor, onRollAbility, onNewBehavior) -> Unit` | Button + result card + mutation buttons + history |

## Requirements

### R1: Creature Data Tables

The system MUST provide compile-time constant tables: descriptors (100 weighted `DIRECT`), abilities (100 = 50 paired `DIRECT`), behavior_initial (10 `DIRECT`), behavior_new (10 `DIRECT`). Statistics MUST reuse `CharacterData.statistics`.

#### Scenario: Descriptor table has 100 entries

- GIVEN `CreatureData.descriptors`
- THEN it MUST contain exactly 100 `TableEntry.DIRECT` entries
- AND at least 2 entries MUST share the same result (weighting evidence)

#### Scenario: Ability table uses paired entries

- GIVEN `CreatureData.abilities`
- THEN it MUST contain exactly 100 entries
- AND entries at indices (1,2), (3,4), ..., (99,100) MUST form 50 pairs with matching results

#### Scenario: Statistics table imported from CharacterData

- GIVEN `CreatureData.statistics === CharacterData.statistics`
- THEN the reference MUST be the same object (no copy)

### R2: Creature Generation

The generator MUST roll 2× d100 for descriptors, 2× d100 for abilities, 1× d10 for initial behavior, and 1× d10 for statistics. `newBehavior` MUST be `null` on initial generation.

#### Scenario: Full happy-path generation

- GIVEN `rollFn` returns `[12, 55]` for d100 descriptors, `[30, 71]` for d100 abilities, `[4]` for d10 behavior, `[6]` for d10 stat
- WHEN `generate(rollFn)` is called
- THEN `descriptors` SHALL contain exactly 2 strings
- AND `abilities` SHALL contain exactly 2 strings
- AND `initialBehavior` SHALL be non-null
- AND `statistics` SHALL be non-null
- AND `newBehavior` SHALL be `null`

#### Scenario: Duplicate descriptor or ability

- GIVEN the same index is rolled twice for descriptors (or abilities)
- WHEN `generate(rollFn)` executes
- THEN the result MAY contain duplicate strings in that list
- AND this SHALL NOT be treated as an error

#### Scenario: Initial behavior d10 boundaries

- GIVEN `rollFn` returns `[1, 7, 10]` for behavior d10
- WHEN `generate(rollFn)` is called 3 times with these values
- THEN behavior SHALL map to indices 1, 7, and 10 respectively

### R3: Post-Generation Mutations

The system MUST provide 3 mutation operations that accept a `CreatureResult` and `rollFn`, returning a new `CreatureResult` copy. Mutations MUST NOT mutate the original.

#### Scenario: Roll Descriptor appends 3rd descriptor

- GIVEN a `CreatureResult` with 2 descriptors
- WHEN `rollDescriptor(result, rollFn)` is called
- THEN a new `CreatureResult` SHALL be returned
- AND `descriptors.size` SHALL be 3
- AND the original result's descriptors SHALL still be size 2

#### Scenario: Roll Ability appends 3rd ability

- GIVEN a `CreatureResult` with 2 abilities
- WHEN `rollAbility(result, rollFn)` is called
- THEN a new `CreatureResult` SHALL be returned
- AND `abilities.size` SHALL be 3

#### Scenario: New Behavior sets newBehavior on first call

- GIVEN a `CreatureResult` with `newBehavior == null`
- WHEN `rollNewBehavior(result, rollFn)` is called
- THEN the returned result SHALL have `newBehavior != null`

#### Scenario: New Behavior is no-op when already set

- GIVEN a `CreatureResult` with `newBehavior = "Acts as expected"`
- WHEN `rollNewBehavior(result, rollFn)` is called
- THEN the returned result SHALL have the same `newBehavior` value
- AND `rollFn` SHALL NOT be invoked

### R4: Result Display

The screen MUST render a "Generate Creature" button and, after generation, display a result card with descriptors, abilities, behavior, statistics, and 3 mutation buttons (Roll Descriptor, Roll Ability, New Behavior).

#### Scenario: Initial empty screen

- GIVEN no creature has been generated
- WHEN `CreatureScreen` renders
- THEN only the "Generate Creature" button SHALL be visible
- AND no result card SHALL be present

#### Scenario: Card displayed after generation

- GIVEN the user taps "Generate Creature"
- WHEN the screen recomposes
- THEN descriptors, abilities, initial behavior, and statistics SHALL be visible
- AND 3 mutation buttons SHALL be present

### R5: Immutable History

History MUST be an immutable list capped at 10 entries, ordered newest-first. Adding to a full history MUST evict the oldest entry.

#### Scenario: Accumulates entries up to cap

- GIVEN an empty `CreatureHistory`
- WHEN 10 results are added sequentially
- THEN the list MUST contain exactly 10 entries
- AND index 0 MUST be the most recently added

#### Scenario: Full history evicts oldest

- GIVEN a `CreatureHistory` with 10 entries
- WHEN an 11th result is added
- THEN the returned list MUST contain exactly 10 entries
- AND the oldest entry SHALL NOT be present
- AND the original list SHALL remain unchanged
