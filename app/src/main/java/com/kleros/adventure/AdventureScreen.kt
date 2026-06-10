@file:Suppress("FunctionNaming")

package com.kleros.adventure

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kleros.dice.DiceType
import com.kleros.table.TableScreen

@Composable
fun AdventureScreen(modifier: Modifier = Modifier) {
    TableScreen(
        tables = AdventureData.tables,
        modifier = modifier,
        title = "Adventure Crafter",
        diceType = DiceType.D100,
    )
}
