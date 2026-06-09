# Design: Kleros Navigation Drawer

## Technical Approach

Replace the `FilterChip` `Row` in `AppNavigation` with a Material3 `ModalNavigationDrawer` + `TopAppBar` hamburger pattern. The `Screen` enum gains an `icon` field. `AppNavigation` is rewritten to host the drawer scaffold. All four screen composables are unchanged — they receive `Modifier` as before. One dependency added: `material-icons-extended`.

## Architecture Decisions

### Decision: ModalNavigationDrawer wrapping Scaffold (not Scaffold inside drawer)

| Option | Tradeoff |
|--------|----------|
| Scaffold inside ModalNavigationDrawer | Drawer slides over Scaffold content; TopAppBar inside Scaffold, drawer sheet outside. This is the standard M3 pattern. |
| Drawer inside Scaffold | Not supported — ModalNavigationDrawer must be the outermost layout element to render the sheet correctly. |

**Choice**: `ModalNavigationDrawer` wraps `Scaffold`. `Scaffold` provides `TopAppBar` + content area. This is the canonical M3 architecture and requires zero workarounds.

### Decision: DrawerState held at AppNavigation level

`rememberDrawerState(DrawerValue.Closed)` lives at the `AppNavigation` composable scope. `ModalNavigationDrawer` consumes it; `TopAppBar` hamburger calls `drawerState.open()`. No ViewModel needed — drawer visibility is purely UI state.

### Decision: Icons per screen — material-icons-extended

| Screen | Icon |
|--------|------|
| DICE | `Icons.Filled.Casino` |
| NAME_GENERATOR | `Icons.Filled.Badge` |
| MEANING | `Icons.Filled.Psychology` |
| CHARACTER_CRAFTER | `Icons.Filled.Face` |

All four require `material-icons-extended`. R8/Proguard strips unused icons at ~60KB impact.

## Composable Tree

```
KlerosTheme {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Screen.entries.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentScreen == screen,
                        onClick = {
                            currentScreen = screen
                            drawerState.close()
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.label) },
                    navigationIcon = {
                        IconButton(onClick = { drawerState.open() }) {
                            Icon(Icons.AutoMirrored.Filled.Menu, "Open drawer")
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            when (currentScreen) {
                Screen.DICE -> DiceScreen(modifier = Modifier.padding(innerPadding))
                Screen.NAME_GENERATOR -> NameScreen(modifier = Modifier.padding(innerPadding))
                Screen.MEANING -> MeaningScreen(modifier = Modifier.padding(innerPadding))
                Screen.CHARACTER_CRAFTER -> CharacterScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
```

## Data Flow (Sequence)

```
User                  TopAppBar            DrawerState         AppNavigation       Screen Composable
 │                       │                     │                    │                    │
 │── tap hamburger ─────→│                     │                    │                    │
 │                       │── drawerState.open() │                    │                    │
 │                       │────────────────────→│                    │                    │
 │                       │                     │── recompose ──────→│                    │
 │◄─ drawer slides in ───│                     │                    │                    │
 │                       │                     │                    │                    │
 │── tap "Meaning" ────────────────────────────→│                    │                    │
 │                       │                     │── close() ────────→│                    │
 │                       │                     │── currentScreen    │                    │
 │                       │                     │   = MEANING ──────→│                    │
 │                       │                     │                    │── show MeaningScreen│
 │◄─ drawer closes ──────│                     │                    │                    │
 │◄─ Meaning renders ────│                     │                    │                    │
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `gradle/libs.versions.toml` | Modify | Add `material-icons-extended` library entry |
| `app/build.gradle.kts` | Modify | Add `implementation(libs.androidx.compose.material.icons.extended)` |
| `app/src/main/java/com/kleros/MainActivity.kt` | Modify | Screen enum gains `icon` field; AppNavigation rewritten with ModalNavigationDrawer + TopAppBar |
| `app/src/androidTest/java/com/kleros/NavigationDrawerTest.kt` | New | Compose UI tests for drawer open/close/select |

## Interfaces / Contracts

### Screen enum (modified)

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.graphics.vector.ImageVector

private enum class Screen(
    val label: String,
    val icon: ImageVector,
) {
    DICE("Dice Roll", Icons.Filled.Casino),
    NAME_GENERATOR("Name Gen", Icons.Filled.Badge),
    MEANING("Meaning", Icons.Filled.Psychology),
    CHARACTER_CRAFTER("Char Caft", Icons.Filled.Face),
}
```

### Dependency entry (version catalog)

```toml
[versions]
composeIconsExtended = "1.7.8"         # or managed by compose-bom

[libraries]
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
```

**Note**: Since `compose-bom = "2026.02.01"` already manages all Compose artifacts, the version catalog entry omits `version.ref` — the BOM controls the version. No separate version needed.

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| UI | Drawer opens via hamburger tap | Compose UI: `onNodeWithTag("hamburger")` → `performClick()` → assert drawer items visible |
| UI | Drawer items display correct label | Compose UI: assert `onNodeWithText("Dice Roll")` displayed when drawer open |
| UI | Selecting item navigates to screen | Compose UI: click drawer item → assert `onNodeWithText("Roll")` visible (DiceScreen content) |
| UI | Current screen highlighted | Compose UI: assert `NavigationDrawerItem` with `selected` semantics |
| UI | Swipe gesture opens drawer | `semantics(disabledConfig = ExperimentalTestApi)` → `swipeRight()` on content |

### Test file: `app/src/androidTest/java/com/kleros/NavigationDrawerTest.kt`

```kotlin
@RunWith(AndroidJUnit4::class)
class NavigationDrawerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun hamburgerOpensDrawer() {
        composeTestRule.setContent { KlerosTheme { AppNavigation() } }
        composeTestRule.onNodeWithTag("hamburger").performClick()
        composeTestRule.onNodeWithText("Dice Roll").assertIsDisplayed()
        composeTestRule.onNodeWithText("Meaning").assertIsDisplayed()
    }

    @Test
    fun drawerItemSelectsScreen() {
        composeTestRule.setContent { KlerosTheme { AppNavigation() } }
        composeTestRule.onNodeWithTag("hamburger").performClick()
        composeTestRule.onNodeWithText("Meaning").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Meaning").assertDoesNotExist() // drawer closed
        // DiceScreen content gone, MeaningScreen content present
    }

    @Test
    fun currentScreenHighlighted() {
        composeTestRule.setContent { KlerosTheme { AppNavigation() } }
        // DICE is default — its drawer item should be selected
        composeTestRule.onNodeWithTag("hamburger").performClick()
        composeTestRule.onNodeWithTag("drawerItem_DICE").assertIsDisplayed()
        // semantics check for selected state
    }
}
```

## Migration / Rollout

No migration required. This is a single-module UI refactor — deploy as normal.

## Open Questions

- None. All decisions are scoped in the proposal.
