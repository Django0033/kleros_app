@file:Suppress("MagicNumber")

package com.kleros.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@Suppress("FunctionNaming")
@Composable
fun CharacterScreen(modifier: Modifier = Modifier) {
    var currentResult by remember { mutableStateOf<CharacterResult?>(null) }
    var history by remember { mutableStateOf(CharacterHistory()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Character Crafter",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(16.dp))

        FilledTonalButton(
            onClick = {
                val result = CharacterCrafter.generate()
                currentResult = result
                history = history.append(result)
            },
            modifier = Modifier.testTag("generateButton"),
        ) {
            Text("Generate Character")
        }

        Spacer(Modifier.height(16.dp))

        currentResult?.let { result ->
            ResultCard(result = result)
        }

        Spacer(Modifier.height(16.dp))

        HistorySection(history = history)
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ResultCard(result: CharacterResult) {
    Column {
        Text(
            text = "Identity: ${result.identity}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("identityLabel"),
        )
        Text(
            text = "Mind: ${result.mind}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("mindLabel"),
        )
        Text(
            text = "Body: ${result.body}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("bodyLabel"),
        )
        Text(
            text = "Talent: ${result.talent}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("talentLabel"),
        )
        Text(
            text = "Statistics: ${result.statistics}",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistorySection(history: CharacterHistory) {
    Column {
        Text(
            text = "History",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        if (history.results.isEmpty()) {
            Text(
                text = "No characters generated yet",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Column(modifier = Modifier.testTag("historyList")) {
                history.results.forEach { result ->
                    val parts = listOf(result.identity, result.mind, result.body, result.talent)
                    val label = parts.joinToString("/") + " — " + result.statistics
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
