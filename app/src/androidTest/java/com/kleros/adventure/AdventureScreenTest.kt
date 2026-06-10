package com.kleros.adventure

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kleros.ui.theme.KlerosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdventureScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersRollButton() {
        composeTestRule.setContent {
            KlerosTheme {
                AdventureScreen()
            }
        }

        composeTestRule.onNodeWithTag("rollButton").assertIsDisplayed()
    }

    @Test
    fun actionTableChipIsPresent() {
        composeTestRule.setContent {
            KlerosTheme {
                AdventureScreen()
            }
        }

        composeTestRule.onNodeWithTag("themeChip_Action").assertIsDisplayed()
    }

    @Test
    fun tensionTableChipIsPresent() {
        composeTestRule.setContent {
            KlerosTheme {
                AdventureScreen()
            }
        }

        composeTestRule.onNodeWithTag("themeChip_Tension").assertIsDisplayed()
    }

    @Test
    fun mysteryTableChipIsPresent() {
        composeTestRule.setContent {
            KlerosTheme {
                AdventureScreen()
            }
        }

        composeTestRule.onNodeWithTag("themeChip_Mystery").assertIsDisplayed()
    }

    @Test
    fun socialTableChipIsPresent() {
        composeTestRule.setContent {
            KlerosTheme {
                AdventureScreen()
            }
        }

        composeTestRule.onNodeWithTag("themeChip_Social").assertIsDisplayed()
    }

    @Test
    fun personalTableChipIsPresent() {
        composeTestRule.setContent {
            KlerosTheme {
                AdventureScreen()
            }
        }

        composeTestRule.onNodeWithTag("themeChip_Personal").assertIsDisplayed()
    }

    @Test
    fun randomThemeButtonIsPresent() {
        composeTestRule.setContent {
            KlerosTheme {
                AdventureScreen()
            }
        }

        composeTestRule.onNodeWithTag("randomThemeButton").assertIsDisplayed()
    }
}
