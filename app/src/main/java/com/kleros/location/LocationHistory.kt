package com.kleros.location

data class LocationHistory(
    val results: List<LocationResult> = emptyList(),
) {
    companion object {
        const val MAX_SIZE = 10
    }

    fun append(result: LocationResult): LocationHistory {
        val newResults = listOf(result) + results
        return copy(results = newResults.take(MAX_SIZE))
    }
}
