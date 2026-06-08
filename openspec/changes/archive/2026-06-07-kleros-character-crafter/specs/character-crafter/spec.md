# Character Crafter Specification

## Purpose

Quick NPC generation for GMs. Rolls 4 independent d100 for descriptors (Identity, Mind, Body, Talent) from a shared 100-entry pool, plus 1d10-driven 5-tier statistics. Displays as a result card with immutable newest-first history (max 10).

## Data Definitions

| Symbol | Domain | Description |
|--------|--------|-------------|
| `Slots` | `{Identity, Mind, Body, Talent}` | Descriptor categories |
| `Pool` | `Array<(Slot, String)>(100)` | Shared compile-time constant word list |
| `DescriptorMap` | `Map<Slot, String>` | One descriptor per generated slot |
| `StatTier` | `{50p_lower, 25p_lower, expected, 25p_higher, 50p_higher}` | Relative ability tier |
| `StatMap` | `Map<String, StatTier>` | Named statistic to tier mapping |
| `CharacterResult` | data class | `(id, descriptors, statistics, timestamp)` |
| `History` | `List<CharacterResult>` | Immutable, max 10, newest-first |

## Interface Definition

| Symbol | Signature | Description |
|--------|-----------|-------------|
| `CharacterData.entries` | compile-time `List<Pair<Slot, String>>` | 100 shared descriptor entries |
| `CharacterGenerator.generate` | `(DiceRoller.roll) -> CharacterResult` | 4× d100 descriptor lookup + 5× d10 stat tier |
| `CharacterHistory.add` | `(CharacterResult) -> History` | Returns new list, prepends, caps at 10 |
| `CharacterScreen` | `@Composable (history, onGenerate) -> Unit` | Button + result card + history list |

## Requirements

### R1: Shared Descriptor Pool

The system MUST provide a compile-time constant pool (`@file:Suppress("MagicNumber")`) of exactly 100 `(Slot, String)` entries distributed across 4 slots.

#### Scenario: Pool is complete

- GIVEN `CharacterData.entries`
- THEN the list MUST contain exactly 100 entries
- AND each slot MUST have at least 20 entries

#### Scenario: All four slots populated

- GIVEN `CharacterData.entries`
- WHEN grouped by slot
- THEN each of the 4 slots MUST have at least one entry

### R2: Character Generation

The generator MUST roll 4 independent d100 rolls (one per slot) for descriptor lookup and 5× d10 per statistic mapped to the 5-tier table: d10 1-2 → 50p_lower, 3-4 → 25p_lower, 5-6 → expected, 7-9 → 25p_higher, 10 → 50p_higher.

#### Scenario: Full happy-path generation

- GIVEN a `rollFn` that returns `[12, 55, 88, 3]` for d100 and `[6]` for d10
- WHEN `generate(rollFn)` is called
- THEN each slot SHALL receive one descriptor from the pool
- AND statistics SHALL map to `expected` tier
- AND the result SHALL contain a unique id and timestamp

#### Scenario: Duplicate descriptor across slots

- GIVEN the pool contains the same word in `Identity` and `Talent`
- WHEN two independent d100 rolls select that word for both slots
- THEN the result MAY contain identical descriptor text in both slots
- AND this SHALL NOT be treated as an error

#### Scenario: Stat tier boundaries

- GIVEN a `rollFn` returning d10 values `[2, 4, 7, 10, 1]`
- WHEN `generate(rollFn)` executes
- THEN tiers SHALL map respectively to 50p_lower, 25p_lower, 25p_higher, 50p_higher, 50p_lower

### R3: Result Display

The screen MUST render a "Generate Character" button. On tap, it MUST display a result card with 4 descriptors and statistics. Before first generation, no card SHALL appear.

#### Scenario: Initial empty screen

- GIVEN no character has been generated
- WHEN `CharacterScreen` renders
- THEN only the "Generate Character" button SHALL be visible
- AND no result card SHALL be present

#### Scenario: Card displayed after generation

- GIVEN the user taps "Generate Character"
- WHEN the screen recomposes
- THEN 4 slot labels and their descriptor values SHALL be visible
- AND the statistics block SHALL display tier labels for each stat

### R4: Immutable History

History MUST be an immutable list capped at 10 entries, ordered newest-first. Adding to a full history MUST evict the oldest entry without mutating the existing list.

#### Scenario: Accumulates entries up to cap

- GIVEN an empty `CharacterHistory`
- WHEN 10 results are added sequentially
- THEN the list MUST contain exactly 10 entries
- AND index 0 MUST be the most recently added

#### Scenario: Full history evicts oldest

- GIVEN a `CharacterHistory` with 10 entries
- WHEN an 11th result is added
- THEN the returned list MUST contain exactly 10 entries
- AND the oldest entry (index 9 from prior state) SHALL NOT be present
- AND the original list SHALL remain unchanged (immutability)
