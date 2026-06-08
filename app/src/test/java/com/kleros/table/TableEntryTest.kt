@file:Suppress("MagicNumber")

package com.kleros.table

import org.junit.Assert.assertEquals
import org.junit.Test

class TableEntryTest {

    @Test
    fun `RANGE entry stores min, max, and result`() {
        val entry: TableEntry = TableEntry.RANGE(min = 1, max = 5, result = "A")
        assertEquals(1, (entry as TableEntry.RANGE).min)
        assertEquals(5, entry.max)
        assertEquals("A", entry.result)
    }

    @Test
    fun `DIRECT entry stores index and result`() {
        val entry: TableEntry = TableEntry.DIRECT(index = 2, result = "Lion")
        assertEquals(2, (entry as TableEntry.DIRECT).index)
        assertEquals("Lion", entry.result)
    }

    @Test
    fun `RANGE_MODIFIER entry stores min, max, result, and modifier`() {
        val entry: TableEntry = TableEntry.RANGE_MODIFIER(
            min = 1, max = 10, result = "Hit", modifier = 2,
        )
        assertEquals(1, (entry as TableEntry.RANGE_MODIFIER).min)
        assertEquals(10, entry.max)
        assertEquals("Hit", entry.result)
        assertEquals(2, entry.modifier)
    }
}
