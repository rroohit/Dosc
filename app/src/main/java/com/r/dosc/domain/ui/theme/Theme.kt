package com.r.dosc.domain.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

val DarkColorPalette = darkColors(
    primary = Ocean_Red,
    primaryVariant = Desert_Storm,
    secondary = Ocean_Red_2,
    onPrimary = Dark_1,

    )

private val LightColorPalette = lightColors(
    primary = Ocean_Red,
    primaryVariant = Salem,
    secondary = Desert_Storm,
    secondaryVariant = Ocean_Red,
)

@Composable
fun DoscTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}