package com.kleros.table

data class TableHistory(
    val entries: List<TableRollResult.Success> = emptyList(),
    val maxSize: Int = MAX_SIZE,
) {
    companion object {
        const val MAX_SIZE = 10
    }

    fun append(result: TableRollResult.Success): TableHistory {
        val newEntries = listOf(result) + entries
        return copy(entries = newEntries.take(maxSize))
    }
}
