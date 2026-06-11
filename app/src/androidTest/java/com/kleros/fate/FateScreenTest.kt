package com.kleros.fate

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
class FateScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersTitle() {
        composeTestRule.setContent {
            KlerosTheme {
                FateScreen()
            }
        }

        composeTestRule.onNodeWithText("Fate Oracle").assertIsDisplayed()
    }

    @Test
    fun rollButtonExists() {
        composeTestRule.setContent {
            KlerosTheme {
                FateScreen()
            }
        }

        composeTestRule.onNodeWithTag("fateRollButton").assertIsDisplayed()
    }

    @Test
    fun oddsChipForFiftyFiftyExists() {
        composeTestRule.setContent {
            KlerosTheme {
                FateScreen()
            }
        }

        composeTestRule.onNodeWithTag("oddsChip_50/50").assertIsDisplayed()
    }

    @Test
    fun tapRollDisplaysResultText() {
        composeTestRule.setContent {
            KlerosTheme {
                FateScreen()
            }
        }

        composeTestRule.onNodeWithTag("fateRollButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("fateResultText").assertIsDisplayed()
    }
}
