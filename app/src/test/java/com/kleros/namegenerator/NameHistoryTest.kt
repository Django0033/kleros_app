package com.kleros.namegenerator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NameHistoryTest {

    @Test
    fun `empty history returns empty list`() {
        val history = NameHistory()
        assertTrue("results should be empty", history.results.isEmpty())
    }

    @Test
    fun `append 3 results yields size 3 with newest first`() {
        val r1 = NameResult(name = "A", rollMode = RollMode.NORMAL)
        val r2 = NameResult(name = "B", rollMode = RollMode.NORMAL)
        val r3 = NameResult(name = "C", rollMode = RollMode.NORMAL)

        val history = NameHistory()
            .append(r1)
            .append(r2)
            .append(r3)

        assertEquals(3, history.results.size)
        assertEquals("C", history.results[0].name)
        assertEquals("B", history.results[1].name)
        assertEquals("A", history.results[2].name)
    }

    @Test
    fun `append 12 results caps at 10 discarding oldest`() {
        var history = NameHistory()
        for (i in 1..12) {
            history = history.append(
                NameResult(name = "N$i", rollMode = RollMode.NORMAL),
            )
        }

        assertEquals(10, history.results.size)
        assertEquals("N12", history.results[0].name)
        assertEquals("N3", history.results[9].name)
    }

    @Test
    fun `timestamps are monotonically decreasing with newest at index 0`() {
        val t1 = 1000L
        val t2 = 2000L
        val t3 = 3000L

        val r1 = NameResult(name = "A", rollMode = RollMode.NORMAL).copy(timestampMillis = t1)
        val r2 = NameResult(name = "B", rollMode = RollMode.NORMAL).copy(timestampMillis = t2)
        val r3 = NameResult(name = "C", rollMode = RollMode.NORMAL).copy(timestampMillis = t3)

        val history = NameHistory()
            .append(r1)
            .append(r2)
            .append(r3)

        val timestamps = history.results.map { it.timestampMillis }
        assertEquals(3, timestamps.size)
        assertTrue("timestamp[0] >= timestamp[1]", timestamps[0] >= timestamps[1])
        assertTrue("timestamp[1] >= timestamp[2]", timestamps[1] >= timestamps[2])
    }

    @Test
    fun `MAX_SIZE is 10`() {
        assertEquals(10, NameHistory.MAX_SIZE)
    }
}
