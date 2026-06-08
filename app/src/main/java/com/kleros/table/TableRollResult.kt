package com.kleros.table

sealed class TableRollResult {

    data class Success(
        val value: String,
    ) : TableRollResult()

    data class Error(
        val message: String,
    ) : TableRollResult()
}
