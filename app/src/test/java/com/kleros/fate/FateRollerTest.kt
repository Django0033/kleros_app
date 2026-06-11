package com.kleros.fate

import com.kleros.dice.DiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FateRollerTest {

    // FIFTY_FIFTY tests
    @Test
    fun `FIFTY_FIFTY with roll 5 returns Yes`() {
        val result = FateRoller.roll(OddsLevel.FIFTY_FIFTY) { 5 }
        assertTrue(result is FateResult.Yes)
        val yes = result as FateResult.Yes
        assertEquals(5, yes.roll)
        assertEquals(OddsLevel.FIFTY_FIFTY, yes.oddsLevel)
        assertFalse(yes.isDouble)
    }

    @Test
    fun `FIFTY_FIFTY with roll 44 returns Yes with double true`() {
        val result = FateRoller.roll(OddsLevel.FIFTY_FIFTY) { 44 }
        assertTrue(result is FateResult.Yes)
        val yes = result as FateResult.Yes
        assertEquals(44, yes.roll)
        assertTrue(yes.isDouble)
    }

    @Test
    fun `FIFTY_FIFTY with roll 43 returns No`() {
        val result = FateRoller.roll(OddsLevel.FIFTY_FIFTY) { 43 }
        assertTrue(result is FateResult.No)
        val no = result as FateResult.No
        assertEquals(43, no.roll)
        assertFalse(no.isDouble)
    }

    // IMPOSSIBLE
    @Test
    fun `IMPOSSIBLE with roll 1 returns ExceptionalNo`() {
        val result = FateRoller.roll(OddsLevel.IMPOSSIBLE) { 1 }
        assertTrue(result is FateResult.ExceptionalNo)
        val exNo = result as FateResult.ExceptionalNo
        assertEquals(1, exNo.roll)
    }

    // CERTAIN
    @Test
    fun `CERTAIN with roll 100 returns ExceptionalYes with double true`() {
        val result = FateRoller.roll(OddsLevel.CERTAIN) { 100 }
        assertTrue(result is FateResult.ExceptionalYes)
        val exYes = result as FateResult.ExceptionalYes
        assertEquals(100, exYes.roll)
        assertTrue(exYes.isDouble)
    }

    // VERY_UNLIKELY
    @Test
    fun `VERY_UNLIKELY with roll 1 returns ExceptionalYes`() {
        val result = FateRoller.roll(OddsLevel.VERY_UNLIKELY) { 1 }
        assertTrue(result is FateResult.ExceptionalYes)
        val exYes = result as FateResult.ExceptionalYes
        assertEquals(1, exYes.roll)
        assertFalse(exYes.isDouble)
    }

    // VERY_LIKELY
    @Test
    fun `VERY_LIKELY with roll 95 returns ExceptionalNo`() {
        val result = FateRoller.roll(OddsLevel.VERY_LIKELY) { 95 }
        assertTrue(result is FateResult.ExceptionalNo)
        val exNo = result as FateResult.ExceptionalNo
        assertEquals(95, exNo.roll)
        assertFalse(exNo.isDouble)
    }

    // LIKELY
    @Test
    fun `LIKELY with roll 66 returns ExceptionalNo because 66 is greater than 65`() {
        val result = FateRoller.roll(OddsLevel.LIKELY) { 66 }
        assertTrue(result is FateResult.ExceptionalNo)
        val exNo = result as FateResult.ExceptionalNo
        assertEquals(66, exNo.roll)
        assertTrue("66 is a double (66 % 11 == 0)", exNo.isDouble)
    }

    // NEARLY_CERTAIN
    @Test
    fun `NEARLY_CERTAIN with roll 18 returns No because 18 is greater than 17 yesMax`() {
        val result = FateRoller.roll(OddsLevel.NEARLY_CERTAIN) { 18 }
        assertTrue(result is FateResult.No)
        val no = result as FateResult.No
        assertEquals(18, no.roll)
        assertFalse(no.isDouble)
    }

    @Test
    fun `NEARLY_CERTAIN with roll 85 returns No`() {
        val result = FateRoller.roll(OddsLevel.NEARLY_CERTAIN) { 85 }
        assertTrue(result is FateResult.No)
        val no = result as FateResult.No
        assertEquals(85, no.roll)
        assertFalse(no.isDouble)
    }

    @Test
    fun `NEARLY_CERTAIN with roll 86 returns ExceptionalNo`() {
        val result = FateRoller.roll(OddsLevel.NEARLY_CERTAIN) { 86 }
        assertTrue(result is FateResult.ExceptionalNo)
        val exNo = result as FateResult.ExceptionalNo
        assertEquals(86, exNo.roll)
        assertFalse(exNo.isDouble)
    }

    // All doubles
    @Test
    fun `rolls 11 22 33 44 55 66 77 88 99 and 100 all have isDouble true`() {
        val doubleRolls = listOf(11, 22, 33, 44, 55, 66, 77, 88, 99, 100)
        doubleRolls.forEach { roll ->
            val result = FateRoller.roll(OddsLevel.FIFTY_FIFTY) { roll }
            assertTrue("Roll $roll should be double", result.isDouble)
        }
    }

    @Test
    fun `non double rolls have isDouble false`() {
        val nonDoubleRolls = listOf(1, 2, 10, 12, 23, 34, 45, 56, 67, 78, 89)
        nonDoubleRolls.forEach { roll ->
            val result = FateRoller.roll(OddsLevel.FIFTY_FIFTY) { roll }
            assertFalse("Roll $roll should NOT be double", result.isDouble)
        }
    }
}
