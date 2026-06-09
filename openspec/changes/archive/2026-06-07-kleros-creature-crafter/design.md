# Design: Creature Crafter

## Technical Approach

New `com.kleros.creature` package (6 production files + 5 test files) mirroring the Character Crafter pattern exactly. Creature generation follows the same `object.generate(rollFn)` contract but produces `List<String>` for descriptors/abilities instead of fixed fields. Post-gen mutation methods — `rollDescriptor()`, `rollAbility()`, `rollNewBehavior()` — accept and return `CreatureResult` copies, keeping the data class immutable. Statistics reused from `CharacterData.statistics` via cross-package import.

## Architecture Decisions

### Decision: Table weight strategy (duplicate DIRECT vs RANGE)

| Option | Tradeoff | Decision |
|--------|----------|----------|
| 100 RANGE entries (some 2-wide for weight) | RANGE uses min/max, not index — lookups need range scanning for everything | ❌ |
| 100 DIRECT entries, adjacency duplicates for weight | Same index-based lookup as CharacterData.descriptors; `creature.lua` source already uses this pattern | ✅ |

**Rationale**: Spec requires `List<TableEntry.DIRECT>` for all creature tables. Adjacent duplicate entries (e.g., Loud at 2, 3) achieve weighting without introducing RANGE logic. Lookup is O(1) by index — identical to `CharacterCrafter.lookupDescriptor()`.

### Decision: Mutation methods on CreatureCrafter (not CreatureResult)

**Choice**: Static `CreatureCrafter.rollDescriptor(CreatureResult, rollFn): CreatureResult`
**Alternatives**: Extension functions on CreatureResult, instance methods, separate mutation service
**Rationale**: Keeps data class a pure value object with no logic. Mutation pattern mirrors `generate()` — same `rollFn` injection, same lookup logic. Caller owns state management (composable's `mutableStateOf`).

### Decision: History reuses exact CharacterHistory pattern

**Choice**: `CreatureHistory` data class with `append()` — identical structure
**Rationale**: No behavioral differences. Duplicating the 14-line file is cheaper than sharing a base (YAGNI — only 2 crafters).

## Data Flow

```
User taps "Generate Creature"
  → CreatureCrafter.generate(rollFn)
    → 2×d100 → CreatureData.descriptors (index lookup)
    → 2×d100 → CreatureData.abilities (index lookup)
    → 1×d10 → CreatureData.behaviorInitial (index lookup)
    → 1×d10 → CharacterData.statistics (range lookup)
  → CreatureResult (newBehavior = null)
  → CreatureHistory.append(result) → capped at 10
  → UI recomposes

User taps "Roll Descriptor"
  → CreatureCrafter.rollDescriptor(currentResult, rollFn)
    → 1×d100 → CreatureData.descriptors → append to list
    → new CreatureResult copy (descriptors + 1)
  → UI recomposes

User taps "Roll Ability" → same pattern, append to abilities list
User taps "New Behavior"
  → CreatureCrafter.rollNewBehavior(currentResult, rollFn)
    → if newBehavior != null: no-op (don't call rollFn)
    → else: 1×d10 → CreatureData.behaviorNew → set newBehavior
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `app/src/main/java/com/kleros/creature/CreatureData.kt` | Create | 4 tables: descriptors(100 DIRECT), abilities(100 DIRECT, 50 paired), behaviorInitial(10 DIRECT), behaviorNew(10 DIRECT) |
| `app/src/main/java/com/kleros/creature/CreatureCrafter.kt` | Create | `object.generate()`, `rollDescriptor()`, `rollAbility()`, `rollNewBehavior()` |
| `app/src/main/java/com/kleros/creature/CreatureResult.kt` | Create | `data class` with `descriptors: List<String>`, `abilities: List<String>`, `initialBehavior: String`, `statistics: String`, `newBehavior: String?`, `timestampMillis` |
| `app/src/main/java/com/kleros/creature/CreatureHistory.kt` | Create | Immutable append, cap 10, newest-first |
| `app/src/main/java/com/kleros/creature/CreatureScreen.kt` | Create | Composable: Generate button, result card, 3 mutation buttons, history list |
| `app/src/main/java/com/kleros/MainActivity.kt` | Modify | Add `Screen.CREATURE_CRAFTER("Creature", Icons.Filled.BugReport)`, `when` branch |
| `app/src/test/java/com/kleros/creature/CreatureDataTest.kt` | Create | Table size, uniqueness, weighting validation |
| `app/src/test/java/com/kleros/creature/CreatureCrafterTest.kt` | Create | Deterministic rolls, mutations, 1000 random, stat boundaries |
| `app/src/test/java/com/kleros/creature/CreatureResultTest.kt` | Create | Constructor, copy, timestamp |
| `app/src/test/java/com/kleros/creature/CreatureHistoryTest.kt` | Create | Append, cap at 10, immutability |
| `app/src/androidTest/java/com/kleros/creature/CreatureScreenTest.kt` | Create | Compose UI: generate button, card visible, mutation buttons, history |

## Interfaces / Contracts

```kotlin
// CreatureResult.kt
data class CreatureResult(
    val descriptors: List<String>,
    val abilities: List<String>,
    val initialBehavior: String,
    val statistics: String,
    val newBehavior: String? = null,
    val timestampMillis: Long = System.currentTimeMillis(),
)

// CreatureCrafter.kt
object CreatureCrafter {
    fun generate(rollFn: (DiceType) -> Int = { DiceRoller.roll(it) }): CreatureResult
    fun rollDescriptor(result: CreatureResult, rollFn: (DiceType) -> Int = { DiceRoller.roll(it) }): CreatureResult
    fun rollAbility(result: CreatureResult, rollFn: (DiceType) -> Int = { DiceRoller.roll(it) }): CreatureResult
    fun rollNewBehavior(result: CreatureResult, rollFn: (DiceType) -> Int = { DiceRoller.roll(it) }): CreatureResult
}

// CreatureHistory.kt
data class CreatureHistory(val results: List<CreatureResult> = emptyList()) {
    fun append(result: CreatureResult): CreatureHistory // immutable, caps at 10, newest-first
}

// CreatureScreen.kt — testTags
"generateCreatureButton", "descriptorLabel", "abilityLabel",
"initialBehaviorLabel", "statisticsLabel", "rollDescriptorButton",
"rollAbilityButton", "newBehaviorButton", "historyList", "historyItem"
```

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | `CreatureData` | 4 tables: 100 desc, 100 abil, 10 initB, 10 newB; paired-ability validation; at least 2 descriptor duplicates (weighting) |
| Unit | `CreatureCrafter.generate` | Deterministic `rollFn` returns exact descriptor/ability/behavior/stat; stat boundaries (d10 1..10); 1000 random — all slots non-empty; timestamp > 0 |
| Unit | `CreatureCrafter.rollDescriptor` | Start with 2, append → 3; original unchanged |
| Unit | `CreatureCrafter.rollAbility` | Start with 2, append → 3; original unchanged |
| Unit | `CreatureCrafter.rollNewBehavior` | Null → sets; non-null → no-op (rollFn not invoked) |
| Unit | `CreatureResult` | Constructor, copy immutability, timestamp auto-populated |
| Unit | `CreatureHistory` | Empty → size 0; append 3 → size 3, newest-first; 12 append → cap 10, oldest evicted |
| Compose UI | `CreatureScreen` | Initial: only generate button; after tap: card + 3 mutation buttons visible; history accumulates |

## Migration / Rollout

No migration required. Additive — existing features unchanged. Navigation wiring is a single `when` branch + enum entry. Remove by deleting `creature/` package and one enum line.

## Open Questions

- None. All decisions resolved by spec, existing codebase patterns, and proposal.

## Stats

- **Production LOC**: ~200 (6 files)
- **Test LOC**: ~200 (5 files)
- **Tables**: 400 total entries (100 desc + 100 abil + 10 initB + 10 newB + 180 adj duplicates for weighting)
