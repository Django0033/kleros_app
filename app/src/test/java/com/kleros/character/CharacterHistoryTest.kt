package com.kleros.character

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterHistoryTest {

    @Test
    fun `empty history returns empty list`() {
        val history = CharacterHistory()
        assertTrue("results should be empty", history.results.isEmpty())
    }

    @Test
    fun `append 3 results yields size 3 with newest first`() {
        val r1 = CharacterResult(identity = "A", mind = "", body = "", talent = "", statistics = "")
        val r2 = CharacterResult(identity = "B", mind = "", body = "", talent = "", statistics = "")
        val r3 = CharacterResult(identity = "C", mind = "", body = "", talent = "", statistics = "")

        val history = CharacterHistory()
            .append(r1)
            .append(r2)
            .append(r3)

        assertEquals(3, history.results.size)
        assertEquals("C", history.results[0].identity)
        assertEquals("B", history.results[1].identity)
        assertEquals("A", history.results[2].identity)
    }

    @Test
    fun `append 12 results caps at 10 discarding oldest`() {
        var history = CharacterHistory()
        for (i in 1..12) {
            history = history.append(
                CharacterResult(identity = "N$i", mind = "", body = "", talent = "", statistics = ""),
            )
        }

        assertEquals(10, history.results.size)
        assertEquals("N12", history.results[0].identity)
        assertEquals("N3", history.results[9].identity)
    }

    @Test
    fun `MAX_SIZE is 10`() {
        assertEquals(10, CharacterHistory.MAX_SIZE)
    }
}
