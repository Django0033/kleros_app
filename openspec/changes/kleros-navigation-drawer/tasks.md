# Tasks: Kleros Navigation Drawer

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~105 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | single-pr |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: single-pr
400-line budget risk: Low

## Phase 1: Foundation — Add Dependency

- [x] 1.1 Add `material-icons-extended` library entry to `gradle/libs.versions.toml` under `[libraries]` (no version.ref — BOM-managed)
- [x] 1.2 Add `implementation(libs.androidx.compose.material.icons.extended)` to `app/build.gradle.kts` dependencies block

## Phase 2: TDD RED — Write Failing Navigation Drawer Tests

- [x] 2.1 Create `app/src/androidTest/java/com/kleros/NavigationDrawerTest.kt` with `hamburgerOpensDrawer()` test — tap hamburger, assert drawer item labels displayed
- [x] 2.2 Add `drawerItemSelectsScreen()` test — tap drawer item, assert screen content changes and drawer closes
- [x] 2.3 Add `drawerClosesAfterItemSelection()` test — open drawer, tap item, assert drawer items no longer displayed
- [x] 2.4 Compile and run `NavigationDrawerTest` — verify RED (compile fails due to `AppNavigation` being private)
- [x] 2.5 Commit RED checkpoint: `test: add NavigationDrawer UI tests (RED)`

## Phase 3: TDD GREEN — Implement Navigation Drawer

- [x] 3.1 Add `icon: ImageVector` field to `Screen` enum in `MainActivity.kt` with one icon per screen (Casino, Badge, Psychology, Face)
- [x] 3.2 Add imports for `ModalNavigationDrawer`, `ModalDrawerSheet`, `NavigationDrawerItem`, `TopAppBar`, `DrawerState`, `Icons.*` to `MainActivity.kt`
- [x] 3.3 Rewrite `AppNavigation` composable: wrap `ModalNavigationDrawer` around `Scaffold`, add `TopAppBar` with hamburger `IconButton`, render `ModalDrawerSheet` with `NavigationDrawerItem` per screen
- [x] 3.4 Add `testTag("navDrawerHamburger")` to hamburger `IconButton`
- [x] 3.5 Compile and run `NavigationDrawerTest` — verify GREEN (all 3 tests compile and pass)
- [x] 3.6 Commit GREEN checkpoint: `feat: replace FilterChip row with ModalNavigationDrawer`

## Phase 4: Verification

- [x] 4.1 Run `./gradlew ktlintCheck` — verify zero lint violations
- [x] 4.2 Run `./gradlew detekt` — verify zero detekt violations
- [x] 4.3 Run existing screen tests — verify they still pass (no screen composable changes)
- [x] 4.4 Verify `compileDebugAndroidTestKotlin` succeeds
