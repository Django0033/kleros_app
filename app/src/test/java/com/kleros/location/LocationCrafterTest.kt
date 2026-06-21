package com.kleros.location

import com.kleros.dice.DiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationCrafterTest {

    @Test
    fun `deterministic descriptor roll produces correct descriptor`() {
        val rollFn: (DiceType) -> Int = { 1 }
        val result = LocationCrafter.rollDescriptor(rollFn = rollFn)
        assertEquals("Abandoned", result.descriptors.single())
        assertTrue(result.elements.isEmpty())
    }

    @Test
    fun `descriptor roll 100 produces Winding`() {
        val rollFn: (DiceType) -> Int = { 100 }
        val result = LocationCrafter.rollDescriptor(rollFn = rollFn)
        assertEquals("Winding", result.descriptors.single())
    }

    @Test
    fun `descriptor roll appends to existing descriptors`() {
        val rollFn: (DiceType) -> Int = { 50 }
        val first = LocationCrafter.rollDescriptor(rollFn = rollFn)
        assertEquals("Inactive", first.descriptors.single())

        val rollFn2: (DiceType) -> Int = { 51 }
        val second = LocationCrafter.rollDescriptor(result = first, rollFn = rollFn2)
        assertEquals(listOf("Inactive", "Large"), second.descriptors)
    }

    @Test
    fun `descriptor roll without result creates new LocationResult`() {
        val rollFn: (DiceType) -> Int = { 1 }
        val result = LocationCrafter.rollDescriptor(rollFn = rollFn)
        assertNotNull(result)
        assertEquals(1, result.descriptors.size)
        assertTrue(result.elements.isEmpty())
    }

    @Test
    fun `deterministic element roll 2d10 plus PP produces correct element`() {
        // D10 rolls: 1 + 1 = 2, plus PP=0 → total 2 → "None"
        val rolls = mutableListOf(1, 1)
        val rollFn: (DiceType) -> Int = { rolls.removeFirst() }
        val result = LocationCrafter.rollElement(pp = 0, rollFn = rollFn)
        assertEquals("None", result.elements.single())
        assertTrue(result.descriptors.isEmpty())
    }

    @Test
    fun `element roll with high roll and PP produces expected result`() {
        // D10 rolls: 10 + 10 = 20, plus PP=0 → total 20 → "Known, Climax"
        val rolls = mutableListOf(10, 10)
        val rollFn: (DiceType) -> Int = { rolls.removeFirst() }
        val result = LocationCrafter.rollElement(pp = 0, rollFn = rollFn)
        assertEquals("Known, Climax", result.elements.single())
    }

    @Test
    fun `element roll with PP shifts the result`() {
        // D10 rolls: 1 + 1 = 2, plus PP=4 → total 6 → "Expected" (range 5-9)
        val rolls = mutableListOf(1, 1)
        val rollFn: (DiceType) -> Int = { rolls.removeFirst() }
        val result = LocationCrafter.rollElement(pp = 4, rollFn = rollFn)
        assertEquals("Expected", result.elements.single())
    }

    @Test
    fun `element roll appends to existing elements`() {
        val rolls = mutableListOf(1, 1)
        val rollFn: (DiceType) -> Int = { rolls.removeFirst() }
        val first = LocationCrafter.rollElement(pp = 0, rollFn = rollFn)
        assertEquals("None", first.elements.single())

        val rolls2 = mutableListOf(10, 10)
        val rollFn2: (DiceType) -> Int = { rolls2.removeFirst() }
        val second = LocationCrafter.rollElement(pp = 0, result = first, rollFn = rollFn2)
        assertEquals(listOf("None", "Known, Climax"), second.elements)
    }

    @Test
    fun `element roll without result creates new LocationResult`() {
        val rolls = mutableListOf(5, 5)
        val rollFn: (DiceType) -> Int = { rolls.removeFirst() }
        val result = LocationCrafter.rollElement(pp = 0, rollFn = rollFn)
        assertNotNull(result)
        assertEquals(1, result.elements.size)
        assertTrue(result.descriptors.isEmpty())
    }

    @Test
    fun `isComplete returns true when last element is Complete`() {
        val result = LocationResult(elements = listOf("Expected", "Random", "Complete"))
        assertTrue(LocationCrafter.isComplete(result))
    }

    @Test
    fun `isComplete returns false when last element is not Complete`() {
        val result = LocationResult(elements = listOf("Expected", "Random"))
        assertFalse(LocationCrafter.isComplete(result))
    }

    @Test
    fun `isComplete returns false for empty elements`() {
        val result = LocationResult()
        assertFalse(LocationCrafter.isComplete(result))
    }

    @Test
    fun `1000 descriptor invocations all produce valid non-empty results`() {
        repeat(1000) {
            val result = LocationCrafter.rollDescriptor()
            assertTrue("Descriptor should be non-empty", result.descriptors.single().isNotBlank())
            assertTrue("Elements should be empty", result.elements.isEmpty())
        }
    }

    @Test
    fun `1000 element invocations all produce valid non-empty results`() {
        repeat(1000) {
            val result = LocationCrafter.rollElement(pp = 0)
            assertTrue("Element should be non-empty", result.elements.single().isNotBlank())
            assertTrue("Descriptors should be empty", result.descriptors.isEmpty())
        }
    }

    @Test
    fun `element roll range edge case completes when 2d10 plus PP ge 22`() {
        // D10 rolls: 10 + 10 = 20, plus PP=2 → total 22 → "Complete"
        val rolls = mutableListOf(10, 10)
        val rollFn: (DiceType) -> Int = { rolls.removeFirst() }
        val result = LocationCrafter.rollElement(pp = 2, rollFn = rollFn)
        assertEquals("Complete", result.elements.single())
        assertTrue(LocationCrafter.isComplete(result))
    }
}
