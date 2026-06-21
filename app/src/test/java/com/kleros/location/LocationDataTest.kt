package com.kleros.location

import com.kleros.table.TableEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationDataTest {

    @Test
    fun `descriptors contains 100 entries`() {
        assertEquals(100, LocationData.descriptors.size)
    }

    @Test
    fun `each descriptor has unique index 1 to 100`() {
        val indices = LocationData.descriptors.map { (it as TableEntry.DIRECT).index }
        assertEquals(100, indices.size)
        assertEquals((1..100).toList(), indices.sorted())
    }

    @Test
    fun `each descriptor has non-empty result`() {
        LocationData.descriptors.forEach { entry ->
            val direct = entry as TableEntry.DIRECT
            assertTrue(
                "Descriptor at index ${direct.index} should not be empty",
                direct.result.isNotBlank(),
            )
        }
    }

    @Test
    fun `elements contains 10 entries`() {
        assertEquals(10, LocationData.elements.size)
    }

    @Test
    fun `elements ranges cover from at least 2 to at least 22`() {
        val ranges: List<TableEntry.RANGE> = LocationData.elements.map { it as TableEntry.RANGE }
        val minMin = ranges.minOf { range: TableEntry.RANGE -> range.min }
        val maxMax = ranges.maxOf { range: TableEntry.RANGE -> range.max }
        assertTrue("Minimum range start should be <= 2, got $minMin", minMin <= 2)
        assertTrue("Maximum range end should be >= 22, got $maxMax", maxMax >= 22)
    }

    @Test
    fun `elements entries have non-empty result`() {
        LocationData.elements.forEach { entry ->
            val result = (entry as TableEntry.RANGE).result
            assertTrue("Element should not be empty", result.isNotBlank())
        }
    }

    @Test
    fun `RegionSize has 3 values`() {
        assertEquals(3, RegionSize.entries.size)
    }

    @Test
    fun `RegionSize SMALL has startingPP of 3`() {
        assertEquals(3, RegionSize.SMALL.startingPP)
    }

    @Test
    fun `RegionSize AVERAGE has startingPP of 0`() {
        assertEquals(0, RegionSize.AVERAGE.startingPP)
    }

    @Test
    fun `RegionSize LARGE has startingPP of -3`() {
        assertEquals(-3, RegionSize.LARGE.startingPP)
    }
}
