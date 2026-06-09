package com.kleros.creature

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatureHistoryTest {

    @Test
    fun `empty history returns empty list`() {
        val history = CreatureHistory()
        assertTrue("results should be empty", history.results.isEmpty())
    }

    @Test
    fun `append 3 results yields size 3 with newest first`() {
        val r1 = CreatureResult(
            descriptors = listOf("A"), abilities = emptyList(),
            initialBehavior = "", statistics = "",
        )
        val r2 = CreatureResult(
            descriptors = listOf("B"), abilities = emptyList(),
            initialBehavior = "", statistics = "",
        )
        val r3 = CreatureResult(
            descriptors = listOf("C"), abilities = emptyList(),
            initialBehavior = "", statistics = "",
        )

        val history = CreatureHistory()
            .append(r1)
            .append(r2)
            .append(r3)

        assertEquals(3, history.results.size)
        assertEquals(listOf("C"), history.results[0].descriptors)
        assertEquals(listOf("B"), history.results[1].descriptors)
        assertEquals(listOf("A"), history.results[2].descriptors)
    }

    @Test
    fun `append 12 results caps at 10 discarding oldest`() {
        var history = CreatureHistory()
        for (i in 1..12) {
            history = history.append(
                CreatureResult(
                    descriptors = listOf("N$i"),
                    abilities = emptyList(),
                    initialBehavior = "",
                    statistics = "",
                ),
            )
        }

        assertEquals(10, history.results.size)
        assertEquals(listOf("N12"), history.results[0].descriptors)
        assertEquals(listOf("N3"), history.results[9].descriptors)
    }

    @Test
    fun `MAX_SIZE is 10`() {
        assertEquals(10, CreatureHistory.MAX_SIZE)
    }
}
