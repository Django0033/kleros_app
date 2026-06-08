# Archive Report: Meaning Tables

**Status**: ✅ Complete
**Date**: 2026-06-07
**Change**: kleros-meaning-tables

## Summary
Added two 1d100 word tables (Action + Description) for scene inspiration, reusing TableRoller and TableScreen.

## Files
- 4 new source files + 2 modified
- 2 test files (1 unit + 1 UI)
- ~711 lines added, 1 deleted

## Dependencies
- TableRoller (existing)
- DiceRoller (existing)

## Changes
- Added `diceType` param to `TableScreen` (backward-compatible default D20)
- Added `MeaningData` with 50 Action + 50 Description entries
- Added `MeaningScreen` wrapper with `DiceType.D100`
- Wired `Screen.MEANING` into MainActivity navigation

## Tests
- 8 unit tests (data integrity: ranges, counts, first/last)
- 3 UI tests (chips render, button render)
- `./gradlew test` ✅, `./gradlew detekt` ✅
