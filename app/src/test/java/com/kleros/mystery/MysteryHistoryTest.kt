package com.kleros.mystery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MysteryHistoryTest {

    @Test
    fun `empty history returns empty list`() {
        val history = MysteryHistory()
        assertTrue("results should be empty", history.results.isEmpty())
    }

    @Test
    fun `append 3 results yields size 3 with newest first`() {
        val r1 = MysteryResult(discoveryResult = "A", descriptor = null, boxes = 0, isDefinitive = false)
        val r2 = MysteryResult(discoveryResult = "B", descriptor = null, boxes = 0, isDefinitive = false)
        val r3 = MysteryResult(discoveryResult = "C", descriptor = null, boxes = 0, isDefinitive = false)

        val history = MysteryHistory()
            .append(r1)
            .append(r2)
            .append(r3)

        assertEquals(3, history.results.size)
        assertEquals("C", history.results[0].discoveryResult)
        assertEquals("B", history.results[1].discoveryResult)
        assertEquals("A", history.results[2].discoveryResult)
    }

    @Test
    fun `append 12 results caps at 10 discarding oldest`() {
        var history = MysteryHistory()
        for (i in 1..12) {
            history = history.append(
                MysteryResult(discoveryResult = "N$i", descriptor = null, boxes = 0, isDefinitive = false),
            )
        }

        assertEquals(10, history.results.size)
        assertEquals("N12", history.results[0].discoveryResult)
        assertEquals("N3", history.results[9].discoveryResult)
    }

    @Test
    fun `MAX_SIZE is 10`() {
        assertEquals(10, MysteryHistory.MAX_SIZE)
    }
}
