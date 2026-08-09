package com.example.scrolldebt

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.scrolldebt.utils.TimeFormatUtils
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimeFormatUtilsTest {

    @Test
    fun testFormatSmartTime_returnsNonEmptyString() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Given
        val minutes = 150L // 2h 30m
        
        // When
        val result = TimeFormatUtils.formatSmartTime(appContext, minutes, "en")
        
        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }
}
