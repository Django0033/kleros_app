@file:Suppress("TooManyFunctions")

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
        val ability1 = lookupAbility(rollFn(DiceType.D100))
        val ability2 = lookupAbility(rollFn(DiceType.D100))
        val behavior = lookupInitialBehavior(rollFn(DiceType.D10))
        val statistics = lookupStatistic(rollFn(DiceType.D10))

        return CreatureResult(
            descriptors = listOf(desc1, desc2),
            abilities = listOf(ability1, ability2),
            initialBehavior = behavior,
            statistics = statistics,
        )
    }

    fun rollDescriptor(
        result: CreatureResult? = null,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CreatureResult {
        val base = result ?: CreatureResult(
            descriptors = emptyList(),
            abilities = emptyList(),
            initialBehavior = "",
            statistics = "",
        )
        val descriptor = lookupDescriptor(rollFn(DiceType.D100))
        return base.copy(
            descriptors = base.descriptors + descriptor,
        )
    }

    fun rollAbility(
        result: CreatureResult? = null,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CreatureResult {
        val base = result ?: CreatureResult(
            descriptors = emptyList(),
            abilities = emptyList(),
            initialBehavior = "",
            statistics = "",
        )
        val ability = lookupAbility(rollFn(DiceType.D100))
        return base.copy(
            abilities = base.abilities + ability,
        )
    }

    fun rollStatistics(
        result: CreatureResult? = null,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CreatureResult {
        val base = result ?: CreatureResult(
            descriptors = emptyList(),
            abilities = emptyList(),
            initialBehavior = "",
            statistics = "",
        )
        val stat = lookupStatistic(rollFn(DiceType.D10))
        return base.copy(statistics = stat)
    }

    fun rollInitialBehavior(
        result: CreatureResult? = null,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CreatureResult {
        val base = result ?: CreatureResult(
            descriptors = emptyList(),
            abilities = emptyList(),
            initialBehavior = "",
            statistics = "",
        )
        val behavior = lookupInitialBehavior(rollFn(DiceType.D10))
        return base.copy(initialBehavior = behavior)
    }

    fun rollNewBehavior(
        result: CreatureResult? = null,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): CreatureResult {
        val base = result ?: CreatureResult(
            descriptors = emptyList(),
            abilities = emptyList(),
            initialBehavior = "",
            statistics = "",
        )
        val behavior = lookupNewBehavior(rollFn(DiceType.D10))
        return base.copy(
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
