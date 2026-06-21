package com.kleros.mystery

import com.kleros.table.TableEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MysteryDataTest {

    @Test
    fun `discoveryCheck contains 7 entries`() {
        assertEquals(7, MysteryData.discoveryCheck.size)
    }

    @Test
    fun `discoveryCheck covers from 1 to Int MAX_VALUE without gaps`() {
        val ranges: List<TableEntry.RANGE> = MysteryData.discoveryCheck.map { it as TableEntry.RANGE }
        assertEquals(1, ranges.first().min)
        assertEquals(Int.MAX_VALUE, ranges.last().max)
        for (i in 0 until ranges.size - 1) {
            assertEquals(
                "Gap found between entry $i and ${i + 1}",
                ranges[i].max + 1,
                ranges[i + 1].min,
            )
        }
    }

    @Test
    fun `descriptors contains 100 entries`() {
        assertEquals(100, MysteryData.descriptors.size)
    }

    @Test
    fun `descriptors cover indices 1 to 100`() {
        val indices = MysteryData.descriptors.map { (it as TableEntry.DIRECT).index }
        assertEquals(100, indices.size)
        assertEquals((1..100).toList(), indices.sorted())
    }

    @Test
    fun `each descriptor has non-empty result`() {
        MysteryData.descriptors.forEach { entry ->
            val direct = entry as TableEntry.DIRECT
            assertTrue(
                "Descriptor at index ${direct.index} should not be empty",
                direct.result.isNotBlank(),
            )
        }
    }

    @Test
    fun `each discovery entry has non-empty result`() {
        MysteryData.discoveryCheck.forEach { entry ->
            val range = entry as TableEntry.RANGE
            assertTrue(
                "Discovery entry range ${range.min}-${range.max} should not be empty",
                range.result.isNotBlank(),
            )
        }
    }
}
