package com.kleros.table

import org.junit.Assert.assertEquals
import org.junit.Test

class TableRollResultTest {

    @Test
    fun `Success stores value correctly`() {
        val result: TableRollResult = TableRollResult.Success("A")
        assertEquals("A", (result as TableRollResult.Success).value)
    }

    @Test
    fun `Error stores message correctly`() {
        val result: TableRollResult = TableRollResult.Error("No match")
        assertEquals("No match", (result as TableRollResult.Error).message)
    }

    @Test
    fun `Success is a TableRollResult subtype`() {
        val result: TableRollResult = TableRollResult.Success("B")
        assert(result is TableRollResult.Success)
    }

    @Test
    fun `Error is a TableRollResult subtype`() {
        val result: TableRollResult = TableRollResult.Error("fail")
        assert(result is TableRollResult.Error)
    }
}
