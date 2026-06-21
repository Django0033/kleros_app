@file:Suppress("MagicNumber")

package com.kleros.mystery

import com.kleros.dice.DiceRoller
import com.kleros.dice.DiceType

object MysteryCrafter {

    fun check(
        boxes: Int,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): MysteryResult {
        val roll = rollFn(DiceType.D100)
        val total = roll + boxes
        val entry = MysteryData.discoveryCheck.first { total in it.min..it.max }
        val isDefinitive = total >= 101
        val descriptor = if (total in 16..Int.MAX_VALUE) {
            rollDescriptor(rollFn)
        } else {
            null
        }
        return MysteryResult(
            discoveryResult = entry.result,
            descriptor = descriptor,
            boxes = boxes,
            isDefinitive = isDefinitive,
        )
    }

    fun rollDescriptor(
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): String {
        val roll = rollFn(DiceType.D100)
        return MysteryData.descriptors[roll - 1].result
    }
}
