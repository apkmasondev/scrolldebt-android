package com.example.scrolldebt.domain.usecases

import com.example.scrolldebt.utils.AppUsageInfo
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BrutalTruthEngineTest {

    private lateinit var engine: BrutalTruthEngine

    @Before
    fun setup() {
        engine = BrutalTruthEngine()
    }

    @Test
    fun `getRoastMessage returns non-empty message for low usage`() {
        val message = engine.getRoastMessage(
            totalTimeMs = 15 * 60 * 1000L, // 15 mins
            breakdown = emptyList(),
            language = "en"
        )
        assertNotNull(message)
        assertTrue(message.isNotBlank())
    }

    @Test
    fun `getRoastMessage returns different messages on consecutive calls`() {
        val totalMs = 30 * 60 * 1000L
        val msg1 = engine.getRoastMessage(totalMs, emptyList(), "en")
        val msg2 = engine.getRoastMessage(totalMs, emptyList(), "en")
        
        // It's technically possible but very unlikely to get the same message
        // since we remove them from the pool via seenQuotes
        assertNotEquals("Consecutive calls should not return the exact same message", msg1, msg2)
    }

    @Test
    fun `getRoastMessage supports fallback languages`() {
        // "pl" is default
        val message = engine.getRoastMessage(
            totalTimeMs = 120 * 60 * 1000L,
            breakdown = emptyList(),
            language = "xyz" // unsupported
        )
        assertNotNull(message)
        assertTrue(message.isNotBlank())
    }

    @Test
    fun `getWeeklyRoast returns valid message based on hours`() {
        val messageLevel1 = engine.getWeeklyRoast(5.0, "en")
        val messageLevel5 = engine.getWeeklyRoast(55.0, "en")

        assertNotNull(messageLevel1)
        assertNotNull(messageLevel5)
        assertTrue(messageLevel5.isNotBlank())
    }
}
