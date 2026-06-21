package com.kleros.mystery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MysteryResultTest {

    @Test
    fun `constructor assigns all fields correctly`() {
        val result = MysteryResult(
            discoveryResult = "New Suspect not connected",
            descriptor = "Betray",
            boxes = 3,
            isDefinitive = false,
        )
        assertEquals("New Suspect not connected", result.discoveryResult)
        assertEquals("Betray", result.descriptor)
        assertEquals(3, result.boxes)
        assertEquals(false, result.isDefinitive)
    }

    @Test
    fun `descriptor can be null`() {
        val result = MysteryResult(
            discoveryResult = "Nothing useful found",
            descriptor = null,
            boxes = 0,
            isDefinitive = false,
        )
        assertNull(result.descriptor)
    }

    @Test
    fun `timestampMillis is auto-populated and non-zero`() {
        val result = MysteryResult(
            discoveryResult = "Nothing useful found",
            descriptor = null,
            boxes = 0,
            isDefinitive = false,
        )
        assertTrue("timestampMillis should be > 0", result.timestampMillis > 0L)
    }

    @Test
    fun `isDefinitive true stores correctly`() {
        val result = MysteryResult(
            discoveryResult = "Definitive clue: connected Suspect is the answer",
            descriptor = "Motive",
            boxes = 20,
            isDefinitive = true,
        )
        assertTrue(result.isDefinitive)
        assertEquals(20, result.boxes)
    }
}
