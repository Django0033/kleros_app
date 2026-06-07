package com.kleros.dice

import kotlin.random.Random

object DiceRoller {
    fun roll(type: DiceType): Int = Random.nextInt(1, type.faces + 1)
}
