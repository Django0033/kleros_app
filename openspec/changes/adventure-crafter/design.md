# Design: Adventure Crafter — 5 Plot Tables

## Technical Approach

Add a new feature module `adventurecrafter/` following exactly the same
`*Data` / `*Screen` split established by `meaning/`. The data object defines
5 plot-generation tables (D20 each). The screen is a one-composable adapter
to `TableScreen`. Register in `MainActivity.kt` navigation drawer.

## Architecture Decisions

### Decision: D20 instead of D100 for all 5 tables

| Option | Tradeoff |
|--------|----------|
| D100 (like Meaning) | 50 entries/table = verbose, harder to fill meaningfully |
| **D20** | 20 entries/table = punchy, fast for plot generation |

**Choice**: D20. The domain is quick plot prompts, not detailed oracle
tables. 5 tables × 20 entries = 100 total entries, same density as Meaning's
2 × 50, but more variety per screen visit.

### Decision: Single icon `Icons.Filled.Explore`

**Alternatives considered**: `Extension`, `AutoMirrored.Filled.Explore`.
**Rationale**: `Explore` is available in `material.icons.filled`, matches the
thematic intent, and is already in the standard library — no new import needed
in `MainActivity.kt`.

## Data Flow

```
User opens drawer
  → taps "Adv Craft"
  → AppNavigation renders AdventureCrafterScreen
  → AdventureCrafterScreen calls TableScreen(tables = AdventureCrafterData.tables)
  → User selects table chip, taps Roll
  → TableRoller.roll returns result, displayed inline
```

File surface:

```
MainActivity.kt (AppNavigation)
     │
     ▼
AdventureCrafterScreen.kt
     │
     ▼
TableScreen.kt (shared, unchanged)
     │
     ▼
AdventureCrafterData.kt ────→ TableDef / TableEntry (shared, unchanged)
       (5 TableDef values)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `app/src/main/java/com/kleros/adventurecrafter/AdventureCrafterData.kt` | Create | `object AdventureCrafterData` with 5 `TableDef` vals + `tables` list |
| `app/src/main/java/com/kleros/adventurecrafter/AdventureCrafterScreen.kt` | Create | `@Composable fun AdventureCrafterScreen(modifier)` → `TableScreen(...)` |
| `app/src/main/java/com/kleros/MainActivity.kt` | Modify | Add `ADVENTURE_CRAFTER` to `Screen` enum + `when` branch |

## Interfaces / Contracts

### AdventureCrafterData.kt

```
Package:  com.kleros.adventurecrafter
Imports:  DiceType, TableDef, TableEntry
Suppress: MagicNumber

object AdventureCrafterData {
    val plotHook: TableDef       // DiceType.D20, name = "Plot Hook"
    val antagonist: TableDef     // DiceType.D20, name = "Antagonist"
    val location: TableDef      // DiceType.D20, name = "Location"
    val complication: TableDef  // DiceType.D20, name = "Complication"
    val reward: TableDef        // DiceType.D20, name = "Reward"
    val tables: List<TableDef>  // all 5 in order
}
```

### AdventureCrafterScreen.kt

```
Package:  com.kleros.adventurecrafter
Imports:  Modifier, TableScreen, AdventureCrafterData, DiceType
Suppress: FunctionNaming

@Composable fun AdventureCrafterScreen(modifier: Modifier = Modifier)
  → TableScreen(
        tables = AdventureCrafterData.tables,
        modifier = modifier,
        title = "Adventure Crafter",
        diceType = DiceType.D20,
    )
```

### MainActivity.kt delta

Add to `Screen` enum:

```kotlin
ADVENTURE_CRAFTER("Adv Craft", Icons.Filled.Explore),
```

Add `when` branch:

```kotlin
Screen.ADVENTURE_CRAFTER -> AdventureCrafterScreen(modifier = Modifier.padding(innerPadding))
```

## Table Content Summary

| Table | Dice | Entry example (first) | Entry example (last) | Entries |
|-------|------|----------------------|---------------------|---------|
| Plot Hook | D20 | "A messenger arrives" | "A rival makes a bold move" | 20 |
| Antagonist | D20 | "Corrupt noble" | "Outer being" | 20 |
| Location | D20 | "Ancient temple ruins" | "Astral pocket realm" | 20 |
| Complication | D20 | "Time limit pressures" | "True threat was a distraction" | 20 |
| Reward | D20 | "Chest of ancient gold" | "Wish or greater boon" | 20 |

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | AdventureCrafterData — each table has 20 entries | Assert `entries.size == 20` per table |
| Unit | AdventureCrafterData — entries are DIRECT with correct index | Assert index 0..19, no gaps |
| Integration | AdventureCrafterScreen renders roll button | Compose test, `onNodeWithTag("rollButton")` |
| Integration | AdventureCrafterScreen renders all 5 table chips | Compose test, `onNodeWithTag("tableSelector_Plot Hook")` etc. |

## Migration / Rollout

No migration required. Brand new module, no existing data to migrate.

## Open Questions

None.
