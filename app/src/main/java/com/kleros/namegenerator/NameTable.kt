@file:Suppress("MagicNumber")

package com.kleros.namegenerator

data class NameTableRow(
    val pattern: String,
    val inicio1: String,
    val inicio2: String,
    val ending: String,
)

object NameTable {
    val rows: List<NameTableRow> = listOf(
        NameTableRow("12o", "(f)a", "hal", "an"),
        NameTableRow("12", "(p)e", "ris", "ar"),
        NameTableRow("12", "(v)i", "del", "er"),
        NameTableRow("23-o", "(n)o", "mor", "ian"),
        NameTableRow("23-", "(s)u", "bar", "ic"),
        NameTableRow("23-", "de", "net", "in"),
        NameTableRow("123-o", "ka", "kel", "o"),
        NameTableRow("123-", "li", "lim", "on"),
        NameTableRow("123-", "ma", "tur", "or"),
        NameTableRow("111", "ro", "pen", "us"),
        NameTableRow("111", "be", "rond", "a"),
        NameTableRow("123", "da", "kay", "aea"),
        NameTableRow("12a", "ki", "jam", "aya"),
        NameTableRow("12i", "le", "vash", "elle"),
        NameTableRow("23-a", "mi", "zab", "ene"),
        NameTableRow("23-i", "ne", "yos", "ess"),
        NameTableRow("23+", "ru", "gran", "ette"),
        NameTableRow("123-a", "si", "ched", "ice"),
        NameTableRow("123-i", "ta", "sark", "id"),
        NameTableRow("123+", "to", "kic", "osa"),
    )

    fun row(index: Int): NameTableRow = rows[index - 1]
}
