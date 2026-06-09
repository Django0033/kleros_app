package com.kleros.creature

import com.kleros.table.TableEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatureDataTest {

    @Test
    fun `descriptors contains 100 entries`() {
        assertEquals(100, CreatureData.descriptors.size)
    }

    @Test
    fun `each descriptor has unique index 1 to 100`() {
        val indices = CreatureData.descriptors.map { (it as TableEntry.DIRECT).index }
        assertEquals(100, indices.size)
        assertEquals((1..100).toList(), indices.sorted())
    }

    @Test
    fun `each descriptor has non-empty result`() {
        CreatureData.descriptors.forEach { entry ->
            val direct = entry as TableEntry.DIRECT
            assertTrue(
                "Descriptor at index ${direct.index} should not be empty",
                direct.result.isNotBlank(),
            )
        }
    }

    @Test
    fun `abilities contains 50 entries`() {
        assertEquals(50, CreatureData.abilities.size)
    }

    @Test
    fun `abilities ranges cover 1 to 100 without gaps`() {
        val ranges = CreatureData.abilities.map { it as TableEntry.RANGE }
        val covered = mutableSetOf<Int>()
        ranges.forEach { range ->
            for (value in range.min..range.max) covered.add(value)
        }
        assertEquals((1..100).toSet(), covered)
    }

    @Test
    fun `each ability has non-empty result`() {
        CreatureData.abilities.forEach { entry ->
            val result = (entry as TableEntry.RANGE).result
            assertTrue("Ability should not be empty", result.isNotBlank())
        }
    }

    @Test
    fun `initial behavior contains 10 entries`() {
        assertEquals(10, CreatureData.initialBehavior.size)
    }

    @Test
    fun `initial behavior has unique index 1 to 10`() {
        val indices = CreatureData.initialBehavior.map { (it as TableEntry.DIRECT).index }
        assertEquals(10, indices.size)
        assertEquals((1..10).toList(), indices.sorted())
    }

    @Test
    fun `initial behavior entries have non-empty result`() {
        CreatureData.initialBehavior.forEach { entry ->
            val direct = entry as TableEntry.DIRECT
            assertTrue(
                "Initial behavior at index ${direct.index} should not be empty",
                direct.result.isNotBlank(),
            )
        }
    }

    @Test
    fun `new behavior contains 10 entries`() {
        assertEquals(10, CreatureData.newBehavior.size)
    }

    @Test
    fun `new behavior has unique index 1 to 10`() {
        val indices = CreatureData.newBehavior.map { (it as TableEntry.DIRECT).index }
        assertEquals(10, indices.size)
        assertEquals((1..10).toList(), indices.sorted())
    }

    @Test
    fun `new behavior entries have non-empty result`() {
        CreatureData.newBehavior.forEach { entry ->
            val direct = entry as TableEntry.DIRECT
            assertTrue(
                "New behavior at index ${direct.index} should not be empty",
                direct.result.isNotBlank(),
            )
        }
    }
}
