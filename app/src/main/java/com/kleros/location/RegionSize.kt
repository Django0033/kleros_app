@file:Suppress("MagicNumber")

package com.kleros.location

enum class RegionSize(val label: String, val startingPP: Int) {
    SMALL("Small", 3),
    AVERAGE("Average", 0),
    LARGE("Large", -3),
}
