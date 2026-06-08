@file:Suppress("MagicNumber")

package com.kleros.table

sealed class TableEntry {

    data class RANGE(
        val min: Int,
        val max: Int,
        val result: String,
    ) : TableEntry()

    data class DIRECT(
        val index: Int,
        val result: String,
    ) : TableEntry()

    data class RANGE_MODIFIER(
        val min: Int,
        val max: Int,
        val result: String,
        val modifier: Int,
    ) : TableEntry()
}
