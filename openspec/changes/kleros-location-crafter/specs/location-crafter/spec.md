# Location Crafter Specification

## Purpose

Procedural location generation with stateful PP (progress points). Each area produces 1 descriptor (d100) + 3 elements (2d10+PP lookup). PP advances per generation, scaling element variety. Region size selects the starting PP budget.

## Requirements

### Requirement: LocationData — 100 Descriptors + 10 Element Ranges

The system MUST define a static constant `LOCATION_CRAFTER_DATA` containing 100 `TableEntry.DIRECT` entries (indices 1–100) for descriptors and exactly 10 `TableEntry.RANGE` entries covering the 2d10+PP span (2–30+) for elements.

#### Scenario: All 100 DIRECT descriptor indices present

- GIVEN `LOCATION_CRAFTER_DATA`
- WHEN counting DIRECT entries
- THEN exactly 100 entries exist, one per integer 1 through 100, with no gaps or duplicates

#### Scenario: 10 RANGE entries cover full 2d10+PP span

- GIVEN `LOCATION_CRAFTER_DATA`
- WHEN inspecting the RANGE table
- THEN exactly 10 entries cover the cumulative range from 2 to at least 30, with no uncovered gaps between entries

### Requirement: PP-Progression Generation

The system SHALL expose `LocationCrafter.generate(regionSize, rollFn)` that returns a `LocationResult` containing 1 descriptor (d100 lookup) and 3 elements (2d10+PP lookup each). PP MUST increment by exactly 1 after each call.

#### Scenario: Generate returns 1 descriptor + 3 elements

- GIVEN starting PP = 5
- WHEN calling `generate(AVERAGE, rollFn)` with `rollFn` returning 42, 7, 8, 9
- THEN the result descriptor matches DIRECT index 42
- AND the result contains exactly 3 element strings from RANGE lookups

#### Scenario: PP increments after each generation

- GIVEN starting PP = 5
- WHEN calling `generate` three times
- THEN the first call uses PP=5, the second PP=6, the third PP=7

### Requirement: Region Size Selection

The system SHALL provide three region sizes: Small (starting PP = 3), Average (5), Large (7), exposed as a selector on `LocationScreen`. Changing the region size MUST reset PP to that size's starting value.

#### Scenario: Selecting region size sets correct starting PP

- GIVEN `LocationScreen` is composed
- WHEN the user selects Small
- THEN the PP counter displays 3
- WHEN the user selects Large
- THEN the PP counter displays 7

#### Scenario: Changing region size mid-session resets PP

- GIVEN PP is at 8 after several generations with Average
- WHEN the user selects Small
- THEN PP resets to 3
- AND subsequent generations begin from PP=3

### Requirement: LocationScreen Composable

The system SHALL expose a `LocationScreen` composable with a region-size chip selector, live PP counter, a generate button, a result card (descriptor + 3 elements + PP used), and a history list capped at 10 entries (newest first).

#### Scenario: All UI controls visible at initial state

- GIVEN `LocationScreen` is composed
- WHEN inspecting the screen
- THEN Small/Average/Large chips are visible, PP counter shows 5 (default), Generate button is enabled, and no result card or history entries are shown

#### Scenario: History caps at 10 entries, newest first

- GIVEN the user generates 12 areas
- WHEN inspecting the history list
- THEN exactly 10 entries are visible
- AND entries are ordered newest (top) to oldest (bottom)

## Non-Functional Requirements

- `LOCATION_CRAFTER_DATA` MUST be a compile-time constant in `com.kleros.location`
- All generator logic MUST be pure (no side effects) — `rollFn` is injected for testability
- No new Gradle dependencies SHALL be introduced
