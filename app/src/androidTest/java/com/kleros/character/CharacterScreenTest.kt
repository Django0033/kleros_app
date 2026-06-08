package com.kleros.character

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
class CharacterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersGenerateButton() {
        composeTestRule.setContent {
            KlerosTheme {
                CharacterScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").assertIsDisplayed()
    }

    @Test
    fun tapGenerateShowsDescriptorLabels() {
        composeTestRule.setContent {
            KlerosTheme {
                CharacterScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("identityLabel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("mindLabel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bodyLabel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("talentLabel").assertIsDisplayed()
    }

    @Test
    fun generateThreeTimesShowsThreeHistoryItems() {
        composeTestRule.setContent {
            KlerosTheme {
                CharacterScreen()
            }
        }

        repeat(3) {
            composeTestRule.onNodeWithTag("generateButton").performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onAllNodesWithTag("historyItem").assertCountEquals(3)
    }
}
