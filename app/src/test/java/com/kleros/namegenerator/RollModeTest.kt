package com.kleros.namegenerator

import org.junit.Assert.assertEquals
import org.junit.Test

class RollModeTest {

    @Test
    fun `NORMAL is first entry`() {
        assertEquals(RollMode.NORMAL, RollMode.entries.first())
    }

    @Test
    fun `all 3 roll modes exist`() {
        assertEquals(3, RollMode.entries.size)
    }

    @Test
    fun `NORMAL label is Normal`() {
        assertEquals("Normal", RollMode.NORMAL.label)
    }

    @Test
    fun `ADVANTAGE label is Advantage`() {
        assertEquals("Advantage", RollMode.ADVANTAGE.label)
    }

    @Test
    fun `DISADVANTAGE label is Disadvantage`() {
        assertEquals("Disadvantage", RollMode.DISADVANTAGE.label)
    }
}
