@file:Suppress("MagicNumber")

package com.kleros.adventure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.kleros.dice.DiceRoller
import com.kleros.dice.DiceType
import com.kleros.table.TableRollResult
import com.kleros.table.TableRoller

private const val HISTORY_MAX = 10

@Suppress("FunctionNaming")
@Composable
fun AdventureScreen(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var currentResult by remember { mutableStateOf<String?>(null) }
    var history by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Adventure Crafter",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(12.dp))

        ThemeSelector(
            selectedIndex = selectedIndex,
            onSelect = { selectedIndex = it },
        )

        Spacer(Modifier.height(12.dp))

        RollButtons(
            selectedIndex = selectedIndex,
            onResult = { text ->
                currentResult = text
                history = (listOf(text) + history).take(HISTORY_MAX)
            },
            onThemeRoll = { index, text ->
                selectedIndex = index
                currentResult = text
                history = (listOf(text) + history).take(HISTORY_MAX)
            },
        )

        Spacer(Modifier.height(16.dp))

        currentResult?.let { result ->
            ResultDisplay(
                themeName = AdventureData.tables[selectedIndex].name,
                result = result,
            )
        }

        Spacer(Modifier.height(16.dp))

        HistorySection(history = history)
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ThemeSelector(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AdventureData.tables.forEachIndexed { index, table ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                label = { Text(table.name) },
                modifier = Modifier.testTag("themeChip_${table.name}"),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RollButtons(
    selectedIndex: Int,
    onResult: (String) -> Unit,
    onThemeRoll: (Int, String) -> Unit,
) {
    Column {
        FilledTonalButton(
        onClick = {
            val rollFn: () -> Int = { DiceRoller.roll(DiceType.D100) }
            val result = TableRoller.roll(AdventureData.tables[selectedIndex], rollFn = rollFn)
            val text = when (result) {
                is TableRollResult.Success -> result.value
                is TableRollResult.Error -> "?"
            }
            onResult(text)
        },
        modifier = Modifier.testTag("rollButton"),
    ) {
        Text("Roll ${AdventureData.tables[selectedIndex].name}")
    }

    Spacer(Modifier.height(8.dp))

    OutlinedButton(
        onClick = {
            val themeRoll = DiceRoller.roll(DiceType.D20)
            val themeIndex = ((themeRoll - 1) % AdventureData.tables.size).coerceIn(0, AdventureData.tables.size - 1)
            val rollFn: () -> Int = { DiceRoller.roll(DiceType.D100) }
            val result = TableRoller.roll(AdventureData.tables[themeIndex], rollFn = rollFn)
            val text = when (result) {
                is TableRollResult.Success -> result.value
                is TableRollResult.Error -> "?"
            }
            onThemeRoll(themeIndex, text)
        },
        modifier = Modifier.testTag("randomThemeButton"),
    ) {
        Text("Random Theme")
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ResultDisplay(
    themeName: String,
    result: String,
) {
    Text(
        text = "$themeName: $result",
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier.testTag("resultText"),
    )
}

@Suppress("FunctionNaming")
@Composable
private fun HistorySection(history: List<String>) {
    Column {
        Text(
            text = "History",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        if (history.isEmpty()) {
            Text(
                text = "No rolls yet",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            history.forEach { text ->
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("historyItem"),
                )
            }
        }
    }
}
