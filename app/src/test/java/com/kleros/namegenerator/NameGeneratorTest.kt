package com.kleros.namegenerator

import com.kleros.dice.DiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NameGeneratorTest {

    // -- Pattern parsing verification via controlled rolls --

    @Test
    fun `controlled rolls produce expected name from pattern 12o row 1`() {
        val rollValues = mutableListOf(
            1,  // pattern roll → row 1, pattern=12o
            1,  // INICIO1 → row 1, (f)a → first → "a"
            1,  // INICIO2 → row 1, hal
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL, rollFn = rollFn)
        // Result: "a" + "hal" + "o" = "ahalo"
        assertEquals("ahalo", result.name)
    }

    @Test
    fun `controlled rolls produce expected name from pattern 23- row 6`() {
        val rollValues = mutableListOf(
            6,  // pattern roll → row 6, pattern=23-
            6,  // INICIO2 → row 6, net
            6,  // ENDING(FIRST_HALF) → (6+1)/2=3 → row 3 ending=er
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL, rollFn = rollFn)
        // Result: "net" + "er" = "neter"
        assertEquals("neter", result.name)
    }

    @Test
    fun `pattern 123-plus with row 20`() {
        val rollValues = mutableListOf(
            20, // pattern roll → row 20, pattern=123+
            20, // INICIO1 → row 20, to
            20, // INICIO2 → row 20, kic
            20, // ENDING(LAST_HALF) → (20+1)/2+10=20 → row 20 ending=osa
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL, rollFn = rollFn)
        assertEquals("tokicosa", result.name)
    }

    @Test
    fun `pattern 111 uses inicio1 three times`() {
        val rollValues = mutableListOf(
            11, // pattern roll → row 11, pattern=111
            11, // INICIO1 → row 11, be
            11, // INICIO1 → row 11, be
            11, // INICIO1 → row 11, be
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL, rollFn = rollFn)
        assertEquals("bebebe", result.name)
    }

    @Test
    fun `parenthetical prefix (f)a resolves to a when first syllable`() {
        val rollValues = mutableListOf(
            1,  // pattern roll → row 1, pattern=12o
            1,  // INICIO1 → row 1, (f)a, first → "a"
            1,  // INICIO2 → row 1, hal
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL, rollFn = rollFn)
        assertEquals("ahalo", result.name)
    }

    @Test
    fun `parenthetical prefix (f)a resolves to fa when not first syllable`() {
        // Row 10: pattern=111, inicio1=ro
        // Force all INICIO1 reads to return row 1's inicio1=(f)a
        // First INICIO1: (f)a, first → "a"
        // Second INICIO1: (f)a, not first → "fa"
        // Third INICIO1: (f)a, not first → "fa"
        val rollValues = mutableListOf(
            10, // pattern roll → row 10, pattern=111
            1,  // INICIO1 → row 1, (f)a, first → "a"
            1,  // INICIO1 → row 1, (f)a, not first → "fa"
            1,  // INICIO1 → row 1, (f)a, not first → "fa"
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL, rollFn = rollFn)
        assertEquals("afafa", result.name)
    }

    @Test
    fun `non-parenthetical cells are used as-is`() {
        val rollValues = mutableListOf(
            7,  // pattern roll → row 7, pattern=123-o
            7,  // INICIO1 → row 7, ka
            7,  // INICIO2 → row 7, kel
            7,  // ENDING(FIRST_HALF) → (7+1)/2=4 → row 4 ending=ian
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL, rollFn = rollFn)
        // ka + kel + ian + o = kakeliano
        assertTrue(result.name.startsWith("kakel"))
        assertTrue(result.name.endsWith("iano"))
    }

    // -- Advantage / Disadvantage (pattern row selection) --

    @Test
    fun `advantage picks higher for pattern roll`() {
        // Uses ADVANTAGE → pattern roll consumes 2 calls: max(1, 20) = 20
        // Then syllable rolls use subsequent calls
        val rollValues = mutableListOf(
            1, 20, // pattern advantage rolls → max(1,20)=20 → row 20, pattern=123+
            20,    // INICIO1 → row 20, to
            20,    // INICIO2 → row 20, kic
            20,    // ENDING(LAST_HALF) → row 20 ending=osa
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.ADVANTAGE, rollFn = rollFn)
        assertEquals(RollMode.ADVANTAGE, result.rollMode)
        assertEquals("tokicosa", result.name)
    }

    @Test
    fun `disadvantage picks lower for pattern roll`() {
        // DISADVANTAGE → pattern roll consumes 2 calls: min(18, 3) = 3
        val rollValues = mutableListOf(
            18, 3, // pattern disadvantage rolls → min(18,3)=3 → row 3, pattern=12
            3,     // INICIO1 → row 3, (v)i, first → "i"
            3,     // INICIO2 → row 3, del
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.DISADVANTAGE, rollFn = rollFn)
        assertEquals(RollMode.DISADVANTAGE, result.rollMode)
        assertEquals("idel", result.name)
    }

    // -- Uniqueness --

    @Test
    fun `1000 invocations produce at least 50 unique names`() {
        val uniqueNames = mutableSetOf<String>()
        repeat(1000) {
            uniqueNames.add(NameGenerator.generate().name)
        }
        assertTrue(
            "Expected at least 50 unique names but got ${uniqueNames.size}",
            uniqueNames.size >= 50,
        )
    }

    // -- Non-empty result --

    @Test
    fun `generate returns non-empty string`() {
        val result = NameGenerator.generate()
        assertTrue("Name should be non-empty", result.name.isNotBlank())
    }

    @Test
    fun `generate returns NameResult with correct rollMode`() {
        val result = NameGenerator.generate(rollMode = RollMode.ADVANTAGE)
        assertEquals(RollMode.ADVANTAGE, result.rollMode)
    }

    // -- Range mapping tests --

    @Test
    fun `first half range rolls 1-10 for ending`() {
        val rollValues = mutableListOf(
            4,  // pattern roll → row 4, pattern=23-o
            4,  // INICIO2 → row 4, mor
            1,  // ENDING(FIRST_HALF) → (1+1)/2=1 → row 1 ending=an
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL, rollFn = rollFn)
        assertEquals("morano", result.name)
    }

    @Test
    fun `last half range rolls 11-20 for ending`() {
        val rollValues = mutableListOf(
            20, // pattern roll → row 20, pattern=123+
            20, // INICIO1 → row 20, to
            20, // INICIO2 → row 20, kic
            1,  // ENDING(LAST_HALF) → (1+1)/2+10=11 → row 11 ending=a
        )
        val rollFn: (DiceType) -> Int = { rollValues.removeFirst() }
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL, rollFn = rollFn)
        assertEquals("tokica", result.name)
    }

    // -- Default parameter tests --

    @Test
    fun `generate with only rollMode works with default rollFn`() {
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL)
        assertTrue("Name should be non-empty", result.name.isNotBlank())
    }

    @Test
    fun `generate with no arguments uses defaults`() {
        val result = NameGenerator.generate()
        assertTrue("Name should be non-empty", result.name.isNotBlank())
    }

    @Test
    fun `generate with NORMAL produces NameResult with NORMAL mode`() {
        val result = NameGenerator.generate(rollMode = RollMode.NORMAL)
        assertEquals(RollMode.NORMAL, result.rollMode)
    }

    @Test
    fun `1000 generated names completes in under 1 second`() {
        val start = System.currentTimeMillis()
        repeat(1000) {
            NameGenerator.generate()
        }
        val elapsed = System.currentTimeMillis() - start
        assertTrue("1000 name generations took $elapsed ms, expected < 1000 ms", elapsed < 1000)
    }
}
