package com.kleros.dice

import org.junit.Assert.assertEquals
import org.junit.Test

class DiceTypeTest {

    @Test
    fun `all 7 dice types exist`() {
        assertEquals(7, DiceType.entries.size)
    }

    @Test
    fun `D4 has 4 faces`() {
        assertEquals(4, DiceType.D4.faces)
    }

    @Test
    fun `D6 has 6 faces`() {
        assertEquals(6, DiceType.D6.faces)
    }

    @Test
    fun `D8 has 8 faces`() {
        assertEquals(8, DiceType.D8.faces)
    }

    @Test
    fun `D10 has 10 faces`() {
        assertEquals(10, DiceType.D10.faces)
    }

    @Test
    fun `D12 has 12 faces`() {
        assertEquals(12, DiceType.D12.faces)
    }

    @Test
    fun `D20 has 20 faces`() {
        assertEquals(20, DiceType.D20.faces)
    }

    @Test
    fun `D100 has 100 faces`() {
        assertEquals(100, DiceType.D100.faces)
    }

    @Test
    fun `name returns the enum constant name`() {
        assertEquals("D4", DiceType.D4.name)
        assertEquals("D6", DiceType.D6.name)
        assertEquals("D8", DiceType.D8.name)
        assertEquals("D10", DiceType.D10.name)
        assertEquals("D12", DiceType.D12.name)
        assertEquals("D20", DiceType.D20.name)
        assertEquals("D100", DiceType.D100.name)
    }
}
