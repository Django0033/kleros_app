package com.kleros.character

data class CharacterHistory(
    val results: List<CharacterResult> = emptyList(),
) {
    companion object {
        const val MAX_SIZE = 10
    }

    fun append(result: CharacterResult): CharacterHistory {
        val newResults = listOf(result) + results
        return copy(results = newResults.take(MAX_SIZE))
    }
}
