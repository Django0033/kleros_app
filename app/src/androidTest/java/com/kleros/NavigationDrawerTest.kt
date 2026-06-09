package com.kleros

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
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
class NavigationDrawerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun hamburgerIconOpensDrawer() {
        composeTestRule.setContent {
            KlerosTheme {
                AppNavigation()
            }
        }

        composeTestRule.onNodeWithTag("navDrawerHamburger").performClick()
        composeTestRule.onNodeWithText("Dice Roll").assertIsDisplayed()
    }

    @Test
    fun selectingDrawerItemSwitchesScreen() {
        composeTestRule.setContent {
            KlerosTheme {
                AppNavigation()
            }
        }

        composeTestRule.onNodeWithTag("navDrawerHamburger").performClick()
        composeTestRule.onNodeWithText("Meaning").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("rollButton").assertIsDisplayed()
    }

    @Test
    fun drawerClosesAfterItemSelection() {
        composeTestRule.setContent {
            KlerosTheme {
                AppNavigation()
            }
        }

        composeTestRule.onNodeWithTag("navDrawerHamburger").performClick()
        composeTestRule.onNodeWithText("Meaning").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Dice Roll").assertIsNotDisplayed()
    }
}
