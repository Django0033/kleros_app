package com.kleros.fate

data class FateHistory(
    val results: List<FateResult> = emptyList(),
) {
    companion object {
        const val MAX_SIZE = 10
    }

    fun append(result: FateResult): FateHistory {
        val newResults = listOf(result) + results
        return copy(results = newResults.take(MAX_SIZE))
    }
}
