package com.kleros.dice

data class DiceRollResult(
    val diceType: DiceType,
    val value: Int,
    val timestampMillis: Long = System.currentTimeMillis(),
)
