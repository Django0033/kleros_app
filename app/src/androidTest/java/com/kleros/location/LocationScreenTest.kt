package com.kleros.location

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
class LocationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersRegionChips() {
        composeTestRule.setContent {
            KlerosTheme {
                LocationScreen()
            }
        }

        composeTestRule.onNodeWithTag("regionChip_SMALL").assertIsDisplayed()
        composeTestRule.onNodeWithTag("regionChip_AVERAGE").assertIsDisplayed()
        composeTestRule.onNodeWithTag("regionChip_LARGE").assertIsDisplayed()
    }

    @Test
    fun screenShowsRollElementButton() {
        composeTestRule.setContent {
            KlerosTheme {
                LocationScreen()
            }
        }

        composeTestRule.onNodeWithTag("rollElementButton").assertIsDisplayed()
    }

    @Test
    fun selectingRegionShowsChipAsSelected() {
        composeTestRule.setContent {
            KlerosTheme {
                LocationScreen()
            }
        }

        composeTestRule.onNodeWithTag("regionChip_SMALL").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("PP: 3").assertIsDisplayed()
    }

    @Test
    fun rollingElementShowsResult() {
        composeTestRule.setContent {
            KlerosTheme {
                LocationScreen()
            }
        }

        composeTestRule.onNodeWithTag("regionChip_SMALL").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("rollElementButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("elementResult").assertIsDisplayed()
    }

    @Test
    fun rollingDescriptorShowsResult() {
        composeTestRule.setContent {
            KlerosTheme {
                LocationScreen()
            }
        }

        composeTestRule.onNodeWithTag("regionChip_SMALL").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("rollDescriptorButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("descriptorResult").assertIsDisplayed()
    }
}
