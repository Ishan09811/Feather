package io.github.feather_browser.feather.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun FeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if (!darkTheme) {
        lightColorScheme()
    } else {
        darkColorScheme()
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}