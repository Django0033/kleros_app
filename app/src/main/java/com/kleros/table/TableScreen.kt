@file:Suppress("MagicNumber")

package com.kleros.table

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

@Suppress("FunctionNaming")
@Composable
fun TableScreen(
    tables: List<TableDef>,
    modifier: Modifier = Modifier,
    title: String = "",
    diceType: DiceType = DiceType.D20,
) {
    var selectedTableIndex by remember { mutableIntStateOf(0) }
    var currentResult by remember { mutableStateOf<TableRollResult?>(null) }
    var history by remember { mutableStateOf(TableHistory()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.height(8.dp))
        }

        TableSelector(
            tables = tables,
            selectedIndex = selectedTableIndex,
            onSelect = { selectedTableIndex = it },
        )

        Spacer(Modifier.height(16.dp))

        FilledTonalButton(
            onClick = {
                val result = TableRoller.roll(tables[selectedTableIndex]) {
                    DiceRoller.roll(diceType)
                }
                currentResult = result
                if (result is TableRollResult.Success) {
                    history = history.append(result)
                }
            },
            modifier = Modifier.testTag("rollButton"),
        ) {
            Text("Roll")
        }

        Spacer(Modifier.height(16.dp))

        ResultDisplay(result = currentResult)

        Spacer(Modifier.height(16.dp))

        HistorySection(history = history)
    }
}

@Suppress("FunctionNaming")
@Composable
private fun TableSelector(
    tables: List<TableDef>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tables.forEachIndexed { index, table ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                label = { Text(table.name) },
                modifier = Modifier.testTag("tableSelector_${table.name}"),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ResultDisplay(result: TableRollResult?) {
    result?.let { r ->
        when (r) {
            is TableRollResult.Success -> {
                Text(
                    text = r.value,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.testTag("rollResult"),
                )
            }

            is TableRollResult.Error -> {
                Text(
                    text = r.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("rollResult"),
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistorySection(history: TableHistory) {
    Column {
        Text(
            text = "History",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        if (history.entries.isEmpty()) {
            Text(
                text = "No rolls yet",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Column(modifier = Modifier.testTag("historyList")) {
                history.entries.forEach { result ->
                    Text(
                        text = result.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("historyItem"),
                    )
                }
            }
        }
    }
}
