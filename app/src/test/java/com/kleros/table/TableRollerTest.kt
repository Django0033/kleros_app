@file:Suppress("MagicNumber")

package com.kleros.table

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TableRollerTest {

    // -- RANGE tests --

    @Test
    fun `RANGE returns entry whose range contains the roll`() {
        val tableDef = TableDef(
            name = "test",
            entries = listOf(
                TableEntry.RANGE(min = 1, max = 5, result = "A"),
                TableEntry.RANGE(min = 6, max = 10, result = "B"),
            ),
        )

        val result1 = TableRoller.roll(tableDef, rollFn = { 3 })
        val success1 = result1 as TableRollResult.Success
        assertEquals("A", success1.value)

        val result2 = TableRoller.roll(tableDef, rollFn = { 8 })
        val success2 = result2 as TableRollResult.Success
        assertEquals("B", success2.value)
    }

    @Test
    fun `RANGE first matching entry wins with overlapping ranges`() {
        val tableDef = TableDef(
            name = "overlap",
            entries = listOf(
                TableEntry.RANGE(min = 1, max = 10, result = "First"),
                TableEntry.RANGE(min = 5, max = 15, result = "Second"),
            ),
        )

        val result = TableRoller.roll(tableDef, rollFn = { 7 })
        val success = result as TableRollResult.Success
        assertEquals("First", success.value)
    }

    @Test
    fun `RANGE no match within any entry returns Error`() {
        val tableDef = TableDef(
            name = "test",
            entries = listOf(
                TableEntry.RANGE(min = 1, max = 5, result = "A"),
            ),
        )

        val result = TableRoller.roll(tableDef, rollFn = { 10 })
        assertTrue(result is TableRollResult.Error)
        assertEquals("No entry matches roll 10", (result as TableRollResult.Error).message)
    }

    // -- DIRECT tests --

    @Test
    fun `DIRECT returns entry whose index equals the roll`() {
        val tableDef = TableDef(
            name = "animals",
            entries = listOf(
                TableEntry.DIRECT(index = 1, result = "Eagle"),
                TableEntry.DIRECT(index = 2, result = "Lion"),
                TableEntry.DIRECT(index = 3, result = "Serpent"),
            ),
        )

        val result1 = TableRoller.roll(tableDef, rollFn = { 1 })
        assertEquals("Eagle", (result1 as TableRollResult.Success).value)

        val result2 = TableRoller.roll(tableDef, rollFn = { 2 })
        assertEquals("Lion", (result2 as TableRollResult.Success).value)
    }

    @Test
    fun `DIRECT no matching index returns Error`() {
        val tableDef = TableDef(
            name = "single",
            entries = listOf(
                TableEntry.DIRECT(index = 5, result = "Only"),
            ),
        )

        val result = TableRoller.roll(tableDef, rollFn = { 1 })
        assertTrue(result is TableRollResult.Error)
    }

    // -- RANGE_MODIFIER tests --

    @Test
    fun `RANGE_MODIFIER applies modifier to roll within range`() {
        val tableDef = TableDef(
            name = "mod-test",
            entries = listOf(
                TableEntry.RANGE_MODIFIER(min = 1, max = 5, result = "A", modifier = 2),
                TableEntry.RANGE_MODIFIER(min = 6, max = 10, result = "B", modifier = 2),
            ),
        )

        // roll 1 → in [1,5], effective = 1+2 = 3, clamped to [1,5] → 3
        val result1 = TableRoller.roll(tableDef, rollFn = { 1 })
        assertEquals("A", (result1 as TableRollResult.Success).value)

        // roll 6 → in [6,10], effective = 6+2 = 8, clamped to [6,10] → 8
        val result2 = TableRoller.roll(tableDef, rollFn = { 6 })
        assertEquals("B", (result2 as TableRollResult.Success).value)
    }

    @Test
    fun `RANGE_MODIFIER negative modifier clamped to entry min`() {
        val tableDef = TableDef(
            name = "clamp-min",
            entries = listOf(
                TableEntry.RANGE_MODIFIER(min = 10, max = 20, result = "X", modifier = -20),
            ),
        )

        // roll 15 → in [10,20], effective = 15-20 = -5, clamped to [10,20] → 10
        val result = TableRoller.roll(tableDef, rollFn = { 15 })
        assertEquals("X", (result as TableRollResult.Success).value)
    }

    @Test
    fun `RANGE_MODIFIER large modifier capped at entry max`() {
        val tableDef = TableDef(
            name = "clamp-max",
            entries = listOf(
                TableEntry.RANGE_MODIFIER(min = 10, max = 20, result = "X", modifier = 20),
            ),
        )

        // roll 15 → in [10,20], effective = 15+20 = 35, clamped to [10,20] → 20
        val result = TableRoller.roll(tableDef, rollFn = { 15 })
        assertEquals("X", (result as TableRollResult.Success).value)
    }

    @Test
    fun `RANGE_MODIFIER roll outside range skips to next entry`() {
        val tableDef = TableDef(
            name = "skip",
            entries = listOf(
                TableEntry.RANGE_MODIFIER(min = 1, max = 5, result = "Hit", modifier = 2),
                TableEntry.RANGE(min = 6, max = 10, result = "Fallback"),
            ),
        )

        // roll 8 → not in [1,5], falls through to RANGE(6,10) → match
        val result = TableRoller.roll(tableDef, rollFn = { 8 })
        assertEquals("Fallback", (result as TableRollResult.Success).value)
    }

    // -- Edge case tests --

    @Test
    fun `empty entries list returns Error`() {
        val tableDef = TableDef(name = "empty", entries = emptyList())

        val result = TableRoller.roll(tableDef, rollFn = { 1 })
        assertTrue(result is TableRollResult.Error)
    }

    @Test
    fun `rollFn injection with deterministic lambda`() {
        val tableDef = TableDef(
            name = "deterministic",
            entries = listOf(
                TableEntry.RANGE(min = 1, max = 1, result = "Only"),
            ),
        )

        val result = TableRoller.roll(tableDef, rollFn = { 1 })
        assertEquals("Only", (result as TableRollResult.Success).value)
    }

    @Test
    fun `error message includes the actual roll value`() {
        val tableDef = TableDef(
            name = "test",
            entries = listOf(
                TableEntry.RANGE(min = 1, max = 5, result = "A"),
            ),
        )

        val result = TableRoller.roll(tableDef, rollFn = { 99 })
        assertEquals("No entry matches roll 99", (result as TableRollResult.Error).message)
    }
}
