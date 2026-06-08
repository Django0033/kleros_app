package com.kleros.dice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RollHistoryTest {

    @Test
    fun `empty history returns empty list`() {
        val history = RollHistory()
        assertTrue("results should be empty", history.results.isEmpty())
    }

    @Test
    fun `append 3 results yields size 3 with newest first`() {
        val r1 = DiceRollResult(DiceType.D6, value = 1)
        val r2 = DiceRollResult(DiceType.D6, value = 2)
        val r3 = DiceRollResult(DiceType.D6, value = 3)

        val history = RollHistory()
            .append(r1)
            .append(r2)
            .append(r3)

        assertEquals(3, history.results.size)
        assertEquals(3, history.results[0].value)
        assertEquals(2, history.results[1].value)
        assertEquals(1, history.results[2].value)
    }

    @Test
    fun `append 12 results caps at 10 discarding oldest`() {
        var history = RollHistory()
        for (i in 1..12) {
            history = history.append(DiceRollResult(DiceType.D20, value = i))
        }

        assertEquals(10, history.results.size)
        // Newest (12) at index 0, oldest kept is 3 (12-9 = 3)
        assertEquals(12, history.results[0].value)
        assertEquals(3, history.results[9].value)
    }

    @Test
    fun `timestamps are monotonically decreasing with newest at index 0`() {
        val t1 = 1000L
        val t2 = 2000L
        val t3 = 3000L

        val r1 = DiceRollResult(DiceType.D6, value = 1).copy(timestampMillis = t1)
        val r2 = DiceRollResult(DiceType.D6, value = 2).copy(timestampMillis = t2)
        val r3 = DiceRollResult(DiceType.D6, value = 3).copy(timestampMillis = t3)

        val history = RollHistory()
            .append(r1)
            .append(r2)
            .append(r3)

        val timestamps = history.results.map { it.timestampMillis }
        assertEquals(3, timestamps.size)
        assertTrue(
            "timestamp[0] >= timestamp[1]",
            timestamps[0] >= timestamps[1],
        )
        assertTrue(
            "timestamp[1] >= timestamp[2]",
            timestamps[1] >= timestamps[2],
        )
    }

    @Test
    fun `MAX_SIZE is 10`() {
        assertEquals(10, RollHistory.MAX_SIZE)
    }
}
