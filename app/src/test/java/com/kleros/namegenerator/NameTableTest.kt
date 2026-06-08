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
    fun `every row has non-empty syllable1`() {
        NameTable.rows.forEachIndexed { index, row ->
            assertTrue("Row ${index + 1} syllable1 is empty", row.syllable1.isNotBlank())
        }
    }

    @Test
    fun `every row has non-empty syllable2`() {
        NameTable.rows.forEachIndexed { index, row ->
            assertTrue("Row ${index + 1} syllable2 is empty", row.syllable2.isNotBlank())
        }
    }

    @Test
    fun `every row has non-empty suffix`() {
        NameTable.rows.forEachIndexed { index, row ->
            assertTrue("Row ${index + 1} suffix is empty", row.suffix.isNotBlank())
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
