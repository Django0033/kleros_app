@file:Suppress("MagicNumber", "MatchingDeclarationName")

package com.kleros.fate

enum class OddsLevel(val label: String, val exYesMax: Int, val yesMax: Int, val noMax: Int) {
    IMPOSSIBLE("Impossible", 0, 0, 0),
    NEARLY_IMPOSSIBLE("Nearly Impossible", 0, 3, 15),
    VERY_UNLIKELY("Very Unlikely", 0, 5, 25),
    UNLIKELY("Unlikely", 0, 7, 35),
    FIFTY_FIFTY("50/50", 0, 10, 50),
    LIKELY("Likely", 0, 13, 65),
    VERY_LIKELY("Very Likely", 0, 15, 75),
    NEARLY_CERTAIN("Nearly Certain", 0, 17, 85),
    CERTAIN("Certain", 18, 18, 90),
}
