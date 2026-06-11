package com.kleros.fate

import org.junit.Assert.assertEquals
import org.junit.Test

class FateDataTest {

    @Test
    fun `all 9 odds levels exist`() {
        assertEquals(9, OddsLevel.entries.size)
    }

    @Test
    fun `IMPOSSIBLE has correct thresholds`() {
        assertEquals(0, OddsLevel.IMPOSSIBLE.exYesMax)
        assertEquals(0, OddsLevel.IMPOSSIBLE.yesMax)
        assertEquals(0, OddsLevel.IMPOSSIBLE.noMax)
    }

    @Test
    fun `NEARLY_IMPOSSIBLE has correct thresholds`() {
        assertEquals(0, OddsLevel.NEARLY_IMPOSSIBLE.exYesMax)
        assertEquals(3, OddsLevel.NEARLY_IMPOSSIBLE.yesMax)
        assertEquals(15, OddsLevel.NEARLY_IMPOSSIBLE.noMax)
    }

    @Test
    fun `VERY_UNLIKELY has correct thresholds`() {
        assertEquals(0, OddsLevel.VERY_UNLIKELY.exYesMax)
        assertEquals(5, OddsLevel.VERY_UNLIKELY.yesMax)
        assertEquals(25, OddsLevel.VERY_UNLIKELY.noMax)
    }

    @Test
    fun `UNLIKELY has correct thresholds`() {
        assertEquals(0, OddsLevel.UNLIKELY.exYesMax)
        assertEquals(7, OddsLevel.UNLIKELY.yesMax)
        assertEquals(35, OddsLevel.UNLIKELY.noMax)
    }

    @Test
    fun `FIFTY_FIFTY has correct thresholds`() {
        assertEquals(0, OddsLevel.FIFTY_FIFTY.exYesMax)
        assertEquals(10, OddsLevel.FIFTY_FIFTY.yesMax)
        assertEquals(50, OddsLevel.FIFTY_FIFTY.noMax)
    }

    @Test
    fun `LIKELY has correct thresholds`() {
        assertEquals(0, OddsLevel.LIKELY.exYesMax)
        assertEquals(13, OddsLevel.LIKELY.yesMax)
        assertEquals(65, OddsLevel.LIKELY.noMax)
    }

    @Test
    fun `VERY_LIKELY has correct thresholds`() {
        assertEquals(0, OddsLevel.VERY_LIKELY.exYesMax)
        assertEquals(15, OddsLevel.VERY_LIKELY.yesMax)
        assertEquals(75, OddsLevel.VERY_LIKELY.noMax)
    }

    @Test
    fun `NEARLY_CERTAIN has correct thresholds`() {
        assertEquals(0, OddsLevel.NEARLY_CERTAIN.exYesMax)
        assertEquals(17, OddsLevel.NEARLY_CERTAIN.yesMax)
        assertEquals(85, OddsLevel.NEARLY_CERTAIN.noMax)
    }

    @Test
    fun `CERTAIN has correct thresholds`() {
        assertEquals(18, OddsLevel.CERTAIN.exYesMax)
        assertEquals(18, OddsLevel.CERTAIN.yesMax)
        assertEquals(90, OddsLevel.CERTAIN.noMax)
    }

    @Test
    fun `entries returns levels in order`() {
        val expected = listOf(
            OddsLevel.IMPOSSIBLE,
            OddsLevel.NEARLY_IMPOSSIBLE,
            OddsLevel.VERY_UNLIKELY,
            OddsLevel.UNLIKELY,
            OddsLevel.FIFTY_FIFTY,
            OddsLevel.LIKELY,
            OddsLevel.VERY_LIKELY,
            OddsLevel.NEARLY_CERTAIN,
            OddsLevel.CERTAIN,
        )
        assertEquals(expected, OddsLevel.entries)
    }

    @Test
    fun `each odds level has non-empty label`() {
        OddsLevel.entries.forEach { level ->
            assertEquals(true, level.label.isNotBlank())
        }
    }
}
