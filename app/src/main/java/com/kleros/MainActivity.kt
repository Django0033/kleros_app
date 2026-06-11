package com.kleros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.kleros.adventure.AdventureScreen
import com.kleros.character.CharacterScreen
import com.kleros.creature.CreatureScreen
import com.kleros.dice.DiceScreen
import com.kleros.fate.FateScreen
import com.kleros.meaning.MeaningScreen
import com.kleros.namegenerator.NameScreen
import com.kleros.ui.theme.KlerosTheme
import kotlinx.coroutines.launch

@Suppress("FunctionNaming")
private enum class Screen(val label: String, val icon: ImageVector) {
    DICE("Dice Roll", Icons.Filled.Casino),
    NAME_GENERATOR("Name Gen", Icons.Filled.Badge),
    MEANING("Meaning", Icons.Filled.Psychology),
    CHARACTER_CRAFTER("Char Craft", Icons.Filled.Face),
    CREATURE_CRAFTER("Crea Craft", Icons.Filled.BugReport),
    ADVENTURE_CRAFTER("Adv Craft", Icons.Filled.Star),
    FATE("Fate", Icons.Filled.Visibility),
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlerosTheme {
                AppNavigation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionNaming")
@Composable
internal fun AppNavigation(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf(Screen.DICE) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                Screen.entries.forEach { screen ->
                    NavigationDrawerItem(
                        selected = currentScreen == screen,
                        onClick = {
                            currentScreen = screen
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.label) },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("navDrawerHamburger"),
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open navigation")
                        }
                    },
                )
            },
            modifier = modifier,
        ) { innerPadding ->
            when (currentScreen) {
                Screen.DICE -> DiceScreen(modifier = Modifier.padding(innerPadding))
                Screen.NAME_GENERATOR -> NameScreen(modifier = Modifier.padding(innerPadding))
                Screen.MEANING -> MeaningScreen(modifier = Modifier.padding(innerPadding))
                Screen.CHARACTER_CRAFTER -> CharacterScreen(modifier = Modifier.padding(innerPadding))
                Screen.CREATURE_CRAFTER -> CreatureScreen(modifier = Modifier.padding(innerPadding))
                Screen.ADVENTURE_CRAFTER -> AdventureScreen(modifier = Modifier.padding(innerPadding))
                Screen.FATE -> FateScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
