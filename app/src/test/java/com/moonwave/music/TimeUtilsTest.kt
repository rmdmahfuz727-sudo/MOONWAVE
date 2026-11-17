package com.moonwave.music

import com.moonwave.music.core.util.TimeUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun testFormatDurationSeconds() {
        assertEquals("00:00", TimeUtils.formatDuration(0))
        assertEquals("00:45", TimeUtils.formatDuration(45_000))
        assertEquals("03:25", TimeUtils.formatDuration(205_000))
    }

    @Test
    fun testFormatDurationHours() {
        // 1 hour, 15 minutes, 30 seconds = (3600 + 900 + 30) * 1000 = 4530000 ms
        assertEquals("1:15:30", TimeUtils.formatDuration(4_530_000))
    }

    @Test
    fun testNegativeDurationReturnsZero() {
        assertEquals("00:00", TimeUtils.formatDuration(-5000))
    }
}
