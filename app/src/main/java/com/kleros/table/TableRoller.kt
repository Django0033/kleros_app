package com.kleros.table

import com.kleros.dice.DiceRoller
import com.kleros.dice.DiceType

object TableRoller {

    fun roll(
        table: TableDef,
        rollFn: () -> Int = { DiceRoller.roll(DiceType.D20) },
    ): TableRollResult {
        val roll = rollFn()

        val entry = table.entries.firstOrNull { e ->
            when (e) {
                is TableEntry.RANGE -> roll in e.min..e.max
                is TableEntry.DIRECT -> roll == e.index
                is TableEntry.RANGE_MODIFIER -> roll in e.min..e.max
            }
        }

        return if (entry != null) {
            TableRollResult.Success(
                when (entry) {
                    is TableEntry.RANGE -> entry.result
                    is TableEntry.DIRECT -> entry.result
                    is TableEntry.RANGE_MODIFIER -> entry.result
                },
            )
        } else {
            TableRollResult.Error("No entry matches roll $roll")
        }
    }
}
