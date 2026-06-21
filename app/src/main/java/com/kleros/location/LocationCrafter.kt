@file:Suppress("MagicNumber")

package com.kleros.location

import com.kleros.dice.DiceRoller
import com.kleros.dice.DiceType
import com.kleros.table.TableEntry

object LocationCrafter {

    fun rollDescriptor(
        result: LocationResult? = null,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): LocationResult {
        val roll = rollFn(DiceType.D100)
        val entry = LocationData.descriptors[roll - 1] as TableEntry.DIRECT
        val base = result ?: LocationResult()
        return base.copy(
            descriptors = base.descriptors + entry.result,
        )
    }

    fun rollElement(
        pp: Int,
        result: LocationResult? = null,
        rollFn: (DiceType) -> Int = { DiceRoller.roll(it) },
    ): LocationResult {
        val total = rollFn(DiceType.D10) + rollFn(DiceType.D10) + pp
        val entry = LocationData.elements.first { elementEntry ->
            val range = elementEntry as TableEntry.RANGE
            total in range.min..range.max
        }
        val rangeEntry = entry as TableEntry.RANGE
        val base = result ?: LocationResult()
        return base.copy(
            elements = base.elements + rangeEntry.result,
        )
    }

    fun isComplete(result: LocationResult): Boolean {
        return result.elements.lastOrNull() == "Complete"
    }
}
