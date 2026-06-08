package com.kleros.character

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterResultTest {

    @Test
    fun `constructor assigns all fields correctly`() {
        val result = CharacterResult(
            identity = "Academic",
            mind = "Body",
            body = "Brave",
            talent = "Charm",
            statistics = "What you expect",
        )
        assertEquals("Academic", result.identity)
        assertEquals("Body", result.mind)
        assertEquals("Brave", result.body)
        assertEquals("Charm", result.talent)
        assertEquals("What you expect", result.statistics)
    }

    @Test
    fun `timestampMillis is auto-populated and non-zero`() {
        val result = CharacterResult(
            identity = "Aggressive",
            mind = "Mind",
            body = "Body",
            talent = "Talent",
            statistics = "About 50% higher",
        )
        assertTrue("timestampMillis should be > 0", result.timestampMillis > 0L)
    }

    @Test
    fun `copy produces new instance with changed field`() {
        val original = CharacterResult(
            identity = "Dark",
            mind = "Mind",
            body = "Body",
            talent = "Talent",
            statistics = "About 25% lower",
        )
        val modified = original.copy(identity = "Light")
        assertNotEquals(original, modified)
        assertEquals("Light", modified.identity)
        assertEquals("Mind", modified.mind)
    }
}
