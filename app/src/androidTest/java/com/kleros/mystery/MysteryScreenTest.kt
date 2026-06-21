package com.kleros.mystery

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kleros.ui.theme.KlerosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MysteryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersCheckButton() {
        composeTestRule.setContent {
            KlerosTheme {
                MysteryScreen()
            }
        }

        composeTestRule.onNodeWithTag("checkButton").assertIsDisplayed()
    }

    @Test
    fun screenShowsRollDescriptorButton() {
        composeTestRule.setContent {
            KlerosTheme {
                MysteryScreen()
            }
        }

        composeTestRule.onNodeWithTag("rollDescriptorButton").assertIsDisplayed()
    }

    @Test
    fun tapCheckShowsDiscoveryResult() {
        composeTestRule.setContent {
            KlerosTheme {
                MysteryScreen()
            }
        }

        composeTestRule.onNodeWithTag("checkButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("discoveryResult").assertIsDisplayed()
    }

    @Test
    fun screenShowsTitle() {
        composeTestRule.setContent {
            KlerosTheme {
                MysteryScreen()
            }
        }

        composeTestRule.onNodeWithText("Mystery Crafter").assertIsDisplayed()
    }

    @Test
    fun screenShowsBoxesCounter() {
        composeTestRule.setContent {
            KlerosTheme {
                MysteryScreen()
            }
        }

        composeTestRule.onNodeWithText("Boxes: 0/20").assertIsDisplayed()
    }

    @Test
    fun checkThreeTimesShowsThreeHistoryItems() {
        composeTestRule.setContent {
            KlerosTheme {
                MysteryScreen()
            }
        }

        repeat(3) {
            composeTestRule.onNodeWithTag("checkButton").performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onNodeWithTag("historyList").assertIsDisplayed()
    }

    @Test
    fun tapRollDescriptorShowsHistoryItem() {
        composeTestRule.setContent {
            KlerosTheme {
                MysteryScreen()
            }
        }

        composeTestRule.onNodeWithTag("rollDescriptorButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("historyList").assertIsDisplayed()
    }
}
