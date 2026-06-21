@file:Suppress("MagicNumber")

package com.kleros.mystery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
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

@Suppress("FunctionNaming")
@Composable
fun MysteryScreen(modifier: Modifier = Modifier) {
    var boxes by remember { mutableIntStateOf(0) }
    var currentResult by remember { mutableStateOf<MysteryResult?>(null) }
    var history by remember { mutableStateOf(MysteryHistory()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Mystery Crafter",
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Boxes: $boxes/20",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(16.dp))

        CheckButton(
            boxes = boxes,
            currentResult = currentResult,
            onResult = { result ->
                currentResult = result
                history = history.append(result)
                val skipBoxes = result.discoveryResult == "Nothing useful found" ||
                    result.discoveryResult == "Connect existing Clue to existing Suspect"
                if (!skipBoxes) {
                    boxes = (boxes + 1).coerceAtMost(20)
                }
            },
        )

        Spacer(Modifier.height(12.dp))

        RollDescriptorButton(
            boxes = boxes,
            onResult = { result ->
                currentResult = result
                history = history.append(result)
            },
        )

        currentResult?.let { result ->
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            ResultDisplay(result = result)
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        HistorySection(history = history)
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CheckButton(
    boxes: Int,
    currentResult: MysteryResult?,
    onResult: (MysteryResult) -> Unit,
) {
    FilledTonalButton(
        onClick = {
            val result = MysteryCrafter.check(boxes = boxes)
            onResult(result)
        },
        enabled = boxes < 20 && (currentResult?.isDefinitive != true),
        modifier = Modifier.testTag("checkButton"),
    ) {
        Text("Check")
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RollDescriptorButton(
    boxes: Int,
    onResult: (MysteryResult) -> Unit,
) {
    OutlinedButton(
        onClick = {
            val descriptor = MysteryCrafter.rollDescriptor()
            val result = MysteryResult(
                discoveryResult = "Rolled descriptor",
                descriptor = descriptor,
                boxes = boxes,
                isDefinitive = false,
            )
            onResult(result)
        },
        modifier = Modifier.testTag("rollDescriptorButton"),
    ) {
        Text("Roll Descriptor")
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ResultDisplay(result: MysteryResult) {
    Column {
        Text(
            text = result.discoveryResult,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("discoveryResult"),
        )

        result.descriptor?.let { desc ->
            Text(
                text = "Descriptor: $desc",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("descriptorText"),
            )
        }

        if (result.isDefinitive) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Definitive!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("definitiveBanner"),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistorySection(history: MysteryHistory) {
    Column {
        Text(
            text = "History",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        if (history.results.isEmpty()) {
            Text(
                text = "No checks yet",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Column(modifier = Modifier.testTag("historyList")) {
                history.results.forEach { result ->
                    val label = buildString {
                        append(result.discoveryResult)
                        result.descriptor?.let { append(" [$it]") }
                        if (result.isDefinitive) append(" ⭐")
                    }
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .testTag("historyItem"),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}
