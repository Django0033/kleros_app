# Design: Character Crafter

## Technical Approach

Add a new `com.kleros.character` package following the exact `com.kleros.namegenerator` pattern. A `CharacterCrafter` object rolls 4 independent d100 picks from a 100-entry shared descriptor pool (identity, mind, body, talent) plus 1 d10 mapped to a 5-tier statistic. Results render on a custom `CharacterScreen` composable with immutable history (max 10). No ViewModel — `remember`/`mutableStateOf` as in `NameScreen`.

## Architecture Decisions

| Decision | Choice | Alternatives | Rationale |
|----------|--------|-------------|-----------|
| **Package** | `com.kleros.character` | Subpackage of namegenerator | Matches existing first-class package pattern (dice, namegenerator, table are siblings) |
| **Roll strategy** | 4 independent d100 rolls for descriptors | Single d100 + entry with all 4 fields | "Shared pool" — any descriptor can appear in any slot; same entry may repeat across slots |
| **Statistics** | Single 1d10 → 5-tier roll | 5 independent d10 rolls | Keeps generation one-tap; 5-tier (2 values per tier) covers relative ability |
| **Descriptor data** | Static `object` with 100 strings | Enum, embedded list | Direct-mapped `descriptor(d100): String` mirrors `NameTable.row(Int)` access pattern |
| **State** | `remember`/`mutableStateOf` | ViewModel | Project convention; existing screens have no ViewModel dependency |
| **Generator signature** | `generate(rollFn: (DiceType) -> Int)` | Direct DiceRoller call | Matches `NameGenerator.generate` — enables deterministic tests |

## Data Flow

```
User taps "Generate Character"
         │
         ▼
CharacterCrafter.generate(rollFn)
         │
         ├── rollFn(D100) → index → CharacterData.descriptor(idx)  → identity: String
         ├── rollFn(D100) → index → CharacterData.descriptor(idx)  → mind: String
         ├── rollFn(D100) → index → CharacterData.descriptor(idx)  → body: String
         ├── rollFn(D100) → index → CharacterData.descriptor(idx)  → talent: String
         └── rollFn(D10)  → tier  → CharacterData.statisticTier(tier) → statistics: String
         │
         ▼
CharacterResult(identity, mind, body, talent, statistics, timestamp)
         │
         ├── displayed in Card on CharacterScreen
         └── appended to CharacterHistory (capped 10, newest-first)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `app/src/main/java/com/kleros/character/CharacterData.kt` | Create | 100-descriptor pool (`@file:Suppress("MagicNumber")`) + 5-tier statistic table with range mapping `(1-2→0, 3-4→1, 5-6→2, 7-8→3, 9-10→4)` |
| `app/src/main/java/com/kleros/character/CharacterCrafter.kt` | Create | `object CharacterCrafter` with `generate(rollFn): CharacterResult` — 4x d100 + 1x d10 |
| `app/src/main/java/com/kleros/character/CharacterResult.kt` | Create | `data class CharacterResult(identity, mind, body, talent, statistics, timestampMillis)` |
| `app/src/main/java/com/kleros/character/CharacterHistory.kt` | Create | Immutable data class, `append()` → capped 10, newest-first |
| `app/src/main/java/com/kleros/character/CharacterScreen.kt` | Create | Composable: title, "Generate Character" button, result Card (4 descriptors + stat tier), history list |
| `app/src/main/java/com/kleros/MainActivity.kt` | Modify | Add `Screen.CHARACTER_CRAFTER("Character")` enum entry + `when` branch → `CharacterScreen()` |
| `app/src/test/java/com/kleros/character/CharacterCrafterTest.kt` | Create | Unit tests for generator with deterministic rollFn |
| `app/src/test/java/com/kleros/character/CharacterDataTest.kt` | Create | Unit tests for 100 descriptors accessible, 5-tier stat ranges |
| `app/src/test/java/com/kleros/character/CharacterHistoryTest.kt` | Create | Unit tests for immutable append, cap at 10, ordering |
| `app/src/androidTest/java/com/kleros/character/CharacterScreenTest.kt` | Create | Compose UI test: button renders, tap shows result |

## Interfaces / Contracts

```kotlin
// --- CharacterData.kt ---
@file:Suppress("MagicNumber")

object CharacterData {
    /** 100 shared descriptor strings, indexed 1-100. */
    fun descriptor(index: Int): String

    /**
     * Maps a 1d10 roll (1-10) to 1 of 5 statistic tiers.
     * Ranges: 1-2, 3-4, 5-6, 7-8, 9-10
     */
    fun statisticTier(roll: Int): StatTier
}

data class StatTier(val label: String, val tier: Int) // tier 0-4

// --- CharacterCrafter.kt ---
object CharacterCrafter {
    fun generate(
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CharacterResult

    // 4x rollFn(D100) for descriptors + 1x rollFn(D10) for statistics
}

// --- CharacterResult.kt ---
data class CharacterResult(
    val identity: String,
    val mind: String,
    val body: String,
    val talent: String,
    val statistics: String,
    val timestampMillis: Long = System.currentTimeMillis(),
)

// --- CharacterHistory.kt ---
data class CharacterHistory(
    val results: List<CharacterResult> = emptyList(),
) {
    companion object { const val MAX_SIZE = 10 }
    fun append(result: CharacterResult): CharacterHistory
}
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit — CharacterData | 100 descriptors accessible at indices 1-100, non-empty | Parameterized loop 1..100 |
| Unit — CharacterData | Statistic tier ranges cover 1-10 without gaps | Assert every roll 1-10 maps to tier 0-4 |
| Unit — CharacterCrafter | Deterministic rollFn produces expected descriptors + stat | Controlled roll list like `NameGeneratorTest` |
| Unit — CharacterCrafter | 1000 invocations produce variety | At least 20 unique descriptors across all slots |
| Unit — CharacterCrafter | Stat ranges are correct | Assert d10=1-2 → tier 0, d10=9-10 → tier 4 |
| Unit — CharacterHistory | Empty, append preserves order newest-first, caps at 10 | Same pattern as `NameHistoryTest` |
| Compose UI — CharacterScreen | Generate button renders | `assertIsDisplayed` on testTag |
| Compose UI — CharacterScreen | Tap generate shows result card | `performClick` + assert descriptor text visible |

## Migration / Rollout

No migration required. Additive only — new package with no changes to existing features. Single enum addition in `MainActivity.kt`.

## Open Questions

None.
