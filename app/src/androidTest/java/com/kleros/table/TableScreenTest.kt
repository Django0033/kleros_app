@file:Suppress("MagicNumber")

package com.kleros.table

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
class TableScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTables = listOf(
        TableDef(
            name = "Loot",
            entries = listOf(
                TableEntry.RANGE(min = 1, max = 10, result = "Gold"),
                TableEntry.RANGE(min = 11, max = 20, result = "Silver"),
            ),
        ),
        TableDef(
            name = "Enemies",
            entries = listOf(
                TableEntry.RANGE(min = 1, max = 10, result = "Goblin"),
                TableEntry.RANGE(min = 11, max = 20, result = "Orc"),
            ),
        ),
        TableDef(
            name = "Weather",
            entries = listOf(
                TableEntry.RANGE(min = 1, max = 10, result = "Sunny"),
                TableEntry.RANGE(min = 11, max = 20, result = "Rainy"),
            ),
        ),
    )

    @Test
    fun screenRendersFilterChipsForEachTable() {
        composeTestRule.setContent {
            KlerosTheme {
                TableScreen(tables = testTables)
            }
        }

        composeTestRule.onNodeWithTag("tableSelector_Loot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tableSelector_Enemies").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tableSelector_Weather").assertIsDisplayed()
    }

    @Test
    fun tapFilterChipSelectsThatTable() {
        composeTestRule.setContent {
            KlerosTheme {
                TableScreen(tables = testTables)
            }
        }

        composeTestRule.onNodeWithTag("tableSelector_Enemies").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("tableSelector_Enemies").assertIsDisplayed()
    }

    @Test
    fun tapRollButtonDisplaysResult() {
        composeTestRule.setContent {
            KlerosTheme {
                TableScreen(tables = testTables)
            }
        }

        composeTestRule.onNodeWithTag("rollButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("rollResult").assertIsDisplayed()
    }

    @Test
    fun rollMultipleTimesShowsHistoryEntries() {
        composeTestRule.setContent {
            KlerosTheme {
                TableScreen(tables = testTables)
            }
        }

        repeat(3) {
            composeTestRule.onNodeWithTag("rollButton").performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onAllNodesWithTag("historyItem").assertCountEquals(3)
    }
}
