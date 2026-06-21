@file:Suppress("MagicNumber", "MultipleEmitters")

package com.kleros.location

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
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

@OptIn(ExperimentalLayoutApi::class)
@Suppress("FunctionNaming", "LongMethod")
@Composable
fun LocationScreen(modifier: Modifier = Modifier) {
    var selectedRegion by remember { mutableStateOf<RegionSize?>(null) }
    var currentResult by remember { mutableStateOf<LocationResult?>(null) }
    var history by remember { mutableStateOf(LocationHistory()) }
    var elementCount by remember { mutableIntStateOf(0) }

    fun currentPP() = (selectedRegion?.startingPP ?: 0) + (elementCount / 3)
    fun isFinished() = currentResult?.let { LocationCrafter.isComplete(it) } ?: false

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Location Crafter",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(16.dp))
        RegionSelector(
            selectedRegion = selectedRegion,
            onRegionSelect = { region ->
                selectedRegion = region
                currentResult = null
                elementCount = 0
            },
        )

        if (selectedRegion != null) {
            val finished = isFinished()
            StatsHeader(
                pp = currentPP(),
                areaNumber = elementCount + 1,
            )

            ActionButtons(onRollElement = {
                    val result = LocationCrafter.rollElement(
                        pp = currentPP(),
                        result = currentResult,
                    )
                    currentResult = result
                    elementCount++
                },
                onRollDescriptor = {
                    val result = LocationCrafter.rollDescriptor(
                        result = currentResult,
                    )
                    currentResult = result
                },
                disabled = finished,
            )

            ResultDisplay(result = currentResult)
            if (finished) {
                CompleteBanner(
                    onSaveNew = {
                        history = history.append(result = currentResult!!)
                        currentResult = null
                        elementCount = 0
                    },
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        HistorySection(history = history)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Suppress("FunctionNaming", "MultipleEmitters")
@Composable
private fun RegionSelector(
    selectedRegion: RegionSize?,
    onRegionSelect: (RegionSize) -> Unit,
) {
    Text(
        text = "Region Size",
        style = MaterialTheme.typography.labelLarge,
    )
    Spacer(Modifier.height(8.dp))
    FlowRow {
        RegionSize.entries.forEach { region ->
            FilterChip(
                selected = selectedRegion == region,
                onClick = { onRegionSelect(region) },
                label = { Text(region.label) },
                modifier = Modifier.testTag("regionChip_${region.name}"),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun StatsHeader(pp: Int, areaNumber: Int) {
    Spacer(Modifier.height(16.dp))
    Text(
        text = "PP: $pp",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.testTag("ppLabel"),
    )
    Text(
        text = "Area #$areaNumber",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Suppress("FunctionNaming")
@Composable
private fun ActionButtons(
    onRollElement: () -> Unit,
    onRollDescriptor: () -> Unit,
    disabled: Boolean,
) {
    Spacer(Modifier.height(16.dp))

    FilledTonalButton(
        onClick = onRollElement,
        enabled = !disabled,
        modifier = Modifier.testTag("rollElementButton"),
    ) {
        Text("Roll Element")
    }

    Spacer(Modifier.height(8.dp))

    FilledTonalButton(
        onClick = onRollDescriptor,
        enabled = !disabled,
        modifier = Modifier.testTag("rollDescriptorButton"),
    ) {
        Text("Roll Descriptor")
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ResultDisplay(result: LocationResult?) {
    result?.let { res ->
        Spacer(Modifier.height(16.dp))

        if (res.elements.isNotEmpty()) {
            Text(
                text = "Element: ${res.elements.last()}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("elementResult"),
            )
        }

        if (res.descriptors.isNotEmpty()) {
            Text(
                text = "Descriptor: ${res.descriptors.last()}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("descriptorResult"),
            )
        }

        if (res.elements.size > 1 || res.descriptors.size > 1) {
            Spacer(Modifier.height(8.dp))
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "All Descriptors:",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    res.descriptors.forEach { desc ->
                        Text(
                            text = "  • $desc",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "All Elements:",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    res.elements.forEach { elem ->
                        Text(
                            text = "  • $elem",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun CompleteBanner(onSaveNew: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Exploration Complete!",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("completeBanner"),
            )
            Spacer(Modifier.height(8.dp))
            FilledTonalButton(
                onClick = onSaveNew,
                modifier = Modifier.testTag("saveResultButton"),
            ) {
                Text("Save & Start New")
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistorySection(history: LocationHistory) {
    Column {
        Text(
            text = "History",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        if (history.results.isEmpty()) {
            Text(
                text = "No locations explored yet",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Column(modifier = Modifier.testTag("historyList")) {
                history.results.forEach { result ->
                    val descriptors = result.descriptors.joinToString("/")
                    val elements = result.elements.joinToString(", ")
                    val label = if (descriptors.isNotEmpty()) {
                        "$descriptors — $elements"
                    } else {
                        elements
                    }
                    Text(
                        text = label.ifEmpty { "(empty)" },
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
