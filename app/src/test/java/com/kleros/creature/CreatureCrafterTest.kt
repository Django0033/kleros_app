package com.kleros.creature

import com.kleros.dice.DiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatureCrafterTest {

    @Test
    fun `deterministic rolls produce expected values`() {
        val rollValues = mutableListOf(
            12,  // descriptor 1
            55,  // descriptor 2
            3,   // ability 1 → Attach (3-4)
            7,   // ability 2 → Bypass (7-8)
            5,   // initial behavior → Friendly (5)
            6,   // statistics → "What you expect" (range 4-7)
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CreatureCrafter.generate(rollFn = rollFn)

        assertEquals(2, result.descriptors.size)
        assertEquals(2, result.abilities.size)
        assertEquals("Friendly", result.initialBehavior)
        assertNotNull(result.statistics)
        assertNull(result.newBehavior)
    }

    @Test
    fun `roll 1 for first descriptor returns Amorphous`() {
        val rollValues = mutableListOf(1, 2, 3, 1, 1, 5)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CreatureCrafter.generate(rollFn = rollFn)

        assertEquals("Amorphous", result.descriptors[0])
    }

    @Test
    fun `roll 100 for abilities returns Strength`() {
        val rollValues = mutableListOf(1, 2, 3, 100, 5, 5)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CreatureCrafter.generate(rollFn = rollFn)

        assertTrue(result.abilities.contains("Strength"))
    }

    @Test
    fun `roll 10 for initial behavior returns Exhibits an Ability`() {
        val rollValues = mutableListOf(1, 2, 3, 1, 10, 5)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CreatureCrafter.generate(rollFn = rollFn)

        assertEquals("Exhibits an Ability", result.initialBehavior)
    }

    @Test
    fun `roll 1 for statistics returns About 50 percent lower`() {
        val rollValues = mutableListOf(1, 2, 3, 1, 1, 1)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CreatureCrafter.generate(rollFn = rollFn)

        assertEquals("About 50% lower", result.statistics)
    }

    @Test
    fun `rollDescriptor appends new descriptor`() {
        val rollValues = mutableListOf(1, 2, 3, 1, 1, 5)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CreatureCrafter.generate(rollFn = rollFn)

        val withExtra = CreatureCrafter.rollDescriptor(result, rollFn = { 50 })
        assertEquals(3, withExtra.descriptors.size)
        assertEquals("Extra", withExtra.descriptors[2])
    }

    @Test
    fun `rollAbility appends new ability`() {
        val rollValues = mutableListOf(1, 2, 3, 1, 1, 5)
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = CreatureCrafter.generate(rollFn = rollFn)

        val withExtra = CreatureCrafter.rollAbility(result, rollFn = { 4 })
        assertEquals(3, withExtra.abilities.size)
        assertEquals("Attach", withExtra.abilities[2])
    }

    @Test
    fun `rollNewBehavior sets newBehavior field`() {
        val result = CreatureResult(
            descriptors = listOf("Amorphous"),
            abilities = listOf("Absorb"),
            initialBehavior = "Friendly",
            statistics = "What you expect",
        )

        val withBehavior = CreatureCrafter.rollNewBehavior(result, rollFn = { 10 })
        assertEquals("Exhibits an Ability", withBehavior.newBehavior)
    }

    @Test
    fun `rollNewBehavior does not affect previous fields`() {
        val result = CreatureResult(
            descriptors = listOf("Amorphous", "Loud"),
            abilities = listOf("Absorb"),
            initialBehavior = "Friendly",
            statistics = "What you expect",
        )

        val withBehavior = CreatureCrafter.rollNewBehavior(result, rollFn = { 1 })
        assertEquals("Acts as expected", withBehavior.newBehavior)
        assertEquals(listOf("Amorphous", "Loud"), withBehavior.descriptors)
        assertEquals(listOf("Absorb"), withBehavior.abilities)
        assertEquals("Friendly", withBehavior.initialBehavior)
        assertEquals("What you expect", withBehavior.statistics)
    }

    @Test
    fun `rollStatistics replaces statistics`() {
        val result = CreatureResult(
            descriptors = listOf("Amorphous"),
            abilities = listOf("Absorb"),
            initialBehavior = "Friendly",
            statistics = "What you expect",
        )

        val updated = CreatureCrafter.rollStatistics(result, rollFn = { 10 })
        assertEquals("About 50% higher", updated.statistics)
    }

    @Test
    fun `rollInitialBehavior replaces initialBehavior`() {
        val result = CreatureResult(
            descriptors = listOf("Amorphous"),
            abilities = listOf("Absorb"),
            initialBehavior = "Friendly",
            statistics = "What you expect",
        )

        val updated = CreatureCrafter.rollInitialBehavior(result, rollFn = { 4 })
        assertEquals("Wary and alert", updated.initialBehavior)
    }

    @Test
    fun `rollNewBehavior replaces existing newBehavior`() {
        val result = CreatureResult(
            descriptors = listOf("Amorphous"),
            abilities = listOf("Absorb"),
            initialBehavior = "Friendly",
            statistics = "What you expect",
            newBehavior = "Exhibits an Ability",
        )

        val updated = CreatureCrafter.rollNewBehavior(result, rollFn = { 1 })
        assertEquals("Acts as expected", updated.newBehavior)
    }

    @Test
    fun `1000 invocations with random rolls all fields populated`() {
        repeat(1000) {
            val result = CreatureCrafter.generate()
            assertTrue("descriptors should be non-empty", result.descriptors.isNotEmpty())
            assertTrue("abilities should be non-empty", result.abilities.isNotEmpty())
            assertTrue("initialBehavior should be non-empty", result.initialBehavior.isNotBlank())
            assertTrue("statistics should be non-empty", result.statistics.isNotBlank())
        }
    }

    @Test
    fun `rollDescriptor with roll 1 returns Amorphous`() {
        val result = CreatureResult(
            descriptors = listOf("Loud"),
            abilities = listOf("Attack"),
            initialBehavior = "Friendly",
            statistics = "About 25% higher",
        )

        val updated = CreatureCrafter.rollDescriptor(result, rollFn = { 1 })
        assertEquals("Amorphous", updated.descriptors.last())
    }

    @Test
    fun `rollAbility with roll 1 returns Absorb`() {
        val result = CreatureResult(
            descriptors = listOf("Amorphous"),
            abilities = listOf("Attack"),
            initialBehavior = "Friendly",
            statistics = "About 25% higher",
        )

        val updated = CreatureCrafter.rollAbility(result, rollFn = { 1 })
        assertEquals("Absorb", updated.abilities.last())
    }
}
