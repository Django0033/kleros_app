package com.kleros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kleros.dice.DiceScreen
import com.kleros.meaning.MeaningScreen
import com.kleros.namegenerator.NameScreen
import com.kleros.ui.theme.KlerosTheme

@Suppress("FunctionNaming")
private enum class Screen(val label: String) {
    DICE("Dice Roll"),
    NAME_GENERATOR("Name Gen"),
    MEANING("Meaning"),
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlerosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun AppNavigation(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf(Screen.DICE) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            Screen.entries.forEach { screen ->
                FilterChip(
                    selected = currentScreen == screen,
                    onClick = { currentScreen = screen },
                    label = { Text(screen.label) },
                )
            }
        }

        when (currentScreen) {
            Screen.DICE -> DiceScreen()
            Screen.NAME_GENERATOR -> NameScreen()
            Screen.MEANING -> MeaningScreen()
        }
    }
}
