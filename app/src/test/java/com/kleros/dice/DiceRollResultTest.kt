package com.kleros.dice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceRollResultTest {

    @Test
    fun `construct with D20 and value 15`() {
        val result = DiceRollResult(diceType = DiceType.D20, value = 15)
        assertEquals(DiceType.D20, result.diceType)
        assertEquals(15, result.value)
    }

    @Test
    fun `timestampMillis is populated and non-zero`() {
        val result = DiceRollResult(diceType = DiceType.D6, value = 3)
        assertTrue("timestampMillis should be > 0", result.timestampMillis > 0L)
    }

    @Test
    fun `copy with changed value produces different instance`() {
        val original = DiceRollResult(diceType = DiceType.D8, value = 5)
        val modified = original.copy(value = 7)
        assertNotEquals(original, modified)
        assertEquals(7, modified.value)
        assertEquals(DiceType.D8, modified.diceType)
    }
}
