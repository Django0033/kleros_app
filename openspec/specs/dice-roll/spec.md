# Dice Roll Specification

## Purpose

Users need a fair, instant random number generator for Kleros on-chain tie-breakers, juror games, and gamification. This replaces the template "Hello Android!" screen with a functional dice-rolling interface.

## Requirements

### Requirement: Dice Type Selection

The system MUST provide a selector offering all 7 standard dice types: D4, D6, D8, D10, D12, D20, and D100. The selector MUST show the current selection prominently. Changing the selection MUST NOT reset the roll history.

#### Scenario: User switches dice type

- GIVEN the user is on the DiceScreen
- WHEN the user selects "D20" from the dice type selector
- THEN the selected type indicator shows "D20"
- AND subsequent rolls return values in 1..20

#### Scenario: Dice types produce documented ranges

- GIVEN each dice type in [D4, D6, D8, D10, D12, D20, D100]
- WHEN rolled 1000 times
- THEN every roll falls in 1..N where N is the face count
- AND every value in 1..N appears at least once

### Requirement: Dice Roll Execution

The system SHALL roll the selected die on a button tap. The roll result MUST be determined by a pure function `DiceRoller.roll(DiceType): Int` with no side effects and no Android dependencies. The roll function MUST produce uniformly distributed values across the full range.

#### Scenario: Happy path — roll succeeds

- GIVEN the user selected "D6" and sees the roll button
- WHEN the user taps "Roll"
- THEN a random integer in 1..6 is displayed

#### Scenario: Edge case — D100 range

- GIVEN the user selected "D100"
- WHEN the roller is invoked
- THEN the result is in 1..100 (inclusive)

### Requirement: Result Animation

The system SHOULD animate the numerical result when it changes. The animation MUST use `animateIntAsState` with a spring effect, scaling the displayed number from previous to new value.

#### Scenario: Result animates on roll

- GIVEN the previous roll result is displayed
- WHEN a new roll yields a different value
- THEN the displayed number animates through intermediate values

### Requirement: Roll History

The system MUST display a roll history list showing the last 10 rolls. History entries MUST be ordered newest-first, each showing dice type, rolled value, and timestamp.

#### Scenario: History collects rolls

- GIVEN the user has not rolled yet
- WHEN the first roll completes
- THEN the history shows exactly 1 entry

#### Scenario: History caps at 10

- GIVEN history already contains 10 entries
- WHEN an 11th roll completes
- THEN history shows exactly 10 entries
- AND the oldest entry is removed

### Requirement: Screen Integration

The DiceScreen MUST replace `Greeting()` as the default content in MainActivity. The app MUST show the dice interface on launch with no navigation required.

#### Scenario: App launches to dice screen

- GIVEN the app starts for the first time
- WHEN MainActivity.onCreate completes
- THEN the DiceScreen composable is displayed
- AND no greeting text is visible

## Non-Functional Requirements

- The pure roller function MUST complete in under 1ms
- The Compose UI MUST remain responsive (no dropped frames during animation)
- All new code MUST reside in package `com.kleros.dice`
- No new Gradle dependencies MAY be added beyond what the template already provides

## Data Definitions

```kotlin
enum class DiceType(val faces: Int) {
    D4(4), D6(6), D8(8), D10(10), D12(12), D20(20), D100(100)
}

data class DiceRollResult(
    val diceType: DiceType,
    val value: Int,        // 1..faces
    val timestamp: Long    // System.currentTimeMillis()
)

data class RollHistory(
    val rolls: List<DiceRollResult> = emptyList(),
    val maxSize: Int = 10
) {
    // Rolls is newest-first, never exceeds maxSize
}

object DiceRoller {
    fun roll(diceType: DiceType): Int  // Pure function, returns 1..diceType.faces
}
```
