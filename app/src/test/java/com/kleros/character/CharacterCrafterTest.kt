package com.kleros.character

import com.kleros.dice.DiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterCrafterTest {

    @Test
    fun `deterministic rolls produce expected descriptors and stat`() {
        val rollValues = mutableListOf(
            12,  // identity → Charm (index 12)
            55,  // mind → Heroic (index 55)
            88,  // body → Speed (index 88)
            3,   // talent → Agile (index 3)
            6,   // statistics → "What you expect" (range 4-7)
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("Charm", result.identity)
        assertEquals("Heroic", result.mind)
        assertEquals("Speed", result.body)
        assertEquals("Agile", result.talent)
        assertEquals("What you expect", result.statistics)
    }

    @Test
    fun `all descriptor rolls return 1 produces Academic for all slots`() {
        val rollFn: (DiceType) -> Int = { 1 }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("Academic", result.identity)
        assertEquals("Academic", result.mind)
        assertEquals("Academic", result.body)
        assertEquals("Academic", result.talent)
    }

    @Test
    fun `stat roll 1 maps to About 50 percent lower`() {
        val rollValues = mutableListOf(1, 1, 1, 1, 1)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("About 50% lower", result.statistics)
    }

    @Test
    fun `stat roll 2 maps to About 25 percent lower`() {
        val rollValues = mutableListOf(1, 1, 1, 1, 2)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("About 25% lower", result.statistics)
    }

    @Test
    fun `stat roll 3 maps to About 25 percent lower`() {
        val rollValues = mutableListOf(1, 1, 1, 1, 3)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("About 25% lower", result.statistics)
    }

    @Test
    fun `stat roll 4 maps to What you expect`() {
        val rollValues = mutableListOf(1, 1, 1, 1, 4)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("What you expect", result.statistics)
    }

    @Test
    fun `stat roll 7 maps to What you expect`() {
        val rollValues = mutableListOf(1, 1, 1, 1, 7)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("What you expect", result.statistics)
    }

    @Test
    fun `stat roll 8 maps to About 25 percent higher`() {
        val rollValues = mutableListOf(1, 1, 1, 1, 8)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("About 25% higher", result.statistics)
    }

    @Test
    fun `stat roll 9 maps to About 25 percent higher`() {
        val rollValues = mutableListOf(1, 1, 1, 1, 9)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("About 25% higher", result.statistics)
    }

    @Test
    fun `stat roll 10 maps to About 50 percent higher`() {
        val rollValues = mutableListOf(1, 1, 1, 1, 10)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("About 50% higher", result.statistics)
    }

    @Test
    fun `1000 invocations with random rolls all slots populated and stat non-empty`() {
        repeat(1000) {
            val result = CharacterCrafter.generate()
            assertTrue("identity should be non-empty", result.identity.isNotBlank())
            assertTrue("mind should be non-empty", result.mind.isNotBlank())
            assertTrue("body should be non-empty", result.body.isNotBlank())
            assertTrue("talent should be non-empty", result.talent.isNotBlank())
            assertTrue("statistics should be non-empty", result.statistics.isNotBlank())
        }
    }

    @Test
    fun `different rolls produce different slot values`() {
        val rollValues = mutableListOf(1, 25, 50, 75, 5)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CharacterCrafter.generate(rollFn = rollFn)

        assertEquals("Academic", result.identity)
        assertEquals("Dangerous", result.mind)
        assertEquals("Guide", result.body)
        assertEquals("Perception", result.talent)
    }

    @Test
    fun `timestampMillis is auto-populated`() {
        val result = CharacterCrafter.generate()
        assertTrue("timestampMillis should be > 0", result.timestampMillis > 0L)
    }
}
