package com.kleros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.kleros.dice.DiceScreen
import com.kleros.ui.theme.KlerosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlerosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    DiceScreen()
                }
            }
        }
    }
}

