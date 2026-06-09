# Archive Report: Navigation Drawer

**Status**: ✅ Complete
**Date**: 2026-06-07
**Change**: kleros-navigation-drawer

## Summary
Replaced FilterChip nav bar with Material3 ModalNavigationDrawer + TopAppBar. Added material-icons-extended dependency and 3 UI tests.

## Files
- 3 modified (version catalog, build.gradle.kts, MainActivity.kt)
- 1 new test (NavigationDrawerTest.kt)
- ~446 lines total

## Changes
- Screen enum gained `icon: ImageVector`
- FilterChip row replaced with drawer + hamburger
- TopAppBar shows current screen label
- Drawer closes on item selection
- Fixed "Char Caft" → "Char Craft" typo
