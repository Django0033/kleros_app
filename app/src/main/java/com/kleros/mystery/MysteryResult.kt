package com.kleros.mystery

data class MysteryResult(
    val discoveryResult: String,
    val descriptor: String?,
    val boxes: Int,
    val isDefinitive: Boolean,
    val timestampMillis: Long = System.currentTimeMillis(),
)
