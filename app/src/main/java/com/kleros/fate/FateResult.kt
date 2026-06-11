package com.kleros.fate

sealed class FateResult {
    abstract val roll: Int
    abstract val oddsLevel: OddsLevel
    abstract val isDouble: Boolean

    data class ExceptionalYes(
        override val roll: Int,
        override val oddsLevel: OddsLevel,
        override val isDouble: Boolean,
    ) : FateResult()

    data class Yes(
        override val roll: Int,
        override val oddsLevel: OddsLevel,
        override val isDouble: Boolean,
    ) : FateResult()

    data class No(
        override val roll: Int,
        override val oddsLevel: OddsLevel,
        override val isDouble: Boolean,
    ) : FateResult()

    data class ExceptionalNo(
        override val roll: Int,
        override val oddsLevel: OddsLevel,
        override val isDouble: Boolean,
    ) : FateResult()
}
