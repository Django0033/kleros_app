# Tasks: Mystery Crafter — Accumulator-Based Discovery

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~540 (replace across 7 files, 2 test + 5 source) |
| 400-line budget risk | Medium |
| Chained PRs recommended | No |
| Suggested split | Single PR (tightly coupled — every file depends on the data model) |
| Delivery strategy | single-pr |

```
Decision needed before apply: Yes
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Medium
```

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Foundation + Data + Model + Core Logic + UI + Tests | PR 1 | Single PR; changes are tightly coupled. Ask maintainer for `size:exception`. |

## Phase 1: Data & Model

- [x] 1.1 Rewrite `MysteryData.kt` — replace `rewardTiers` with `discovery` (7 RANGE entries: `Nothing useful found` through `Connect existing Clue to existing Suspect`, plus the 101+ definitive entry) + replace 100 descriptors with the correct mystery-word list (Accident, Aggressive, Ambition…Witness)
- [x] 1.2 Write `MysteryDataTest.kt` — 7 RANGE entries cover 1..100 with no gaps, 100 DIRECT at indices 1..100, non-empty results
- [x] 1.3 Rewrite `MysteryResult.kt` — replace `BoxContent` model with `discovery: String`, `descriptor: String`, `isDefinitive: Boolean`, `boxes: Int`, `timestampMillis: Long`; delete `BoxContent`
- [x] 1.4 Write `MysteryResultTest.kt` — fields populated correctly, timestamp auto-set, immutability
- [x] 1.5 Verify `MysteryHistory.kt` — no changes needed (generic `append`/`take` pattern already works)

## Phase 2: Core Logic

- [x] 2.1 Rewrite `MysteryCrafter.kt` — `check(rollFn, boxes: Int)`: roll 1d100, add `boxes`, if > 100 → `isDefinitive = true` with definitive discovery string, else first-match RANGE lookup; always roll one descriptor via 1d100 DIRECT lookup
- [x] 2.2 Add standalone `MysteryCrafter.rollDescriptor(rollFn)` → `String` (pure descriptor roll)
- [x] 2.3 Write `MysteryCrafterTest.kt` — accumulator under-threshold yields RANGE discovery; overflow yields definitive; descriptor always drawn; `boxes` field preserved; edge cases at boundaries (roll=1+boxes=99, roll=1+boxes=100, roll=100+boxes=0)

## Phase 3: UI & Integration

- [x] 3.1 Rewrite `MysteryScreen.kt` — state: `boxes` (0 initial), `currentResult`, `history`; display boxes count; "Check" button (disabled at boxes≥20); result card shows discovery + descriptor; definitive badge when applicable; history section with 10-entry cap
- [x] 3.2 Rewrite `MysteryScreenTest.kt` — screen smoke, check button visible, result card displayed after tap, history accumulates
- [x] 3.3 Verify `MainActivity.kt` — `Screen.MYSTERY_CRAFTER` entry and `when` branch already exist; confirm navigation drawer shows "Myst Craft" and routes to MysteryScreen

## Phase 4: Quality

- [x] 4.1 Run `./gradlew detekt` — fix any style issues
- [x] 4.2 Run `./gradlew test` — all unit tests green
- [ ] 4.3 Run `./gradlew connectedCheck` — all instrumented tests green (or emulator CI) — no device available
