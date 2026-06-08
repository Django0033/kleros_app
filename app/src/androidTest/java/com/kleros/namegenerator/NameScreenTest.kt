package com.kleros.namegenerator

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kleros.ui.theme.KlerosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersGenerateButton() {
        composeTestRule.setContent {
            KlerosTheme {
                NameScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").assertIsDisplayed()
    }

    @Test
    fun rollModeSelectorShowsThreeModes() {
        composeTestRule.setContent {
            KlerosTheme {
                NameScreen()
            }
        }

        composeTestRule.onNodeWithTag("rollModeNormal").assertIsDisplayed()
        composeTestRule.onNodeWithTag("rollModeAdvantage").assertIsDisplayed()
        composeTestRule.onNodeWithTag("rollModeDisadvantage").assertIsDisplayed()
    }

    @Test
    fun tapGenerateDisplaysResult() {
        composeTestRule.setContent {
            KlerosTheme {
                NameScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("resultName").assertIsDisplayed()
    }

    @Test
    fun generateThreeTimesShowsThreeHistoryItems() {
        composeTestRule.setContent {
            KlerosTheme {
                NameScreen()
            }
        }

        repeat(3) {
            composeTestRule.onNodeWithTag("generateButton").performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onAllNodesWithTag("historyItem").assertCountEquals(3)
    }

    @Test
    fun switchingModeDoesNotClearHistory() {
        composeTestRule.setContent {
            KlerosTheme {
                NameScreen()
            }
        }

        // Generate a name first
        composeTestRule.onNodeWithTag("generateButton").performClick()
        composeTestRule.waitForIdle()

        // Switch mode to Advantage
        composeTestRule.onNodeWithTag("rollModeAdvantage").performClick()
        composeTestRule.waitForIdle()

        // History should still have 1 item
        composeTestRule.onAllNodesWithTag("historyItem").assertCountEquals(1)
    }
}
