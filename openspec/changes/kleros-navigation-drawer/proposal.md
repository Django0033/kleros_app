# Proposal: Kleros Navigation Drawer

## Intent

Replace the FilterChip-based screen switching bar with a Material3 ModalNavigationDrawer. Current FilterChip row is cramped at 4 chips, scales poorly to more screens, and offers no icon support. A standard drawer pattern improves discoverability, scalability, and visual hierarchy.

## Scope

### In Scope
- ModalNavigationDrawer with hamburger icon in TopAppBar
- NavigationDrawerItem per screen with Material icon + label
- Current screen highlighted in drawer
- Swipe-from-left-edge gesture to open drawer
- Screen enum extended with `icon` property (Material Icons)
- One new dep: `material-icons-extended`
- Strict TDD — drawer UI tests before implementation

### Out of Scope
- Screen composable changes (none needed — they all accept Modifier)
- Navigation library integration (androidx.navigation)
- Backend or database changes
- Animation customization beyond Material3 defaults
- Drawer header or footer content

## Capabilities

### New Capabilities
None — pure UI refactor, no new spec-level behavior.

### Modified Capabilities
None — screen routing behavior is unchanged. Only the visual input mechanism changes.

## Approach

1. Add `material-icons-extended` to `app/build.gradle.kts`
2. Add `icon: ImageVector` field to `Screen` enum, one per screen
3. Replace `FilterChip` `Row` with `ModalNavigationDrawerSheet` + `ModalNavigationDrawerItem` inside a `ModalNavigationDrawer`
4. Add `TopAppBar` with hamburger `IconButton` + `NavigationDrawerScope` | `rememberDrawerState`
5. Compose UI tests: drawer opens/closes, item selects screen, swipe gesture

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `app/build.gradle.kts` | Modified | Add `material-icons-extended` |
| `MainActivity.kt` | Modified | Screen enum + icon, drawer layout, TopAppBar |
| `app/src/androidTest/.../DrawerTest.kt` | New | Compose UI tests for drawer behavior |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| `material-icons-extended` increases APK size | Low | R8/proguard strips unused icons; ~60KB impact per Kleros icons used |
| Swipe gesture conflicts with edge-to-edge | Low | `enableEdgeToEdge()` already called; test on physical device |
| Existing tests break from Scaffold structure change | Low | Screen composables unchanged; existing tests remain green |

## Rollback Plan

Revert `MainActivity.kt` to prior commit, revert `app/build.gradle.kts` dependency line. All other files unchanged.

## Dependencies

- `androidx.compose.material:material-icons-extended` (version aligned with compose-bom)

## Success Criteria

- [ ] Drawer opens via hamburger tap and swipe gesture
- [ ] Each `NavigationDrawerItem` shows correct icon + label
- [ ] Selecting a drawer item navigates to correct screen
- [ ] Current screen highlighted in drawer
- [ ] Existing screen tests pass unchanged
- [ ] Compose UI tests for drawer pass
