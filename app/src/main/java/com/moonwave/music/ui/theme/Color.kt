package com.moonwave.music.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// MOONWAVE Signature Lunar Palette
// Visual Identity: Midnight black, Deep navy, Moonlight silver, Electric blue, Purple gradients
val Primary = Color(0xFF38BDF8) // Electric Moonlight Cyan
val OnPrimary = Color(0xFF032840)
val PrimaryContainer = Color(0xFF0C4A6E)
val OnPrimaryContainer = Color(0xFFBAE6FD)

val Secondary = Color(0xFF818CF8) // Cosmic Indigo
val OnSecondary = Color(0xFF1E1B4B)
val SecondaryContainer = Color(0xFF312E81)
val OnSecondaryContainer = Color(0xFFC7D2FE)

val Tertiary = Color(0xFFA78BFA) // Nebula Purple
val OnTertiary = Color(0xFF2E1065)
val TertiaryContainer = Color(0xFF581C87)
val OnTertiaryContainer = Color(0xFFE9D5FF)

val Error = Color(0xFFF87171)
val OnError = Color(0xFF450A0A)
val ErrorContainer = Color(0xFF7F1D1D)
val OnErrorContainer = Color(0xFFFECACA)

val Outline = Color(0xFF64748B)
val OutlineVariant = Color(0xFF334155)

val Surface = Color(0xFF0B0F19) // Midnight Obsidian Black
val OnSurface = Color(0xFFF1F5F9) // Starlight Silver
val SurfaceVariant = Color(0xFF1E293B) // Deep Navy Glass
val OnSurfaceVariant = Color(0xFF94A3B8)

val SurfaceContainerLowest = Color(0xFF070A12)
val SurfaceContainerLow = Color(0xFF0B1120)
val SurfaceContainer = Color(0xFF0F172A)
val SurfaceContainerHigh = Color(0xFF1E293B)
val SurfaceContainerHighest = Color(0xFF334155)

// Standard Android ARGB Ints for XML/Canvas/Legacy View compatibility
val PrimaryArgb: Int get() = Primary.toArgb()
val OnPrimaryArgb: Int get() = OnPrimary.toArgb()
val PrimaryContainerArgb: Int get() = PrimaryContainer.toArgb()
val OnPrimaryContainerArgb: Int get() = OnPrimaryContainer.toArgb()

val SecondaryArgb: Int get() = Secondary.toArgb()
val OnSecondaryArgb: Int get() = OnSecondary.toArgb()
val SecondaryContainerArgb: Int get() = SecondaryContainer.toArgb()
val OnSecondaryContainerArgb: Int get() = OnSecondaryContainer.toArgb()

val TertiaryArgb: Int get() = Tertiary.toArgb()

val SurfaceArgb: Int get() = Surface.toArgb()
val OnSurfaceArgb: Int get() = OnSurface.toArgb()
val SurfaceVariantArgb: Int get() = SurfaceVariant.toArgb()
val OnSurfaceVariantArgb: Int get() = OnSurfaceVariant.toArgb()

val SurfaceContainerLowArgb: Int get() = SurfaceContainerLow.toArgb()
val SurfaceContainerArgb: Int get() = SurfaceContainer.toArgb()
val SurfaceContainerHighArgb: Int get() = SurfaceContainerHigh.toArgb()
val SurfaceContainerHighestArgb: Int get() = SurfaceContainerHighest.toArgb()
