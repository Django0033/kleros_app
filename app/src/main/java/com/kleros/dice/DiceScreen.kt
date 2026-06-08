@file:Suppress("MagicNumber")

package com.kleros.dice

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private const val SCALE_TARGET = 1.2f
private const val SPRING_DAMPING = 0.3f
private const val SPRING_STIFFNESS = 300f

@Suppress("FunctionNaming")
@Composable
fun DiceScreen(modifier: Modifier = Modifier) {
    var selectedType by remember { mutableStateOf(DiceType.D6) }
    var currentResult by remember { mutableStateOf<DiceRollResult?>(null) }
    var history by remember { mutableStateOf(RollHistory()) }
    val scaleAnim = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    val animatedValue by animateIntAsState(
        targetValue = currentResult?.value ?: 0,
        animationSpec = spring(dampingRatio = SPRING_DAMPING, stiffness = SPRING_STIFFNESS),
        label = "rollValue",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Select Dice",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(8.dp))

        DiceTypeSelector(
            selectedType = selectedType,
            onTypeSelect = { selectedType = it },
        )

        Spacer(Modifier.height(16.dp))

        FilledTonalButton(
            onClick = {
                val value = DiceRoller.roll(selectedType)
                val result = DiceRollResult(diceType = selectedType, value = value)
                currentResult = result
                history = history.append(result)
                coroutineScope.launch {
                    scaleAnim.animateTo(SCALE_TARGET)
                    scaleAnim.animateTo(1f)
                }
            },
        ) {
            Text("Roll")
        }

        Spacer(Modifier.height(16.dp))

        ResultCard(
            currentResult = currentResult,
            animatedValue = animatedValue,
            scaleAnim = scaleAnim,
        )

        Spacer(Modifier.height(16.dp))

        HistorySection(history = history)
    }
}

@Suppress("FunctionNaming")
@Composable
private fun DiceTypeSelector(
    selectedType: DiceType,
    onTypeSelect: (DiceType) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DiceType.entries.forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelect(type) },
                label = { Text(type.label) },
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun ResultCard(
    currentResult: DiceRollResult?,
    animatedValue: Int,
    scaleAnim: Animatable<Float, AnimationVector1D>,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = currentResult?.diceType?.label ?: "-",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = animatedValue.toString(),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scaleAnim.value,
                        scaleY = scaleAnim.value,
                    )
                    .testTag("resultValue"),
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun HistorySection(history: RollHistory) {
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("historyItem"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = result.diceType.label)
                        Text(text = result.value.toString())
                    }
                }
            }
        }
    }
}
