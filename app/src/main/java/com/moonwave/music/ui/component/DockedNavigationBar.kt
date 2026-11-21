package com.moonwave.music.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moonwave.music.ui.theme.observeKey

/**
 * Classic full-width bottom navigation bar ("Docked" style in Appearance settings).
 *
 * Unlike the floating pill bar it spans the whole screen edge, keeps a hairline divider and
 * respects the Frosted Glass / Solid State Surface appearance preferences.
 */
@Composable
fun DockedNavigationBar(
    items: List<Triple<String, String, Int>>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    hideNavLabels: Boolean = false,
    compact: Boolean = false
) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences(AppearancePrefs.PREFS, android.content.Context.MODE_PRIVATE)
    }
    val frostedEnabled by prefs.observeKey(AppearancePrefs.FROSTED_GLASS, true)
    val solidState by prefs.observeKey(AppearancePrefs.SOLID_STATE_SURFACES, false)

    val haptic = LocalHapticFeedback.current
    val selectedIndex = items.indexOfFirst { it.first == currentRoute }.let { if (it >= 0) it else 0 }
    val barHeight = if (compact) 64.dp else 76.dp

    // The chrome must read as a glass surface, never as an opaque black slab. In dark mode use a
    // translucent light tint so the screen behind stays visible through the frost; in solid state
    // lift the theme surface instead of falling back to its darkest container colour.
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val surfaceColor = when {
        solidState -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(
            alpha = if (isDark) 0.92f else 0.96f
        )
        isDark -> Color.White.copy(alpha = if (frostedEnabled) 0.10f else 0.14f)
        else -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(
            alpha = if (frostedEnabled) 0.82f else 0.94f
        )
    }
    // Classic docked bar: spans the screen but every corner is rounded. The small horizontal
    // inset makes the rounded bottom edge visible above the system gesture area and keeps the
    // bar looking like a single premium surface in both frosted and solid-state modes.
    val barShape = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 22.dp,
        bottomEnd = 22.dp
    )
    val barModifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .then(
            if (frostedEnabled && !solidState) {
                Modifier.frostedGlass(
                    shape = barShape,
                    tint = surfaceColor,
                    elevation = 10.dp
                )
            } else {
                Modifier
                    .clip(barShape)
                    .background(surfaceColor)
            }
        )

    Row(
        modifier = barModifier.height(barHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEachIndexed { index, (route, label, iconResId) ->
            val isSelected = index == selectedIndex
            val interactionSource = remember { MutableInteractionSource() }
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.08f else 1f,
                animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
                label = "DockedNavIconScale"
            )
            val iconColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onItemClick(route)
                        }
                    )
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 32.dp else 36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
                            else Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = label,
                        tint = iconColor,
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                            }
                    )
                }
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
