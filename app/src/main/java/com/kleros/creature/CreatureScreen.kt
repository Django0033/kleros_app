@file:Suppress("MagicNumber")

package com.kleros.creature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Suppress("FunctionNaming")
@Composable
fun CreatureScreen(modifier: Modifier = Modifier) {
    var currentResult by remember { mutableStateOf<CreatureResult?>(null) }
    var history by remember { mutableStateOf(CreatureHistory()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Creature Crafter",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(16.dp))

        FilledTonalButton(
            onClick = {
                val result = CreatureCrafter.generate()
                currentResult = result
                history = history.append(result)
            },
            modifier = Modifier.testTag("generateButton"),
        ) {
            Text("Generate Creature")
        }

        Spacer(Modifier.height(16.dp))

        currentResult?.let { result ->
            ResultCard(result = result)

            Spacer(Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(Modifier.height(12.dp))
        }

        ActionButtons(result = currentResult) { updatedResult ->
            currentResult = updatedResult
            history = history.append(updatedResult)
        }

        Spacer(Modifier.height(16.dp))

        HistorySection(history = history)
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ResultCard(result: CreatureResult) {
    Column {
        Text(
            text = "Descriptors: ${result.descriptors.joinToString(", ")}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("descriptorsLabel"),
        )
        Text(
            text = "Abilities: ${result.abilities.joinToString(", ")}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("abilitiesLabel"),
        )
        Text(
            text = "Initial: ${result.initialBehavior}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("initialLabel"),
        )
        Text(
            text = "Statistics: ${result.statistics}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("statisticsLabel"),
        )
        result.newBehavior?.let { nb ->
            Text(
                text = "New Behavior: $nb",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("newBehaviorLabel"),
            )
        }
    }
}

@Suppress("FunctionNaming", "ParameterNaming")
@Composable
private fun ActionButtons(
    result: CreatureResult?,
    onResultUpdated: (CreatureResult) -> Unit,
) {
    Column {
        OutlinedButton(
            onClick = {
                val updated = CreatureCrafter.rollDescriptor(result)
                onResultUpdated(updated)
            },
            modifier = Modifier.testTag("rollDescriptorButton"),
        ) {
            Text("Descriptors")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                val updated = CreatureCrafter.rollAbility(result)
                onResultUpdated(updated)
            },
            modifier = Modifier.testTag("rollAbilityButton"),
        ) {
            Text("Abilities")
        }

        OutlinedButton(
            onClick = {
                val updated = CreatureCrafter.rollInitialBehavior(result)
                onResultUpdated(updated)
            },
            modifier = Modifier.testTag("rollInitialButton"),
        ) {
            Text("Initial Behavior")
        }

        OutlinedButton(
            onClick = {
                val updated = CreatureCrafter.rollStatistics(result)
                onResultUpdated(updated)
            },
            modifier = Modifier.testTag("rollStatsButton"),
        ) {
            Text("Statistics")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                val updated = CreatureCrafter.rollNewBehavior(result)
                onResultUpdated(updated)
            },
            modifier = Modifier.testTag("newBehaviorButton"),
        ) {
            Text("New Behavior")
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistorySection(history: CreatureHistory) {
    Column {
        Text(
            text = "History",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        if (history.results.isEmpty()) {
            Text(
                text = "No creatures generated yet",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Column(modifier = Modifier.testTag("historyList")) {
                history.results.forEach { result ->
                    val parts = result.descriptors + result.abilities
                    val label = parts.joinToString("/") + " — " + result.initialBehavior
                    Text(
                        text = label,
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
