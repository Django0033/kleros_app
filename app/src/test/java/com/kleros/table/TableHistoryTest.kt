package com.kleros.table

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TableHistoryTest {

    @Test
    fun `empty history returns empty list`() {
        val history = TableHistory()
        assertTrue(history.entries.isEmpty())
    }

    @Test
    fun `append 3 entries yields size 3 with newest first`() {
        val r1 = TableRollResult.Success("A")
        val r2 = TableRollResult.Success("B")
        val r3 = TableRollResult.Success("C")

        val history = TableHistory()
            .append(r1)
            .append(r2)
            .append(r3)

        assertEquals(3, history.entries.size)
        assertEquals("C", history.entries[0].value)
        assertEquals("B", history.entries[1].value)
        assertEquals("A", history.entries[2].value)
    }

    @Test
    fun `append 12 entries caps at maxSize discarding oldest`() {
        var history = TableHistory()
        for (i in 1..12) {
            history = history.append(TableRollResult.Success("N$i"))
        }

        assertEquals(10, history.entries.size)
        assertEquals("N12", history.entries[0].value)
        assertEquals("N3", history.entries[9].value)
    }

    @Test
    fun `custom maxSize parameter overrides default`() {
        var history = TableHistory(maxSize = 3)
        for (i in 1..5) {
            history = history.append(TableRollResult.Success("V$i"))
        }

        assertEquals(3, history.entries.size)
        assertEquals("V5", history.entries[0].value)
        assertEquals("V3", history.entries[2].value)
    }

    @Test
    fun `default maxSize is 10`() {
        assertEquals(10, TableHistory.MAX_SIZE)
    }
}
