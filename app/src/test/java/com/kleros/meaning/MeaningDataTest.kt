@file:Suppress("MagicNumber")

package com.kleros.meaning

import com.kleros.table.TableEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class MeaningDataTest {

    @Test
    fun `action table has 50 entries`() {
        assertEquals(50, MeaningData.action.entries.size)
    }

    @Test
    fun `description table has 50 entries`() {
        assertEquals(50, MeaningData.description.entries.size)
    }

    @Test
    fun `action entries cover ranges 1 to 100 without gaps or overlaps`() {
        val ranges = MeaningData.action.entries.map { it as TableEntry.RANGE }
        var expectedMin = 1
        for (entry in ranges) {
            assertEquals(expectedMin, entry.min)
            assertEquals(expectedMin + 1, entry.max)
            expectedMin = entry.max + 1
        }
        assertEquals(101, expectedMin)
    }

    @Test
    fun `description entries cover ranges 1 to 100 without gaps or overlaps`() {
        val ranges = MeaningData.description.entries.map { it as TableEntry.RANGE }
        var expectedMin = 1
        for (entry in ranges) {
            assertEquals(expectedMin, entry.min)
            assertEquals(expectedMin + 1, entry.max)
            expectedMin = entry.max + 1
        }
        assertEquals(101, expectedMin)
    }

    @Test
    fun `first action entry is 1 to 2 Attain`() {
        val first = MeaningData.action.entries.first() as TableEntry.RANGE
        assertEquals(1, first.min)
        assertEquals(2, first.max)
        assertEquals("Attain", first.result)
    }

    @Test
    fun `last action entry is 99 to 100 Uncertain`() {
        val last = MeaningData.action.entries.last() as TableEntry.RANGE
        assertEquals(99, last.min)
        assertEquals(100, last.max)
        assertEquals("Uncertain", last.result)
    }

    @Test
    fun `first description entry is 1 to 2 Artificial`() {
        val first = MeaningData.description.entries.first() as TableEntry.RANGE
        assertEquals(1, first.min)
        assertEquals(2, first.max)
        assertEquals("Artificial", first.result)
    }

    @Test
    fun `last description entry is 99 to 100 Weird`() {
        val last = MeaningData.description.entries.last() as TableEntry.RANGE
        assertEquals(99, last.min)
        assertEquals(100, last.max)
        assertEquals("Weird", last.result)
    }
}
