package com.kleros.namegenerator

import com.kleros.dice.DiceRoller
import com.kleros.dice.DiceType

object NameGenerator {

    private const val HALF_OFFSET = 10

    fun generate(
        rollMode: RollMode = RollMode.NORMAL,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): NameResult {
        val patternRoll = when (rollMode) {
            RollMode.NORMAL -> rollFn(DiceType.D20)
            RollMode.ADVANTAGE -> maxOf(rollFn(DiceType.D20), rollFn(DiceType.D20))
            RollMode.DISADVANTAGE -> minOf(rollFn(DiceType.D20), rollFn(DiceType.D20))
        }
        val row = NameTable.row(patternRoll)
        val operations = parsePattern(row.pattern)

        val syllables = mutableListOf<String>()
        var isFirst = true
        for (op in operations) {
            when (op) {
                is Operation.SyllableColumn -> {
                    val roll = rollFn(DiceType.D20)
                    val rowIndex = mapRollToRow(roll, op.range)
                    val cellValue = getCellValue(rowIndex, op.column)
                    syllables.add(resolveParenthetical(cellValue, isFirst))
                    isFirst = false
                }
                is Operation.LiteralSuffix -> syllables.add(op.text)
            }
        }

        return NameResult(name = syllables.joinToString(""), rollMode = rollMode)
    }

    private sealed interface Operation {
        data class SyllableColumn(val column: Column, val range: Range) : Operation
        data class LiteralSuffix(val text: String) : Operation
    }

    private enum class Column { INICIO1, INICIO2, ENDING }

    private enum class Range { ALL, FIRST_HALF, LAST_HALF }

    private fun parsePattern(pattern: String): List<Operation> {
        val operations = mutableListOf<Operation>()
        var i = 0
        while (i < pattern.length) {
            i = when (pattern[i]) {
                '1' -> {
                    operations.add(Operation.SyllableColumn(Column.INICIO1, Range.ALL))
                    i + 1
                }
                '2' -> {
                    operations.add(Operation.SyllableColumn(Column.INICIO2, Range.ALL))
                    i + 1
                }
                '3' -> parseDigitThree(pattern, i, operations)
                else -> parseLiteralLetters(pattern, i, operations)
            }
        }
        return operations
    }

    private fun parseDigitThree(
        pattern: String,
        index: Int,
        operations: MutableList<Operation>,
    ): Int {
        if (index + 1 < pattern.length) {
            return when (pattern[index + 1]) {
                '-' -> {
                    operations.add(Operation.SyllableColumn(Column.ENDING, Range.FIRST_HALF))
                    index + 2
                }
                '+' -> {
                    operations.add(Operation.SyllableColumn(Column.ENDING, Range.LAST_HALF))
                    index + 2
                }
                else -> {
                    operations.add(Operation.SyllableColumn(Column.ENDING, Range.ALL))
                    index + 1
                }
            }
        }
        operations.add(Operation.SyllableColumn(Column.ENDING, Range.ALL))
        return index + 1
    }

    private fun parseLiteralLetters(
        pattern: String,
        index: Int,
        operations: MutableList<Operation>,
    ): Int {
        var i = index
        val suffix = StringBuilder()
        while (i < pattern.length && pattern[i].isLetter()) {
            suffix.append(pattern[i])
            i++
        }
        if (suffix.isNotEmpty()) {
            operations.add(Operation.LiteralSuffix(suffix.toString()))
        } else {
            i++ // skip unknown char
        }
        return i
    }

    private fun mapRollToRow(roll: Int, range: Range): Int = when (range) {
        Range.ALL -> roll
        Range.FIRST_HALF -> (roll + 1) / 2
        Range.LAST_HALF -> (roll + 1) / 2 + HALF_OFFSET
    }

    private fun getCellValue(rowIndex: Int, column: Column): String {
        val row = NameTable.row(rowIndex)
        return when (column) {
            Column.INICIO1 -> row.inicio1
            Column.INICIO2 -> row.inicio2
            Column.ENDING -> row.ending
        }
    }

    private fun resolveParenthetical(value: String, isFirst: Boolean): String {
        val openParen = value.indexOf('(')
        val closeParen = value.indexOf(')')
        if (openParen == -1 || closeParen == -1 || closeParen <= openParen) {
            return value
        }
        val prefix = value.substring(openParen + 1, closeParen)
        val base = value.substring(closeParen + 1)
        return if (isFirst) base else "$prefix$base"
    }
}
