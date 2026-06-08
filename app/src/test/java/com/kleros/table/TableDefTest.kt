package com.kleros.table

import org.junit.Assert.assertEquals
import org.junit.Test

class TableDefTest {

    @Test
    fun `TableDef groups name and entries`() {
        val entries = listOf<TableEntry>(
            TableEntry.RANGE(min = 1, max = 5, result = "A"),
            TableEntry.DIRECT(index = 1, result = "X"),
        )
        val tableDef = TableDef(name = "Test Table", entries = entries)

        assertEquals("Test Table", tableDef.name)
        assertEquals(2, tableDef.entries.size)
        assertEquals("A", (tableDef.entries[0] as TableEntry.RANGE).result)
        assertEquals("X", (tableDef.entries[1] as TableEntry.DIRECT).result)
    }
}
