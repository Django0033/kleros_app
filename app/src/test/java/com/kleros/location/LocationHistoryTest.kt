package com.kleros.location

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationHistoryTest {

    @Test
    fun `empty history returns empty list`() {
        val history = LocationHistory()
        assertTrue("results should be empty", history.results.isEmpty())
    }

    @Test
    fun `append 3 results yields size 3 with newest first`() {
        val r1 = LocationResult(descriptors = listOf("A"))
        val r2 = LocationResult(descriptors = listOf("B"))
        val r3 = LocationResult(descriptors = listOf("C"))

        val history = LocationHistory()
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
        var history = LocationHistory()
        for (i in 1..12) {
            history = history.append(
                LocationResult(descriptors = listOf("N$i")),
            )
        }

        assertEquals(10, history.results.size)
        assertEquals(listOf("N12"), history.results[0].descriptors)
        assertEquals(listOf("N3"), history.results[9].descriptors)
    }

    @Test
    fun `MAX_SIZE is 10`() {
        assertEquals(10, LocationHistory.MAX_SIZE)
    }
}
