package com.moonwave.music.core.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.moonwave.music.core.model.LyricsLine
import com.moonwave.music.core.model.LyricsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class LrclibClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun fetchLyrics(
        trackName: String,
        artistName: String,
        albumName: String,
        durationSeconds: Int
    ): LyricsResult? = withContext(Dispatchers.IO) {
        // 1. Try exact get
        val urlBuilder = "https://lrclib.net/api/get".toHttpUrlOrNull()?.newBuilder()
            ?: return@withContext null

        urlBuilder.addQueryParameter("track_name", trackName)
        urlBuilder.addQueryParameter("artist_name", artistName)
        if (albumName.isNotEmpty() && albumName != "Unknown Album") {
            urlBuilder.addQueryParameter("album_name", albumName)
        }
        if (durationSeconds > 0) {
            urlBuilder.addQueryParameter("duration", durationSeconds.toString())
        }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .header("User-Agent", "MOONWAVE-Android/1.0.0 (https://github.com/rmdmahfuz727-sudo/MOONWAVE)")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: return@withContext null
                    val json = gson.fromJson(bodyString, JsonObject::class.java)
                    return@withContext parseLyricsJson(json)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Fallback to search if exact match failed
        searchLyrics("$trackName $artistName")
    }

    suspend fun searchLyrics(query: String): LyricsResult? = withContext(Dispatchers.IO) {
        val urlBuilder = "https://lrclib.net/api/search".toHttpUrlOrNull()?.newBuilder()
            ?: return@withContext null
        urlBuilder.addQueryParameter("q", query)

        val request = Request.Builder()
            .url(urlBuilder.build())
            .header("User-Agent", "MOONWAVE-Android/1.0.0")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: return@withContext null
                    val jsonArray = gson.fromJson(bodyString, com.google.gson.JsonArray::class.java)
                    if (jsonArray != null && jsonArray.size() > 0) {
                        val firstObj = jsonArray[0].asJsonObject
                        return@withContext parseLyricsJson(firstObj)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        null
    }

    private fun parseLyricsJson(json: JsonObject): LyricsResult {
        val syncedLyrics = json.get("syncedLyrics")?.let { if (!it.isJsonNull) it.asString else null }
        val plainLyrics = json.get("plainLyrics")?.let { if (!it.isJsonNull) it.asString else null } ?: ""

        if (!syncedLyrics.isNullOrEmpty()) {
            val lines = parseLrc(syncedLyrics)
            return LyricsResult(
                isSynced = true,
                lines = lines,
                plainLyrics = plainLyrics,
                source = "LRCLIB"
            )
        }

        return LyricsResult(
            isSynced = false,
            lines = emptyList(),
            plainLyrics = plainLyrics,
            source = "LRCLIB"
        )
    }

    fun parseLrc(rawLrc: String): List<LyricsLine> {
        val lines = mutableListOf<LyricsLine>()
        val regex = Regex("""\[(\d{2}):(\d{2})(?:\.(\d{2,3}))?](.*)""")

        rawLrc.lines().forEach { line ->
            val match = regex.find(line.trim())
            if (match != null) {
                val min = match.groupValues[1].toLongOrNull() ?: 0L
                val sec = match.groupValues[2].toLongOrNull() ?: 0L
                val msStr = match.groupValues[3]
                val ms = when (msStr.length) {
                    2 -> (msStr.toLongOrNull() ?: 0L) * 10
                    3 -> msStr.toLongOrNull() ?: 0L
                    else -> 0L
                }
                val totalMs = (min * 60 + sec) * 1000 + ms
                val text = match.groupValues[4].trim()
                if (text.isNotEmpty()) {
                    lines.add(LyricsLine(timestampMs = totalMs, text = text))
                }
            }
        }
        return lines.sortedBy { it.timestampMs }
    }
}
