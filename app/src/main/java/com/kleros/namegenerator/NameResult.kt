package com.kleros.namegenerator

data class NameResult(
    val name: String,
    val rollMode: RollMode,
    val timestampMillis: Long = System.currentTimeMillis(),
)
