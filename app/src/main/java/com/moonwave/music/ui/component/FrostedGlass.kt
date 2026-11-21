package com.moonwave.music.ui.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Frosted surface styling used by MOONWAVE's floating chrome.
 *
 * The appearance is a translucent tint, a hairline highlight, a soft shadow and — on Android 12+
 * — a blurred copy of whatever [backdrop] supplies. On older Android versions the blur modifier is
 * a no-op, so the tint and highlight keep the glass readable without a hard edge.
 */
object FrostedGlassDefaults {
    val Shape: Shape = RoundedCornerShape(24.dp)
    val Tint: Color = Color(0xFF121218).copy(alpha = 0.62f)
    val Border: Color = Color.White.copy(alpha = 0.14f)
    val BlurRadius: Dp = 18.dp
    val Elevation: Dp = 12.dp
}

/**
 * A lightweight frosted-glass modifier for chrome that sits directly over screen content
 * (for example the floating navigation pill). It does not capture the backdrop; it relies on a
 * translucent tint so the content underneath still shows through.
 */
fun Modifier.frostedGlass(
    shape: Shape = FrostedGlassDefaults.Shape,
    tint: Color = FrostedGlassDefaults.Tint,
    borderColor: Color = FrostedGlassDefaults.Border,
    borderWidth: Dp = 1.dp,
    elevation: Dp = FrostedGlassDefaults.Elevation
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = Color.Black.copy(alpha = 0.18f),
        spotColor = Color.Black.copy(alpha = 0.32f)
    )
    .clip(shape)
    .background(tint)
    .background(
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.07f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.05f)
            )
        )
    )
    .border(width = borderWidth, color = borderColor, shape = shape)

/**
 * Frosted panel with an optional blurred backdrop. Supplying a backdrop (album art, a colour
 * wash, etc.) is what makes the glass show real blur on Android 12 and above.
 */
@Composable
fun FrostedGlass(
    modifier: Modifier = Modifier,
    shape: Shape = FrostedGlassDefaults.Shape,
    tint: Color = FrostedGlassDefaults.Tint,
    blurRadius: Dp = FrostedGlassDefaults.BlurRadius,
    borderColor: Color = FrostedGlassDefaults.Border,
    borderWidth: Dp = 1.dp,
    elevation: Dp = FrostedGlassDefaults.Elevation,
    backdrop: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val canBlur = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.16f),
                spotColor = Color.Black.copy(alpha = 0.28f)
            )
            .clip(shape)
    ) {
        if (backdrop != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .then(if (canBlur) Modifier.blur(blurRadius) else Modifier)
                    .graphicsLayer {
                        // Scaling hides the soft edges produced by the blur at the panel border.
                        scaleX = 1.08f
                        scaleY = 1.08f
                        alpha = 0.9f
                    },
                content = backdrop
            )
        }

        Box(modifier = Modifier.fillMaxSize().background(tint))
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.05f)
                    )
                )
            )
        )
        Box(modifier = Modifier.fillMaxSize().border(borderWidth, borderColor, shape))

        content()
    }
}
