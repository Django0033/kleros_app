@file:Suppress("MagicNumber")

package com.kleros.fate

import com.kleros.dice.DiceRoller
import com.kleros.dice.DiceType

object FateRoller {

    fun roll(
        oddsLevel: OddsLevel,
        rollFn: () -> Int = { DiceRoller.roll(DiceType.D100) },
    ): FateResult {
        val roll = rollFn()
        val isDouble = (roll % 11 == 0) || roll == 100

        val baseResult = determineBaseResult(roll, oddsLevel, isDouble)

        return applySpecialRules(roll, oddsLevel, isDouble, baseResult)
    }

    private fun determineBaseResult(
        roll: Int,
        oddsLevel: OddsLevel,
        isDouble: Boolean,
    ): FateResult = when {
        roll <= oddsLevel.exYesMax -> FateResult.ExceptionalYes(roll, oddsLevel, isDouble)
        roll <= oddsLevel.yesMax -> FateResult.Yes(roll, oddsLevel, isDouble)
        roll <= oddsLevel.noMax -> FateResult.No(roll, oddsLevel, isDouble)
        else -> FateResult.ExceptionalNo(roll, oddsLevel, isDouble)
    }

    private fun applySpecialRules(
        roll: Int,
        oddsLevel: OddsLevel,
        isDouble: Boolean,
        baseResult: FateResult,
    ): FateResult = when {
        roll == 1 && oddsLevel.yesMax > 0 ->
            upgradeResult(baseResult, roll, oddsLevel, isDouble)
        oddsLevel == OddsLevel.CERTAIN && roll == 100 ->
            FateResult.ExceptionalYes(roll, oddsLevel, isDouble)
        isDouble && roll <= oddsLevel.noMax ->
            upgradeResult(baseResult, roll, oddsLevel, isDouble)
        else -> baseResult
    }

    private fun upgradeResult(
        result: FateResult,
        roll: Int,
        oddsLevel: OddsLevel,
        isDouble: Boolean,
    ): FateResult = when (result) {
        is FateResult.ExceptionalNo -> FateResult.No(roll, oddsLevel, isDouble)
        is FateResult.No -> FateResult.Yes(roll, oddsLevel, isDouble)
        is FateResult.Yes -> FateResult.ExceptionalYes(roll, oddsLevel, isDouble)
        is FateResult.ExceptionalYes -> result
    }
}
