package com.kleros.creature

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
class CreatureScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersGenerateButton() {
        composeTestRule.setContent {
            KlerosTheme {
                CreatureScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").assertIsDisplayed()
    }

    @Test
    fun tapGenerateShowsResultFields() {
        composeTestRule.setContent {
            KlerosTheme {
                CreatureScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("descriptorsLabel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("abilitiesLabel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("initialLabel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("statisticsLabel").assertIsDisplayed()
    }

    @Test
    fun tapGenerateShowsActionButtons() {
        composeTestRule.setContent {
            KlerosTheme {
                CreatureScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("rollDescriptorButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("rollAbilityButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("newBehaviorButton").assertIsDisplayed()
    }

    @Test
    fun tapRollDescriptorAddsDescriptor() {
        composeTestRule.setContent {
            KlerosTheme {
                CreatureScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("rollDescriptorButton").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun tapRollAbilityAddsAbility() {
        composeTestRule.setContent {
            KlerosTheme {
                CreatureScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("rollAbilityButton").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun tapNewBehaviorSetsNewBehavior() {
        composeTestRule.setContent {
            KlerosTheme {
                CreatureScreen()
            }
        }

        composeTestRule.onNodeWithTag("generateButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("newBehaviorButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("newBehaviorLabel").assertIsDisplayed()
    }

    @Test
    fun generateThreeTimesShowsThreeHistoryItems() {
        composeTestRule.setContent {
            KlerosTheme {
                CreatureScreen()
            }
        }

        repeat(3) {
            composeTestRule.onNodeWithTag("generateButton").performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onAllNodesWithTag("historyItem").assertCountEquals(3)
    }
}
