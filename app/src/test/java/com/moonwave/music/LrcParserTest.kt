package com.moonwave.music

import com.moonwave.music.core.network.LrclibClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LrcParserTest {

    private val client = LrclibClient()

    @Test
    fun testParseLrcValidLines() {
        val rawLrc = """
            [00:10.50]Moonlight over the city
            [00:25.100]Electric rhythm taking flight
            [01:05.00]Cosmic waves through the night
        """.trimIndent()

        val lines = client.parseLrc(rawLrc)

        assertEquals(3, lines.size)
        assertEquals(10500L, lines[0].timestampMs)
        assertEquals("Moonlight over the city", lines[0].text)
        assertEquals(25100L, lines[1].timestampMs)
        assertEquals("Electric rhythm taking flight", lines[1].text)
        assertEquals(65000L, lines[2].timestampMs)
        assertEquals("Cosmic waves through the night", lines[2].text)
    }

    @Test
    fun testParseLrcIgnoresInvalidLines() {
        val rawLrc = """
            [ar:Moonwave]
            [ti:Cosmic Rhythm]
            [00:15.00]Valid lyric
            Just a random note without timestamp
        """.trimIndent()

        val lines = client.parseLrc(rawLrc)

        assertEquals(1, lines.size)
        assertEquals(15000L, lines[0].timestampMs)
        assertEquals("Valid lyric", lines[0].text)
    }

    @Test
    fun testParseLrcEmptyReturnsEmptyList() {
        val lines = client.parseLrc("")
        assertTrue(lines.isEmpty())
    }
}
