@file:Suppress("MagicNumber")

package com.kleros.namegenerator

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
fun NameScreen(modifier: Modifier = Modifier) {
    var rollMode by remember { mutableStateOf(RollMode.NORMAL) }
    var currentName by remember { mutableStateOf<String?>(null) }
    var history by remember { mutableStateOf(NameHistory()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Name Generator",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        RollModeSelector(
            selectedMode = rollMode,
            onModeSelect = { rollMode = it },
        )

        Spacer(Modifier.height(16.dp))

        FilledTonalButton(
            onClick = {
                val result = NameGenerator.generate(rollMode = rollMode) {
                    DiceRoller.roll(DiceType.D20)
                }
                currentName = result.name
                history = history.append(result)
            },
            modifier = Modifier.testTag("generateButton"),
        ) {
            Text("Generate Name")
        }

        Spacer(Modifier.height(16.dp))

        currentName?.let { name ->
            Text(
                text = name,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.testTag("resultName"),
            )
        }

        Spacer(Modifier.height(16.dp))

        HistorySection(history = history)
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RollModeSelector(
    selectedMode: RollMode,
    onModeSelect: (RollMode) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RollMode.entries.forEach { mode ->
            FilterChip(
                selected = selectedMode == mode,
                onClick = { onModeSelect(mode) },
                label = { Text(mode.label) },
                modifier = Modifier.testTag("rollMode${mode.name}"),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistorySection(history: NameHistory) {
    Column {
        Text(
            text = "History",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        if (history.results.isEmpty()) {
            Text(
                text = "No names generated yet",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Column(modifier = Modifier.testTag("historyList")) {
                history.results.forEach { result ->
                    Text(
                        text = result.name,
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
