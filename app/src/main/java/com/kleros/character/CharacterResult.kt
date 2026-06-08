package com.kleros.character

data class CharacterResult(
    val identity: String,
    val mind: String,
    val body: String,
    val talent: String,
    val statistics: String,
    val timestampMillis: Long = System.currentTimeMillis(),
)
