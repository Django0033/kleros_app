package com.kleros.character

import com.kleros.table.TableEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterDataTest {

    @Test
    fun `descriptors contains 100 entries`() {
        assertEquals(100, CharacterData.descriptors.size)
    }

    @Test
    fun `each descriptor has unique index 1 to 100`() {
        val indices = CharacterData.descriptors.map { (it as TableEntry.DIRECT).index }
        assertEquals(100, indices.size)
        assertEquals((1..100).toList(), indices.sorted())
    }

    @Test
    fun `each descriptor has non-empty result`() {
        CharacterData.descriptors.forEach { entry ->
            val direct = entry as TableEntry.DIRECT
            assertTrue(
                "Descriptor at index ${direct.index} should not be empty",
                direct.result.isNotBlank(),
            )
        }
    }

    @Test
    fun `statistics contains 5 entries`() {
        assertEquals(5, CharacterData.statistics.size)
    }

    @Test
    fun `statistics ranges cover 1 to 10 without gaps`() {
        val ranges = CharacterData.statistics.map { it as TableEntry.RANGE }
        val covered = mutableSetOf<Int>()
        ranges.forEach { range ->
            for (value in range.min..range.max) covered.add(value)
        }
        assertEquals((1..10).toSet(), covered)
    }

    @Test
    fun `statistics entries have non-empty result`() {
        CharacterData.statistics.forEach { entry ->
            val result = (entry as TableEntry.RANGE).result
            assertTrue("Statistic should not be empty", result.isNotBlank())
        }
    }
}
