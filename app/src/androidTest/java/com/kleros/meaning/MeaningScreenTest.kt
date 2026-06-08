package com.kleros.meaning

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kleros.ui.theme.KlerosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MeaningScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersRollButton() {
        composeTestRule.setContent {
            KlerosTheme {
                MeaningScreen()
            }
        }

        composeTestRule.onNodeWithTag("rollButton").assertIsDisplayed()
    }

    @Test
    fun actionTableChipIsPresent() {
        composeTestRule.setContent {
            KlerosTheme {
                MeaningScreen()
            }
        }

        composeTestRule.onNodeWithTag("tableSelector_Meaning Action").assertIsDisplayed()
    }

    @Test
    fun descriptionTableChipIsPresent() {
        composeTestRule.setContent {
            KlerosTheme {
                MeaningScreen()
            }
        }

        composeTestRule.onNodeWithTag("tableSelector_Meaning Description").assertIsDisplayed()
    }
}
