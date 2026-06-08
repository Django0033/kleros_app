# Design: Kleros Dice Roll

## Technical Approach

Pure-function domain layer (`DiceRoller`, `RollHistory`) in `com.kleros.dice`, stateless and Android-free. UI layer (`DiceScreen`) in the same package uses `remember`/`mutableStateOf` — no ViewModel for this MVP. Result value animates via `animateIntAsState`; a companion `Animatable` drives a scale pulse on each roll. History is an immutable list capped at 10 entries.

## Architecture Decisions

| Option | Tradeoffs | Decision |
|--------|-----------|----------|
| ViewModel vs composable-local state | ViewModel adds DI + lifecycle; local state is simpler for zero-persist screen | Local state — no ViewModel |
| `DiceRoller` as object vs class | Object is singleton, no DI needed; class would support mocking | Object with `roll()` |
| `RollHistory` as data class vs mutable model | Data class forces immutable snapshots, thread-safe; caller replaces on append | Data class + `append()` copy |
| `animateIntAsState` vs custom animator | Built-in handles value animation; scale needs separate `Animatable` | Both: `animateIntAsState` for value, `Animatable` for scale pulse |
| Package `com.kleros.dice` vs nested module | Single module already; flat package matches existing theme pattern | `com.kleros.dice` |

## Data Flow

```
User tap → selectedDiceType updated
                ↓
User tap → Roll button onClick
                ↓
       DiceRoller.roll(type)
                ↓
       lastResult = (type, value, now)
                ↓
       history = history.append(result)
                ↓
    animateIntAsState(value) → UI updates
    Animatable(1.2f→1.0f)    → scale pulse
```

## Component Specifications

### `DiceType` — enum
```kotlin
enum class DiceType(val faces: Int, val label: String) {
    D4(4, "D4"), D6(6, "D6"), D8(8, "D8"),
    D10(10, "D10"), D12(12, "D12"), D20(20, "D20"),
    D100(100, "D100")
}
```

### `DiceRollResult` — data class
```kotlin
data class DiceRollResult(
    val diceType: DiceType,
    val value: Int,
    val timestampMillis: Long = System.currentTimeMillis()
)
```

### `DiceRoller` — object
```kotlin
object DiceRoller {
    fun roll(type: DiceType): Int = Random.nextInt(1, type.faces + 1)
}
```
Invariant: `value in 1..type.faces`.

### `RollHistory` — data class
```kotlin
data class RollHistory(val results: List<DiceRollResult> = emptyList()) {
    companion object { const val MAX_SIZE = 10 }
    fun append(result: DiceRollResult): RollHistory =
        copy(results = (listOf(result) + results).take(MAX_SIZE))
}
```
Invariant: `results.size <= 10`, newest first (index 0).

### `DiceScreen` — @Composable
```
State:
  selectedDiceType: MutableState<DiceType>
  lastResult:       MutableState<DiceRollResult?>
  history:          MutableState<RollHistory>
  scaleAnim:        Animatable(1f)

Animation:
  - animatedValue: Int by animateIntAsState(lastResult?.value ?: 0, spring())
  - On roll: launch { scaleAnim.animateTo(1.2f); scaleAnim.animateTo(1f) }
  - Result text rendered with Modifier.graphicsLayer(scaleX = scale, scaleY = scale)

Layout (top→bottom):
  1. Dice type selector: FilterChip row (one per DiceType)
  2. Roll button: FilledTonalButton, enabled when a type is selected
  3. Result card: ElevatedCard with animated integer + dice type label
  4. History: LazyColumn (max 10), each row shows dice type + value
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `app/src/main/java/com/kleros/dice/DiceType.kt` | Create | Enum with 7 face counts |
| `app/src/main/java/com/kleros/dice/DiceRollResult.kt` | Create | Data class for a single roll |
| `app/src/main/java/com/kleros/dice/DiceRoller.kt` | Create | `roll(DiceType): Int` pure function |
| `app/src/main/java/com/kleros/dice/RollHistory.kt` | Create | Capped list (10), newest first |
| `app/src/main/java/com/kleros/dice/DiceScreen.kt` | Create | Full composable with selector, button, result, history |
| `app/src/main/java/com/kleros/MainActivity.kt` | Modify | Replace `Greeting()` with `DiceScreen()`; remove `Greeting` + `GreetingPreview` |
| `app/src/test/java/com/kleros/dice/DiceRollerTest.kt` | Create | Range validation per type (1000 rolls each) |
| `app/src/test/java/com/kleros/dice/RollHistoryTest.kt` | Create | Max size, ordering, empty edge case |
| `app/src/androidTest/java/com/kleros/dice/DiceScreenTest.kt` | Create | Compose UI test: select, roll, assert result + history |

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | `DiceRoller.roll()` | 1000 invocations per dice type; assert every result in `1..type.faces`. Edge: D4 range, D100 range. |
| Unit | `RollHistory.append()` | Append 12 results → assert size = 10, newest first. Append 0 → empty. Assert timestamp ordering. |
| UI | `DiceScreen` | Compose UI Test: select D20, tap roll, wait for idle, assert result text visible. Roll 3 times → assert history count = 3. |
| Lint | detekt/ktlint | `./gradlew detekt ktlintCheck` must pass. |

## Sequence Diagram

```
User           DiceScreen              DiceRoller         RollHistory
 │                  │                      │                  │
 │  tap D20 chip    │                      │                  │
 │─────────────────>│ selectedDiceType=D20  │                  │
 │                  │                      │                  │
 │  tap Roll btn    │                      │                  │
 │─────────────────>│                      │                  │
 │                  │ roll(D20)            │                  │
 │                  │─────────────────────>│                  │
 │                  │   15                 │                  │
 │                  │<─────────────────────│                  │
 │                  │                      │                  │
 │                  │ append(result)        │                  │
 │                  │────────────────────────────────────────>│
 │                  │         RollHistory(size=1)              │
 │                  │<────────────────────────────────────────│
 │                  │                      │                  │
 │  sees "15" +    │                      │                  │
 │  scale pulse    │                      │                  │
 │<─────────────────│                      │                  │
```

## Migration / Rollout

No migration required. Additive files + one-line change in `MainActivity.kt`. Rollback: revert `MainActivity.kt` and delete `app/src/main/java/com/kleros/dice/`.

## Open Questions

- None.
