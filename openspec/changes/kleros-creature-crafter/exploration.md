# Exploration: Creature Crafter

## Current State

The app has a working **Character Crafter** (`com.kleros.character` package) with 5 source files + 4 test files + 1 UI test file, following the established namegenerator pattern. Character Crafter generates NPCs via 4 independent d100 descriptor rolls (identity, mind, body, talent) + 1 d10 statistics roll, displayed on a custom `CharacterScreen` composable with immutable newest-first history (max 10).

### Key patterns to reuse

| Pattern | File | How It Works |
|---------|------|-------------|
| Data | `CharacterData.kt` | `object` holding `List<TableEntry>` compile-time constants |
| Generator | `CharacterCrafter.kt` | `object` with `generate(rollFn: (DiceType) -> Int): CharacterResult` |
| Model | `CharacterResult.kt` | `data class` with named fields + `timestampMillis: Long` |
| History | `CharacterHistory.kt` | immutable `data class`, `append()` → capped 10 newest-first |
| Screen | `CharacterScreen.kt` | `@Composable` with button + result card + history list |
| Navigation | `MainActivity.kt` | `Screen` enum → `when` branch → composable |
| Tests | `*Test.kt` (unit) + `*ScreenTest.kt` (Compose UI) | Deterministic `rollFn` for unit tests |

## Source Data Analysis

### Source file: `opm.nvim/lua/opm/tables/creature.lua`

Contains 4 tables, each indexed 1-N:

#### 1. `descriptor` — 100 entries (1d100)
Weighted table: many entries appear twice (e.g., Loud at indices 2+3, Chemical at 11+12). ~56 unique values but 100 weighted slots. The `TableEntry.DIRECT` pattern supports this naturally since each index maps independently.

**NOTE**: The user's prompt listed ~83 unique descriptors (Amorphous through Weight) that do NOT match this file. The actual file has a different vocabulary with extensive pair-weighting. **I recommend using the actual file data** — it's the authoritative source for the feature being ported.

#### 2. `behavior_initial` — 10 entries (1d10)
Exactly 10 unique entries:
1. Inert, motionless
2. Moving, traveling
3. Moving, traveling (duplicate — 2 & 3 both "Moving, traveling")
4. Wary and alert
5. Friendly
6. Attacking, aggressive
7. Feeding
8. Working, doing something
9. Defensive, protecting itself
10. Exhibits an Ability

#### 3. `behavior_new` — 10 entries (1d10)
Exactly 10 entries:
1-6. Acts as expected (×6)
7-8. Next expected step, or greater intensity (×2)
9. Roll on Initial Behavior
10. Exhibits an Ability

#### 4. `ability` — 100 entries (1d100) [OPTIONAL — user spec says 4 rolls]
Also weighted with pairs. 50 unique values × 2 = 100 slots. Not in the 4-roll set per user specification.

### Statistics — reuse `CharacterData.statistics`

The 5-tier table from `CharacterData.statistics` (1d10 → 5 tiers) maps directly:
| Roll | Result |
|------|--------|
| 1 | About 50% lower |
| 2-3 | About 25% lower |
| 4-7 | What you expect |
| 8-9 | About 25% higher |
| 10 | About 50% higher |

## Key Differences from Character Crafter

| Aspect | Character Crafter | Creature Crafter |
|--------|------------------|-----------------|
| **Descriptor rolls** | 4 independent d100 → 4 slots (identity, mind, body, talent) | **1** d100 → 1 descriptor |
| **Additional rolls** | none | 1d10 initial behavior + 1d10 new behavior |
| **Statistics** | 1d10 → 5-tier from CharacterData | **Same** — reuse CharacterData.statistics |
| **Total rolls** | 5 (4×d100 + 1×d10) | 4 (1×d100 + 2×d10 + 1×d10 stat) |
| **Behavior concept** | None — static descriptors | Initial + new behavior tells GM what creature is DOING |
| **Descriptor pool** | Shared pool (any word in any slot) | Single roll from weighted creature-specific pool |
| **Shared data** | None | `CharacterData.statistics` TABLE is reused directly |

### CreatureResult fields

```
CreatureResult(
    descriptor: String,        // 1d100 → descriptor table
    initialBehavior: String,   // 1d10 → behavior_initial table
    newBehavior: String,       // 1d10 → behavior_new table
    statistics: String,        // 1d10 → CharacterData.statistics (REUSED)
    timestampMillis: Long,
)
```

## Approaches

### 1. **New `com.kleros.creature` package** (recommended)

Follow exact Character Crafter file pattern in a new package:

| File | Purpose |
|------|---------|
| `creature/CreatureData.kt` | `object` with 100-entry descriptor list (from creature.lua) + initial_behavior (10) + new_behavior (10) |
| `creature/CreatureCrafter.kt` | `object` with `generate(rollFn): CreatureResult` — 1×d100 descriptor + 1×d10 init_behavior + 1×d10 new_behavior + 1×d10 stat |
| `creature/CreatureResult.kt` | `data class` with 4 fields + timestamp |
| `creature/CreatureHistory.kt` | Same immutable newest-first pattern (max 10) |
| `creature/CreatureScreen.kt` | Custom screen: "Generate Creature" button + result card + history |
| `MainActivity.kt` | Add `CREATURE_CRAFTER("Creature")` to Screen enum + icon + `when` branch |

**Stats reuse**: CreatureCrafter calls `CharacterData.statistics` directly — no need to duplicate.

**Pros**: Clean separation, zero coupling to character package, follows established convention
**Cons**: Minor duplication of statistics access pattern
**Effort**: Low — ~200 lines production + ~150 lines test

### 2. **Extend Character Crafter package**

Add creature tables and generator to `com.kleros.character` (e.g., `CharacterData.creatureDescriptors`, `CreatureCrafter` as a separate object in same package).

**Pros**: Shares statistics table reference naturally
**Cons**: Mixed responsibilities, confusing package, breaks the clear single-purpose package convention
**Effort**: Low, but architecturally inferior

### 3. **Generic "crafter" package**

Create `com.kleros.crafter` with shared base classes for both Character and Creature crafter.

**Pros**: Would DRY up the pattern
**Cons**: Premature abstraction (only 2 crafters), YAGNI violation, increased complexity for zero benefit
**Effort**: Medium

## Recommendation

**Approach 1**: New `com.kleros.creature` package, same structure as `com.kleros.character`. This is the minimal, clean, additive approach that follows the established project convention exactly. The statistics reuse across packages is already handled by the `TableEntry.DIRECT`/`RANGE` pattern — `CreatureCrafter` simply imports `CharacterData.statistics`.

### Statistics reuse detail

```kotlin
// CreatureCrafter.kt — imports and uses CharacterData.statistics
import com.kleros.character.CharacterData

private fun lookupStatistic(roll: Int): String {
    val entry = CharacterData.statistics.first { statEntry ->
        val range = statEntry as TableEntry.RANGE
        roll in range.min..range.max
    }
    return (entry as TableEntry.RANGE).result
}
```

This is a clean cross-package dependency — `creature` depends on `character`'s data, which is fine since both are siblings under `com.kleros`.

### Descriptor data source decision

The actual `creature.lua` descriptors use **pair-weighting** (56 unique values, 100 weighted slots). I recommend using the **actual file data** as the authoritative source. The user's hand-typed list differs substantially from the file. The weighted distribution adds gameplay depth — common descriptors (appearing twice) are more likely than rare ones (appearing once).

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Statistics table imported from Character package creates cross-package dependency | Low | Clean import — both are sibling packages; no circular dependency |
| Behavior entries use longer descriptions than character descriptors (e.g., "Working, doing something" vs single words) | Low | Natural — creature behavior is inherently descriptive; screen formatting handles it |
| Creature result has different fields than Character (descriptor + initial + new behavior + stat vs identity+mind+body+talent+stat) | None | Different domain, different data class — correct by design |
| Descriptor word discrepancies between user description and actual file | Low | Use actual file data as authoritative; document source in spec |
| Screen naming collisions with testTags | None | Use `"creature"` prefix (e.g., `"generateCreatureButton"`, `"descriptorLabel"`, `"initialBehaviorLabel"`) |

## Affected Areas

| Area | Impact |
|------|--------|
| `app/.../creature/CreatureData.kt` | **Create** — 100 descriptor (1d100) + 10 initial_behavior (1d10) + 10 new_behavior (1d10) |
| `app/.../creature/CreatureCrafter.kt` | **Create** — 1×d100 + 2×d10 + 1×d10 stat generation |
| `app/.../creature/CreatureResult.kt` | **Create** — data class with descriptor, initialBehavior, newBehavior, statistics |
| `app/.../creature/CreatureHistory.kt` | **Create** — immutable history, max 10, newest-first |
| `app/.../creature/CreatureScreen.kt` | **Create** — custom composable screen |
| `app/.../MainActivity.kt` | **Modify** — add `CREATURE_CRAFTER` enum + nav branch + icon |
| `app/.../test/.../creature/*Test.kt` | **Create** — 3-4 unit tests (data, crafter, result, history) |
| `app/.../androidTest/.../creature/CreatureScreenTest.kt` | **Create** — Compose UI smoke test |

## Ready for Proposal

**Yes**. The pattern is well-established by Character Crafter, the source data is fully available, and the differences are well understood. Proceed to `sdd-propose`.

## Skill Resolution

This exploration was self-contained. No external skills were needed beyond the existing codebase analysis. The `sdd-explore` skill's phase-common retrieval steps were: read all files in the affected package (character/), read the creature source data (creature.lua), read the navigation wiring (MainActivity.kt), and read existing SDD change artifacts for the pattern reference.
