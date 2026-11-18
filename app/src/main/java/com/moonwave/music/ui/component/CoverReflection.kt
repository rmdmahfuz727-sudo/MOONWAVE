package com.moonwave.music.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import kotlin.math.max
import kotlin.math.roundToInt

private const val ReflectionStrength = 0.92f
private const val ReflectionFadeEnd = 0.9f
private const val MeshRows = 32

/**
 * Apple Music / Mochy style glossy artwork reflection.
 * Draws an edge-to-edge mirror of the cover fading into the dark background beneath the player.
 */
@Composable
fun CoverReflection(
    artworkUrl: String?,
    coverHeight: () -> Float,
    magnify: () -> Float = { 1.0f },
    alpha: () -> Float = { 0.65f },
    modifier: Modifier = Modifier,
    steady: Boolean = false,
) {
    if (artworkUrl == null) return
    val context = LocalContext.current
    val request = remember(context, artworkUrl) {
        ImageRequest.Builder(context)
            .data(artworkUrl)
            .allowHardware(false)
            .size(128, 128)
            .crossfade(false)
            .build()
    }
    val picture by produceState<Bitmap?>(null, request) {
        value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            runCatching {
                val result = context.imageLoader.execute(request)
                val raw = result.drawable?.toBitmap(96, 96)
                raw?.let { blurBitmap(it, 5) }
            }.getOrNull()
        }
    }
    val mesh = remember { ReflectionMesh() }
    Spacer(
        modifier = modifier.drawBehind {
            val bitmap = picture ?: return@drawBehind
            val a = alpha().coerceIn(0f, 1f)
            if (a <= 0.004f) return@drawBehind
            mesh.draw(this, bitmap, coverHeight(), magnify(), a, steady)
        }
    )
}

private fun blurBitmap(input: Bitmap, radius: Int): Bitmap {
    if (radius <= 0) return input
    val width = input.width
    val height = input.height
    if (width <= 0 || height <= 0) return input

    val pixels = IntArray(width * height)
    input.getPixels(pixels, 0, width, 0, 0, width, height)

    repeat(3) {
        boxBlurHorizontal(pixels, width, height, radius)
        boxBlurVertical(pixels, width, height, radius)
    }

    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    output.setPixels(pixels, 0, width, 0, 0, width, height)
    return output
}

private fun boxBlurHorizontal(pixels: IntArray, width: Int, height: Int, radius: Int) {
    val row = IntArray(width)
    for (y in 0 until height) {
        val rowStart = y * width
        for (x in 0 until width) row[x] = pixels[rowStart + x]
        for (x in 0 until width) {
            var a = 0; var r = 0; var g = 0; var b = 0; var count = 0
            val from = (x - radius).coerceAtLeast(0)
            val to = (x + radius).coerceAtMost(width - 1)
            for (i in from..to) {
                val p = row[i]
                a += (p ushr 24) and 0xFF
                r += (p ushr 16) and 0xFF
                g += (p ushr 8) and 0xFF
                b += p and 0xFF
                count++
            }
            pixels[rowStart + x] = ((a / count) shl 24) or ((r / count) shl 16) or ((g / count) shl 8) or (b / count)
        }
    }
}

private fun boxBlurVertical(pixels: IntArray, width: Int, height: Int, radius: Int) {
    val col = IntArray(height)
    for (x in 0 until width) {
        for (y in 0 until height) col[y] = pixels[y * width + x]
        for (y in 0 until height) {
            var a = 0; var r = 0; var g = 0; var b = 0; var count = 0
            val from = (y - radius).coerceAtLeast(0)
            val to = (y + radius).coerceAtMost(height - 1)
            for (i in from..to) {
                val p = col[i]
                a += (p ushr 24) and 0xFF
                r += (p ushr 16) and 0xFF
                g += (p ushr 8) and 0xFF
                b += p and 0xFF
                count++
            }
            pixels[y * width + x] = ((a / count) shl 24) or ((r / count) shl 16) or ((g / count) shl 8) or (b / count)
        }
    }
}

private class ReflectionMesh {
    private val verts = FloatArray((MeshRows + 1) * 2 * 2)
    private val colors = IntArray((MeshRows + 1) * 2)
    private val paint = android.graphics.Paint().apply { isFilterBitmap = true }

    fun draw(scope: DrawScope, bitmap: Bitmap, coverHeight: Float, magnify: Float, alpha: Float, steady: Boolean): Unit = with(scope) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f || coverHeight <= 0f) return
        val bw = bitmap.width.toFloat()
        val bh = bitmap.height.toFloat()

        val fit = max(w / bw, coverHeight / bh)
        val srcLeft = (bw - w / fit) / 2f
        val srcBottom = (bh + coverHeight / fit) / 2f
        val scale = fit * magnify

        for (row in 0..MeshRows) {
            val v = bh * row / MeshRows
            val y = (srcBottom - v) * scale
            val fade = if (steady) alpha else fadeAt(y / h) * alpha
            val colour = android.graphics.Color.argb((fade * 255f).roundToInt().coerceIn(0, 255), 255, 255, 255)
            for (col in 0..1) {
                val u = bw * col
                val x = w / 2f + ((u - srcLeft) * fit - w / 2f) * magnify
                val i = row * 2 + col
                verts[i * 2] = x
                verts[i * 2 + 1] = y
                colors[i] = colour
            }
        }
        drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas
            val saved = native.save()
            native.clipRect(0f, 0f, w, h)
            native.drawBitmapMesh(bitmap, 1, MeshRows, verts, 0, colors, 0, paint)
            native.restoreToCount(saved)
        }
    }

    private fun fadeAt(t: Float): Float {
        val x = (t.coerceIn(0f, 1f) / ReflectionFadeEnd).coerceIn(0f, 1f)
        val eased = x * x * (3f - 2f * x)
        return ReflectionStrength * (1f - eased)
    }
}
