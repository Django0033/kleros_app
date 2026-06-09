package com.kleros.creature

data class CreatureHistory(
    val results: List<CreatureResult> = emptyList(),
) {
    companion object {
        const val MAX_SIZE = 10
    }

    fun append(result: CreatureResult): CreatureHistory {
        val newResults = listOf(result) + results
        return copy(results = newResults.take(MAX_SIZE))
    }
}
