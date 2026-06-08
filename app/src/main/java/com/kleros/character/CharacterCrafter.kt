package com.kleros.character

import com.kleros.dice.DiceRoller
import com.kleros.dice.DiceType
import com.kleros.table.TableEntry

object CharacterCrafter {

    fun generate(
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CharacterResult {
        val identity = lookupDescriptor(rollFn(DiceType.D100))
        val mind = lookupDescriptor(rollFn(DiceType.D100))
        val body = lookupDescriptor(rollFn(DiceType.D100))
        val talent = lookupDescriptor(rollFn(DiceType.D100))
        val statistics = lookupStatistic(rollFn(DiceType.D10))

        return CharacterResult(
            identity = identity,
            mind = mind,
            body = body,
            talent = talent,
            statistics = statistics,
        )
    }

    private fun lookupDescriptor(roll: Int): String {
        val entry = CharacterData.descriptors[roll - 1] as TableEntry.DIRECT
        return entry.result
    }

    private fun lookupStatistic(roll: Int): String {
        val entry = CharacterData.statistics.first { statEntry ->
            val range = statEntry as TableEntry.RANGE
            roll in range.min..range.max
        }
        return (entry as TableEntry.RANGE).result
    }
}
