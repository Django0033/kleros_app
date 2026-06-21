package com.kleros.mystery

import com.kleros.dice.DiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MysteryCrafterTest {

    @Test
    fun `0 boxes and roll 10 yields Nothing useful found with null descriptor`() {
        val rollFn: (DiceType) -> Int = { 10 }
        val result = MysteryCrafter.check(boxes = 0, rollFn = rollFn)
        assertEquals("Nothing useful found", result.discoveryResult)
        assertNull(result.descriptor)
        assertEquals(false, result.isDefinitive)
    }

    @Test
    fun `0 boxes and roll 20 yields New Suspect not connected with descriptor`() {
        val rollFn: (DiceType) -> Int = { 20 }
        val result = MysteryCrafter.check(boxes = 0, rollFn = rollFn)
        assertEquals("New Suspect not connected", result.discoveryResult)
        assertNotNull(result.descriptor)
        assertEquals(false, result.isDefinitive)
    }

    @Test
    fun `50 boxes and roll 60 yields definitive clue`() {
        val rollFn: (DiceType) -> Int = { 60 }
        val result = MysteryCrafter.check(boxes = 50, rollFn = rollFn)
        assertTrue(result.isDefinitive)
        assertTrue(result.discoveryResult.startsWith("Definitive clue"))
        assertNotNull(result.descriptor)
    }

    @Test
    fun `rollDescriptor returns a word from the table`() {
        val knownWords = MysteryData.descriptors.map { it.result }.toSet()
        val rollFn: (DiceType) -> Int = { 10 }
        val descriptor = MysteryCrafter.rollDescriptor(rollFn = rollFn)
        assertTrue("Descriptor should be in the table", descriptor in knownWords)
        assertEquals("Clothing", descriptor)
    }

    @Test
    fun `rollDescriptor with roll 100 returns Witness`() {
        val rollFn: (DiceType) -> Int = { 100 }
        assertEquals("Witness", MysteryCrafter.rollDescriptor(rollFn = rollFn))
    }

    @Test
    fun `rollDescriptor with roll 1 returns Accident`() {
        val rollFn: (DiceType) -> Int = { 1 }
        assertEquals("Accident", MysteryCrafter.rollDescriptor(rollFn = rollFn))
    }

    @Test
    fun `1000 check invocations all produce valid results`() {
        repeat(1000) {
            val result = MysteryCrafter.check(boxes = 0)
            assertTrue("discoveryResult should be non-empty", result.discoveryResult.isNotBlank())
            assertEquals(0, result.boxes)
        }
    }

    @Test
    fun `1000 rollDescriptor invocations all produce valid non-empty results`() {
        repeat(1000) {
            val descriptor = MysteryCrafter.rollDescriptor()
            assertTrue("Descriptor should be non-empty", descriptor.isNotBlank())
        }
    }

    @Test
    fun `boxes increments discovered result`() {
        // With 1 box and roll=14: total=15 -> still "Nothing useful found"
        val rollFn: (DiceType) -> Int = { 14 }
        val result = MysteryCrafter.check(boxes = 1, rollFn = rollFn)
        assertEquals("Nothing useful found", result.discoveryResult)
    }

    @Test
    fun `boxes pushes result into new territory`() {
        // With 2 boxes and roll=14: total=16 -> "New Suspect not connected"
        val rollFn: (DiceType) -> Int = { 14 }
        val result = MysteryCrafter.check(boxes = 2, rollFn = rollFn)
        assertEquals("New Suspect not connected", result.discoveryResult)
        assertNotNull(result.descriptor)
    }

    @Test
    fun `check stores boxes value in result`() {
        val result = MysteryCrafter.check(boxes = 7)
        assertEquals(7, result.boxes)
    }
}
