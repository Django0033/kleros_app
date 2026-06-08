@file:Suppress("FunctionNaming")

package com.kleros.meaning

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kleros.dice.DiceType
import com.kleros.table.TableScreen

@Composable
fun MeaningScreen(modifier: Modifier = Modifier) {
    TableScreen(
        tables = MeaningData.tables,
        modifier = modifier,
        title = "Meaning",
        diceType = DiceType.D100,
    )
}
