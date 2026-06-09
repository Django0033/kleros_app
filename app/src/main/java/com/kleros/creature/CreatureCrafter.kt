package com.kleros.creature

import com.kleros.character.CharacterData
import com.kleros.dice.DiceRoller
import com.kleros.dice.DiceType
import com.kleros.table.TableEntry

object CreatureCrafter {

    fun generate(
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CreatureResult {
        val desc1 = lookupDescriptor(rollFn(DiceType.D100))
        val desc2 = lookupDescriptor(rollFn(DiceType.D100))
        val desc3 = lookupDescriptor(rollFn(DiceType.D100))
        val ability = lookupAbility(rollFn(DiceType.D100))
        val behavior = lookupInitialBehavior(rollFn(DiceType.D10))
        val statistics = lookupStatistic(rollFn(DiceType.D10))

        return CreatureResult(
            descriptors = listOf(desc1, desc2, desc3),
            abilities = listOf(ability),
            initialBehavior = behavior,
            statistics = statistics,
        )
    }

    fun rollDescriptor(
        result: CreatureResult,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CreatureResult {
        val descriptor = lookupDescriptor(rollFn(DiceType.D100))
        return result.copy(
            descriptors = result.descriptors + descriptor,
        )
    }

    fun rollAbility(
        result: CreatureResult,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CreatureResult {
        val ability = lookupAbility(rollFn(DiceType.D100))
        return result.copy(
            abilities = result.abilities + ability,
        )
    }

    fun rollNewBehavior(
        result: CreatureResult,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CreatureResult {
        if (result.newBehavior != null) return result
        val behavior = lookupNewBehavior(rollFn(DiceType.D10))
        return result.copy(
            newBehavior = behavior,
        )
    }

    private fun lookupDescriptor(roll: Int): String {
        val entry = CreatureData.descriptors[roll - 1] as TableEntry.DIRECT
        return entry.result
    }

    private fun lookupAbility(roll: Int): String {
        val entry = CreatureData.abilities.first { abilityEntry ->
            val range = abilityEntry as TableEntry.RANGE
            roll in range.min..range.max
        }
        return (entry as TableEntry.RANGE).result
    }

    private fun lookupInitialBehavior(roll: Int): String {
        val entry = CreatureData.initialBehavior[roll - 1] as TableEntry.DIRECT
        return entry.result
    }

    private fun lookupNewBehavior(roll: Int): String {
        val entry = CreatureData.newBehavior[roll - 1] as TableEntry.DIRECT
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
