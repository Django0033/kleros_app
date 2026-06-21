# Mystery Crafter Specification

## Purpose

Progressive discovery mechanic for mysteries, investigations, and uncovering secrets. An accumulator (boxes) stacks per roll, capping at 20, so each attempt gets closer to the truth. Definitive answer at 101+.

## Requirements

### R1: Mystery Data Tables — 7 Discovery Tiers + 100 Descriptors

The system MUST define compile-time constant `MYSTERY_DATA` containing:
- Exactly 7 `TableEntry.RANGE` entries for discovery, collectively covering integers 1 through 100 with no gaps or overlaps.
- Exactly 100 `TableEntry.DIRECT` entries for flavor descriptors, one per integer index 1 through 100.

#### Scenario: 7 RANGE entries cover 1–100

- GIVEN `MYSTERY_DATA.discovery`
- WHEN enumerating all RANGE boundaries
- THEN every integer 1 through 100 SHALL be covered by exactly one entry
- AND there SHALL be exactly 7 entries

#### Scenario: Descriptor pool has 100 DIRECT entries

- GIVEN `MYSTERY_DATA.descriptors`
- WHEN counting DIRECT entries
- THEN there SHALL be exactly 100 entries at indices 1 through 100
- AND no index SHALL be duplicated or missing

### R2: Accumulator Check — Progressive Roll

`MysteryCrafter.check(rollFn, boxes)` MUST roll 1d100, add `boxes` to the result, and:
- If `roll + boxes > 100` → return `MysteryResult` with `isDefinitive = true` and `discovery` set to a predefined definitive string.
- If `roll + boxes <= 100` → look up the matching RANGE entry by `roll + boxes` and set `discovery` accordingly.
- In all cases, a descriptor SHALL be rolled from the 100-entry DIRECT pool.

#### Scenario: Under-threshold roll returns RANGE discovery

- GIVEN `boxes = 5` and `rollFn` returns 42
- WHEN `check(rollFn, 5)` is called
- THEN `roll + boxes = 47`
- AND the result's `discovery` SHALL match the RANGE entry covering index 47
- AND `isDefinitive` SHALL be `false`
- AND `descriptor` SHALL be non-null

#### Scenario: Overflow roll returns definitive

- GIVEN `boxes = 18` and `rollFn` returns 85
- WHEN `check(rollFn, 18)` is called
- THEN `roll + boxes = 103 (> 100)`
- AND the result SHALL have `isDefinitive = true`
- AND `descriptor` SHALL still be a valid DIRECT entry

#### Scenario: Descriptor always drawn irrespective of outcome

- GIVEN any `boxes` and `rollFn`
- WHEN `check(rollFn, boxes)` executes
- THEN the returned `MysteryResult.descriptor` SHALL match a valid DIRECT entry at index `1d100`

### R3: Box Accumulation — Increment and Cap

The system MUST track boxes as an integer in screen-level state. Each call to `check(rollFn, boxes)` MUST be followed by incrementing boxes by 1, capping at 20. Boxes SHALL NOT exceed 20 and SHALL never decrement during a session.

#### Scenario: Boxes increment each check to 20

- GIVEN `boxes = 12`
- WHEN `check(rollFn, boxes)` returns and boxes is incremented 8 times
- THEN after the 8th increment, `boxes = 20`
- AND the 9th increment leaves `boxes = 20`

#### Scenario: Boxes at zero start

- GIVEN a fresh `MysteryScreen`
- THEN the initial `boxes` value SHALL be 0

### R4: Result Display and History

The system MUST display current boxes, a "Investigate" button, and a result card showing discovery, descriptor, and definitive status after each check. History MUST be an immutable list capped at 10 entries, newest-first, evicting oldest when full.

#### Scenario: Full history evicts oldest

- GIVEN a history list with 10 `MysteryResult` entries
- WHEN an 11th result is added
- THEN the returned list SHALL contain exactly 10 entries
- AND the oldest entry SHALL NOT be present
- AND the original list SHALL be unchanged

#### Scenario: History ordered newest-first

- GIVEN entry A at index 0 and entry B at index 1 after adding B then A
- WHEN inspecting the history list
- THEN index 0 SHALL hold A (most recent)
- AND index 9 SHALL hold the oldest entry
