package com.kleros.mystery

data class MysteryHistory(
    val results: List<MysteryResult> = emptyList(),
) {
    companion object {
        const val MAX_SIZE = 10
    }

    fun append(result: MysteryResult): MysteryHistory {
        val newResults = listOf(result) + results
        return copy(results = newResults.take(MAX_SIZE))
    }
}
