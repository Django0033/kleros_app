package com.kleros.namegenerator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NameTableTest {

    @Test
    fun `table has exactly 20 rows`() {
        assertEquals(20, NameTable.rows.size)
    }

    @Test
    fun `every row has non-empty pattern`() {
        NameTable.rows.forEachIndexed { index, row ->
            assertTrue("Row ${index + 1} pattern is empty", row.pattern.isNotBlank())
        }
    }

    @Test
    fun `every row has non-empty inicio1`() {
        NameTable.rows.forEachIndexed { index, row ->
            assertTrue("Row ${index + 1} inicio1 is empty", row.inicio1.isNotBlank())
        }
    }

    @Test
    fun `every row has non-empty inicio2`() {
        NameTable.rows.forEachIndexed { index, row ->
            assertTrue("Row ${index + 1} inicio2 is empty", row.inicio2.isNotBlank())
        }
    }

    @Test
    fun `every row has non-empty ending`() {
        NameTable.rows.forEachIndexed { index, row ->
            assertTrue("Row ${index + 1} ending is empty", row.ending.isNotBlank())
        }
    }

    @Test
    fun `row 1 returns first row`() {
        val row = NameTable.row(1)
        assertNotNull(row)
        assertEquals("12o", row.pattern)
    }

    @Test
    fun `row 20 returns last row`() {
        val row = NameTable.row(20)
        assertNotNull(row)
        assertEquals("123+", row.pattern)
    }

    @Test
    fun `all row indices 1 through 20 are accessible`() {
        for (i in 1..20) {
            val row = NameTable.row(i)
            assertNotNull("Row $i should not be null", row)
        }
    }
}
