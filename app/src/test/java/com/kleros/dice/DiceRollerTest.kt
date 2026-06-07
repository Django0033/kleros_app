package com.kleros.dice

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class DiceRollerTest {

    @Test
    fun `D4 rolls produce values in 1 to 4`() {
        repeat(1000) {
            val result = DiceRoller.roll(DiceType.D4)
            assertTrue("Expected 1..4 but got $result", result in 1..4)
        }
    }

    @Test
    fun `D6 rolls produce values in 1 to 6`() {
        repeat(1000) {
            val result = DiceRoller.roll(DiceType.D6)
            assertTrue("Expected 1..6 but got $result", result in 1..6)
        }
    }

    @Test
    fun `D8 rolls produce values in 1 to 8`() {
        repeat(1000) {
            val result = DiceRoller.roll(DiceType.D8)
            assertTrue("Expected 1..8 but got $result", result in 1..8)
        }
    }

    @Test
    fun `D10 rolls produce values in 1 to 10`() {
        repeat(1000) {
            val result = DiceRoller.roll(DiceType.D10)
            assertTrue("Expected 1..10 but got $result", result in 1..10)
        }
    }

    @Test
    fun `D12 rolls produce values in 1 to 12`() {
        repeat(1000) {
            val result = DiceRoller.roll(DiceType.D12)
            assertTrue("Expected 1..12 but got $result", result in 1..12)
        }
    }

    @Test
    fun `D20 rolls produce values in 1 to 20`() {
        repeat(1000) {
            val result = DiceRoller.roll(DiceType.D20)
            assertTrue("Expected 1..20 but got $result", result in 1..20)
        }
    }

    @Test
    fun `D100 rolls produce values in 1 to 100`() {
        repeat(1000) {
            val result = DiceRoller.roll(DiceType.D100)
            assertTrue("Expected 1..100 but got $result", result in 1..100)
        }
    }

    @Test
    fun `D4 distribution covers all 4 values over 1000 samples`() {
        val seen = mutableSetOf<Int>()
        repeat(1000) {
            val result = DiceRoller.roll(DiceType.D4)
            assertTrue("Expected 1..4 but got $result", result in 1..4)
            seen += result
        }
        assertTrue(
            "Expected all 4 values to appear but got ${seen.sorted()}",
            seen.size == 4,
        )
    }

    @Test
    fun `D100 distribution covers at least 95 distinct values over 1000 samples`() {
        val seen = mutableSetOf<Int>()
        repeat(1000) {
            val result = DiceRoller.roll(DiceType.D100)
            assertTrue("Expected 1..100 but got $result", result in 1..100)
            seen += result
        }
        // With 1000 samples and 100 faces, we expect most values to appear.
        // Use a threshold to avoid flakiness.
        assertTrue(
            "Expected at least 95 distinct values but got ${seen.size}",
            seen.size >= 95,
        )
    }

    @Test(timeout = 100)
    fun `7000 invocations complete in under 100ms`() {
        val types = DiceType.entries
        repeat(1000) { i ->
            types.forEach { type ->
                DiceRoller.roll(type)
            }
        }
    }
}
