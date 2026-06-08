package com.kleros.namegenerator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NameResultTest {

    @Test
    fun `constructor assigns fields correctly`() {
        val result = NameResult(name = "Elara", rollMode = RollMode.NORMAL)
        assertEquals("Elara", result.name)
        assertEquals(RollMode.NORMAL, result.rollMode)
    }

    @Test
    fun `timestampMillis is auto-populated and non-zero`() {
        val result = NameResult(name = "Morgana", rollMode = RollMode.ADVANTAGE)
        assertTrue("timestampMillis should be > 0", result.timestampMillis > 0L)
    }

    @Test
    fun `copy produces new instance with changed field`() {
        val original = NameResult(name = "Thorne", rollMode = RollMode.DISADVANTAGE)
        val modified = original.copy(name = "Vale")
        assertNotEquals(original, modified)
        assertEquals("Vale", modified.name)
        assertEquals(RollMode.DISADVANTAGE, modified.rollMode)
    }
}
