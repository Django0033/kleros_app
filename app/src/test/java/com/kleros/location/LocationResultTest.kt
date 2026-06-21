package com.kleros.location

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationResultTest {

    @Test
    fun `constructor assigns all fields correctly`() {
        val result = LocationResult(
            descriptors = listOf("Abandoned", "Dark"),
            elements = listOf("Expected", "Random"),
        )
        assertEquals(listOf("Abandoned", "Dark"), result.descriptors)
        assertEquals(listOf("Expected", "Random"), result.elements)
    }

    @Test
    fun `timestampMillis is auto-populated and non-zero`() {
        val result = LocationResult()
        assertTrue("timestampMillis should be > 0", result.timestampMillis > 0L)
    }

    @Test
    fun `copy produces new instance with changed field`() {
        val original = LocationResult(
            descriptors = listOf("Abandoned"),
            elements = listOf("None"),
        )
        val modified = original.copy(descriptors = listOf("Dark", "Abandoned"))
        assertNotEquals(original, modified)
        assertEquals(listOf("Dark", "Abandoned"), modified.descriptors)
        assertEquals(listOf("None"), modified.elements)
    }
}
