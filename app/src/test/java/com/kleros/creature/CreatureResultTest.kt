package com.kleros.creature

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatureResultTest {

    @Test
    fun `constructor assigns all fields correctly`() {
        val result = CreatureResult(
            descriptors = listOf("Amorphous", "Loud"),
            abilities = listOf("Absorb", "Attach"),
            initialBehavior = "Friendly",
            statistics = "What you expect",
            newBehavior = null,
        )
        assertEquals(listOf("Amorphous", "Loud"), result.descriptors)
        assertEquals(listOf("Absorb", "Attach"), result.abilities)
        assertEquals("Friendly", result.initialBehavior)
        assertEquals("What you expect", result.statistics)
        assertNull(result.newBehavior)
    }

    @Test
    fun `timestampMillis is auto-populated and non-zero`() {
        val result = CreatureResult(
            descriptors = emptyList(),
            abilities = emptyList(),
            initialBehavior = "Inert, motionless",
            statistics = "What you expect",
        )
        assertTrue("timestampMillis should be > 0", result.timestampMillis > 0L)
    }

    @Test
    fun `newBehavior defaults to null`() {
        val result = CreatureResult(
            descriptors = emptyList(),
            abilities = emptyList(),
            initialBehavior = "Working, doing something",
            statistics = "About 50% higher",
        )
        assertNull(result.newBehavior)
    }

    @Test
    fun `copy produces new instance with changed field`() {
        val original = CreatureResult(
            descriptors = listOf("Amorphous"),
            abilities = listOf("Absorb"),
            initialBehavior = "Friendly",
            statistics = "What you expect",
            newBehavior = null,
        )
        val modified = original.copy(newBehavior = "Exhibits an Ability")
        assertNotEquals(original, modified)
        assertEquals("Exhibits an Ability", modified.newBehavior)
        assertEquals("Friendly", modified.initialBehavior)
    }

    @Test
    fun `copy with more descriptors`() {
        val original = CreatureResult(
            descriptors = listOf("Amorphous"),
            abilities = listOf("Absorb"),
            initialBehavior = "Friendly",
            statistics = "What you expect",
        )
        val modified = original.copy(
            descriptors = original.descriptors + "Large",
            abilities = original.abilities + "Attack",
        )
        assertEquals(listOf("Amorphous", "Large"), modified.descriptors)
        assertEquals(listOf("Absorb", "Attack"), modified.abilities)
    }
}
