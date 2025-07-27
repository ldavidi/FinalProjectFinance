package com.example.finalprojectfinance.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val LightColors = lightColorScheme(
    primary         = LightGreen,
    onPrimary       = WhiteText,
    secondary       = DarkGreen,
    onSecondary     = WhiteText,
    background      = BackgroundLight,
    onBackground    = BlackText,
    surface         = SurfaceLight,
    onSurface       = BlackText,
    // you can override other roles as you like…
)

@Composable
fun FinalProjectFinanceTheme(
    useDarkTheme: Boolean = false, // force light
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        shapes = androidx.compose.material3.Shapes(),
        content = content
    )
}
