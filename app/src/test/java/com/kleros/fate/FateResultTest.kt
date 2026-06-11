package com.kleros.fate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FateResultTest {

    @Test
    fun `ExceptionalYes stores correct data`() {
        val result = FateResult.ExceptionalYes(roll = 1, oddsLevel = OddsLevel.CERTAIN, isDouble = false)
        assertEquals(1, result.roll)
        assertEquals(OddsLevel.CERTAIN, result.oddsLevel)
        assertFalse(result.isDouble)
    }

    @Test
    fun `Yes stores correct data`() {
        val result = FateResult.Yes(roll = 10, oddsLevel = OddsLevel.FIFTY_FIFTY, isDouble = false)
        assertEquals(10, result.roll)
        assertEquals(OddsLevel.FIFTY_FIFTY, result.oddsLevel)
        assertFalse(result.isDouble)
    }

    @Test
    fun `No stores correct data`() {
        val result = FateResult.No(roll = 50, oddsLevel = OddsLevel.FIFTY_FIFTY, isDouble = false)
        assertEquals(50, result.roll)
        assertEquals(OddsLevel.FIFTY_FIFTY, result.oddsLevel)
        assertFalse(result.isDouble)
    }

    @Test
    fun `ExceptionalNo stores correct data`() {
        val result = FateResult.ExceptionalNo(roll = 99, oddsLevel = OddsLevel.IMPOSSIBLE, isDouble = false)
        assertEquals(99, result.roll)
        assertEquals(OddsLevel.IMPOSSIBLE, result.oddsLevel)
        assertFalse(result.isDouble)
    }

    @Test
    fun `ExceptionalYes with isDouble true`() {
        val result = FateResult.ExceptionalYes(roll = 11, oddsLevel = OddsLevel.CERTAIN, isDouble = true)
        assertEquals(11, result.roll)
        assertTrue(result.isDouble)
    }

    @Test
    fun `all sealed variants are non-null when created`() {
        val exYes = FateResult.ExceptionalYes(roll = 1, oddsLevel = OddsLevel.CERTAIN, isDouble = false)
        val yes = FateResult.Yes(roll = 5, oddsLevel = OddsLevel.FIFTY_FIFTY, isDouble = false)
        val no = FateResult.No(roll = 30, oddsLevel = OddsLevel.FIFTY_FIFTY, isDouble = false)
        val exNo = FateResult.ExceptionalNo(roll = 95, oddsLevel = OddsLevel.IMPOSSIBLE, isDouble = false)

        assertNotNull(exYes)
        assertNotNull(yes)
        assertNotNull(no)
        assertNotNull(exNo)
    }
}
