package com.example.finalprojectfinance.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme

// your custom greens & neutrals
val LightGreen       = Color(0xFF66BB6A)
val DarkGreen        = Color(0xFF2E7D32)
val BlackText        = Color(0xFF000000)
val WhiteText        = Color(0xFFFFFFFF)
val BackgroundLight  = Color(0xFFFFFFFF)  // pure white bg
val SurfaceLight     = Color(0xFFF1F8E9)  // very pale green for cards

val LightColorScheme = lightColorScheme(
    // core
    primary            = LightGreen,
    onPrimary          = WhiteText,

    secondary          = DarkGreen,
    onSecondary        = WhiteText,

    background         = BackgroundLight,
    onBackground       = BlackText,

    // this is what your Cards will use by default
    surface            = SurfaceLight,
    onSurface          = BlackText,

    // override the “variant” tone (Cards, ListItems, etc.)
    surfaceVariant     = SurfaceLight,
    onSurfaceVariant   = BlackText,

    // override the container tone (TopAppBar, NavigationBar, FAB ripple, etc.)
    primaryContainer   = LightGreen,
    onPrimaryContainer = WhiteText,

    secondaryContainer   = DarkGreen,
    onSecondaryContainer = WhiteText,

    // you can also override tertiary if you later use it
    tertiary           = LightGreen,
    onTertiary         = WhiteText,
    tertiaryContainer  = DarkGreen,
    onTertiaryContainer= WhiteText,
)
