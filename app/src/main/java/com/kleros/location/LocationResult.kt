package com.kleros.location

data class LocationResult(
    val descriptors: List<String> = emptyList(),
    val elements: List<String> = emptyList(),
    val timestampMillis: Long = System.currentTimeMillis(),
)
