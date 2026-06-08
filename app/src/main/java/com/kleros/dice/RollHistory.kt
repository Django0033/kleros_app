package com.kleros.dice

data class RollHistory(
    val results: List<DiceRollResult> = emptyList(),
) {
    companion object {
        const val MAX_SIZE = 10
    }

    fun append(result: DiceRollResult): RollHistory {
        val newResults = listOf(result) + results
        return copy(results = newResults.take(MAX_SIZE))
    }
}
