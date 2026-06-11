@file:Suppress("MagicNumber")

package com.kleros.fate

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

@Suppress("FunctionNaming")
@Composable
fun FateScreen(modifier: Modifier = Modifier) {
    var selectedOdds by remember { mutableStateOf(OddsLevel.FIFTY_FIFTY) }
    var currentResult by remember { mutableStateOf<FateResult?>(null) }
    var history by remember { mutableStateOf(FateHistory()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Fate Oracle",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        OddsSelector(
            selectedOdds = selectedOdds,
            onOddsSelect = { selectedOdds = it },
        )

        Spacer(Modifier.height(16.dp))

        FilledTonalButton(
            onClick = {
                val result = FateRoller.roll(selectedOdds)
                currentResult = result
                history = history.append(result)
            },
            modifier = Modifier.testTag("fateRollButton"),
        ) {
            Text("Ask Oracle")
        }

        Spacer(Modifier.height(16.dp))

        currentResult?.let { result ->
            ResultDisplay(result = result)
        }

        Spacer(Modifier.height(16.dp))

        HistorySection(history = history)
    }
}

@Suppress("FunctionNaming")
@Composable
private fun OddsSelector(
    selectedOdds: OddsLevel,
    onOddsSelect: (OddsLevel) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OddsLevel.entries.forEach { odds ->
            FilterChip(
                selected = selectedOdds == odds,
                onClick = { onOddsSelect(odds) },
                label = { Text(odds.label) },
                modifier = Modifier.testTag("oddsChip_${odds.label}"),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ResultDisplay(result: FateResult) {
    Column(modifier = Modifier.testTag("fateResultText")) {
        val resultType = when (result) {
            is FateResult.ExceptionalYes -> "Exceptional Yes"
            is FateResult.Yes -> "Yes"
            is FateResult.No -> "No"
            is FateResult.ExceptionalNo -> "Exceptional No"
        }

        Text(
            text = "${result.oddsLevel.label} | Roll: ${result.roll} | $resultType",
            style = MaterialTheme.typography.bodyLarge,
        )

        if (result.isDouble) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "⚡ Random Event!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistorySection(history: FateHistory) {
    Column {
        Text(
            text = "History",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        if (history.results.isEmpty()) {
            Text(
                text = "No rolls yet",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Column(modifier = Modifier.testTag("historyList")) {
                history.results.forEach { result ->
                    val resultType = when (result) {
                        is FateResult.ExceptionalYes -> "EY"
                        is FateResult.Yes -> "Y"
                        is FateResult.No -> "N"
                        is FateResult.ExceptionalNo -> "EN"
                    }
                    val doubleMarker = if (result.isDouble) " ⚡" else ""
                    Text(
                        text = "${result.oddsLevel.label} [${result.roll}] $resultType$doubleMarker",
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
