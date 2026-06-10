@file:Suppress("MagicNumber")

package com.kleros.adventure

import com.kleros.table.TableEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdventureDataTest {

    @Test
    fun `action table has 100 entries`() {
        assertEquals(100, AdventureData.action.entries.size)
    }

    @Test
    fun `tension table has 100 entries`() {
        assertEquals(100, AdventureData.tension.entries.size)
    }

    @Test
    fun `mystery table has 100 entries`() {
        assertEquals(100, AdventureData.mystery.entries.size)
    }

    @Test
    fun `social table has 100 entries`() {
        assertEquals(100, AdventureData.social.entries.size)
    }

    @Test
    fun `personal table has 100 entries`() {
        assertEquals(100, AdventureData.personal.entries.size)
    }

    @Test
    fun `all 5 tables are present`() {
        assertEquals(5, AdventureData.tables.size)
    }

    @Test
    fun `action entries cover indices 1 to 100 sequentially`() {
        val entries = AdventureData.action.entries.map { it as TableEntry.DIRECT }
        assertEquals(100, entries.size)
        for (i in entries.indices) {
            assertEquals(i + 1, entries[i].index)
        }
    }

    @Test
    fun `tension entries cover indices 1 to 100 sequentially`() {
        val entries = AdventureData.tension.entries.map { it as TableEntry.DIRECT }
        assertEquals(100, entries.size)
        for (i in entries.indices) {
            assertEquals(i + 1, entries[i].index)
        }
    }

    @Test
    fun `mystery entries cover indices 1 to 100 sequentially`() {
        val entries = AdventureData.mystery.entries.map { it as TableEntry.DIRECT }
        assertEquals(100, entries.size)
        for (i in entries.indices) {
            assertEquals(i + 1, entries[i].index)
        }
    }

    @Test
    fun `social entries cover indices 1 to 100 sequentially`() {
        val entries = AdventureData.social.entries.map { it as TableEntry.DIRECT }
        assertEquals(100, entries.size)
        for (i in entries.indices) {
            assertEquals(i + 1, entries[i].index)
        }
    }

    @Test
    fun `personal entries cover indices 1 to 100 sequentially`() {
        val entries = AdventureData.personal.entries.map { it as TableEntry.DIRECT }
        assertEquals(100, entries.size)
        for (i in entries.indices) {
            assertEquals(i + 1, entries[i].index)
        }
    }

    @Test
    fun `all entries are DIRECT type`() {
        for (table in AdventureData.tables) {
            for (entry in table.entries) {
                assertTrue("Entry in '${table.name}' should be DIRECT", entry is TableEntry.DIRECT)
            }
        }
    }

    @Test
    fun `first action entry is index 1`() {
        val first = AdventureData.action.entries.first() as TableEntry.DIRECT
        assertEquals(1, first.index)
    }

    @Test
    fun `last action entry is index 100`() {
        val last = AdventureData.action.entries.last() as TableEntry.DIRECT
        assertEquals(100, last.index)
    }
}
