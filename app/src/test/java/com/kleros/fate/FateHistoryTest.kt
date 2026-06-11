package com.kleros.fate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FateHistoryTest {

    @Test
    fun `empty history returns empty list`() {
        val history = FateHistory()
        assertTrue("results should be empty", history.results.isEmpty())
    }

    @Test
    fun `append 3 results yields size 3 with newest first`() {
        val r1 = FateResult.Yes(roll = 1, oddsLevel = OddsLevel.FIFTY_FIFTY, isDouble = false)
        val r2 = FateResult.Yes(roll = 2, oddsLevel = OddsLevel.FIFTY_FIFTY, isDouble = false)
        val r3 = FateResult.Yes(roll = 3, oddsLevel = OddsLevel.FIFTY_FIFTY, isDouble = false)

        val history = FateHistory()
            .append(r1)
            .append(r2)
            .append(r3)

        assertEquals(3, history.results.size)
        assertEquals(3, history.results[0].roll)
        assertEquals(2, history.results[1].roll)
        assertEquals(1, history.results[2].roll)
    }

    @Test
    fun `append 12 results caps at 10 discarding oldest`() {
        var history = FateHistory()
        for (i in 1..12) {
            history = history.append(
                FateResult.Yes(roll = i, oddsLevel = OddsLevel.FIFTY_FIFTY, isDouble = false),
            )
        }

        assertEquals(10, history.results.size)
        assertEquals(12, history.results[0].roll)
        assertEquals(3, history.results[9].roll)
    }

    @Test
    fun `MAX_SIZE is 10`() {
        assertEquals(10, FateHistory.MAX_SIZE)
    }
}
