package com.kleros.creature

data class CreatureResult(
    val descriptors: List<String>,
    val abilities: List<String>,
    val initialBehavior: String,
    val statistics: String,
    val newBehavior: String? = null,
    val timestampMillis: Long = System.currentTimeMillis(),
)
