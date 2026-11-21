package com.moonwave.music.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moonwave.music.ui.theme.observeKey

/**
 * Moonwave Music style floating pill navigation bar.
 * Features a frosted stadium container with spring-animated gliding indicator capsule,
 * 3D bounce feedback, and fluid active tab highlights.
 */
@Composable
fun MoonwaveNavigationBar(
    items: List<Triple<String, String, Int>>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    hideNavLabels: Boolean = false,
    compact: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val barShape = RoundedCornerShape(32.dp)
    val pillShape = RoundedCornerShape(22.dp)
    val barHeight = if (compact) 56.dp else 64.dp
    val iconSize = if (compact) 21.dp else 24.dp

    val context = androidx.compose.ui.platform.LocalContext.current
    val appearancePrefs = androidx.compose.runtime.remember(context) {
        context.getSharedPreferences(AppearancePrefs.PREFS, android.content.Context.MODE_PRIVATE)
    }
    val frostedEnabled by appearancePrefs.observeKey(AppearancePrefs.FROSTED_GLASS, true)
    val solidState by appearancePrefs.observeKey(AppearancePrefs.SOLID_STATE_SURFACES, false)

    // The floating bar must never read as a black slab. In dark mode we lift the theme surface
    // toward white and use a translucent glass tint; in light mode a soft white veil keeps the
    // content underneath visible. Solid-state mode is lifted too, so it stays a premium surface.
    val isDark = isSystemInDarkTheme()
    val liftedSurface = if (isDark) {
        lerp(MaterialTheme.colorScheme.surfaceContainerHigh, Color.White, 0.16f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val surfaceColor = when {
        solidState -> liftedSurface.copy(alpha = 0.96f)
        isDark -> Color.White.copy(alpha = 0.13f)
        else -> Color.White.copy(alpha = 0.60f)
    }
    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.20f)
    } else {
        Color.White.copy(alpha = 0.55f)
    }
    val activePillColor = if (isDark) {
        Color.White.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.62f)
    }

    val selectedIndex = items.indexOfFirst { it.first == currentRoute }.let { if (it >= 0) it else 0 }
    val animatedPillIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "MoonwaveNavPillPosition"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        val barSurfaceModifier = if (frostedEnabled && !solidState) {
            Modifier.frostedGlass(
                shape = barShape,
                tint = surfaceColor,
                borderColor = borderColor,
                elevation = 14.dp
            )
        } else {
            Modifier
                .clip(barShape)
                .background(surfaceColor)
                .border(1.dp, borderColor, barShape)
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .then(barSurfaceModifier)
                .press3D(maxTilt = 3f, pressedScale = 0.99f)
        ) {
            val totalWidth = maxWidth
            val count = items.size.coerceAtLeast(1)
            val slotWidth = totalWidth / count
            val pillInsetX = 4.dp
            val pillInsetY = if (compact) 5.dp else 6.dp

            // Gliding Spring Indicator Capsule
            Box(
                modifier = Modifier
                    .offset(x = slotWidth * animatedPillIndex + pillInsetX, y = pillInsetY)
                    .width(slotWidth - pillInsetX * 2)
                    .height(barHeight - pillInsetY * 2)
                    .clip(pillShape)
                    .background(activePillColor)
            )

            // Navigation Items Row
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, (route, label, iconResId) ->
                    val isSelected = index == selectedIndex
                    val interactionSource = remember { MutableInteractionSource() }

                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
                        label = "NavIconScale"
                    )

                    val activeAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1.0f else 0.65f,
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = 600f),
                        label = "NavAlpha"
                    )

                    val iconColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onItemClick(route)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                                alpha = activeAlpha
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = iconResId),
                                contentDescription = label,
                                tint = iconColor,
                                modifier = Modifier.size(iconSize)
                            )
                            if (!hideNavLabels) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = iconColor,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
