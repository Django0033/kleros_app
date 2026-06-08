package com.kleros.dice

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kleros.ui.theme.KlerosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiceScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appLaunchesToDiceScreen() {
        composeTestRule.setContent {
            KlerosTheme {
                DiceScreen()
            }
        }

        // Verify the Roll button is displayed (DiceScreen renders)
        composeTestRule.onNodeWithText("Roll").assertIsDisplayed()
        // Verify at least one dice chip is displayed (default D6)
        composeTestRule.onNodeWithText("D6").assertIsDisplayed()
    }

    @Test
    fun selectD20Chip() {
        composeTestRule.setContent {
            KlerosTheme {
                DiceScreen()
            }
        }

        // Click on D20 chip
        composeTestRule.onNodeWithText("D20").performClick()
        // Verify D20 is displayed and Roll button is enabled
        composeTestRule.onNodeWithText("D20").assertIsDisplayed()
        composeTestRule.onNodeWithText("Roll").assertIsEnabled()
    }

    @Test
    fun tapRollButtonDisplaysResult() {
        composeTestRule.setContent {
            KlerosTheme {
                DiceScreen()
            }
        }

        // Tap Roll button (default D6 is selected)
        composeTestRule.onNodeWithText("Roll").performClick()
        composeTestRule.waitForIdle()
        // Verify a numeric result is displayed via test tag
        composeTestRule.onNodeWithTag("resultValue").assertIsDisplayed()
    }

    @Test
    fun rollThreeTimesShowsThreeHistoryEntries() {
        composeTestRule.setContent {
            KlerosTheme {
                DiceScreen()
            }
        }

        // Roll 3 times
        repeat(3) {
            composeTestRule.onNodeWithText("Roll").performClick()
            composeTestRule.waitForIdle()
        }

        // Verify history shows exactly 3 entries
        composeTestRule.onAllNodesWithTag("historyItem").assertCountEquals(3)
    }
}
