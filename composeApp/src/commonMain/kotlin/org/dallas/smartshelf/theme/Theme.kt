package org.dallas.smartshelf.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightThemeColors = lightColorScheme(

    primary = smart_shelf_them_light_primary,
    onPrimary = smart_shelf_them_light_onPrimary,
    primaryContainer = smart_shelf_them_light_primaryContainer,
    onPrimaryContainer = smart_shelf_them_light_onPrimaryContainer,
    secondary = smart_shelf_them_light_secondary,
    onSecondary = smart_shelf_them_light_onSecondary,
    secondaryContainer = smart_shelf_them_light_secondaryContainer,
    onSecondaryContainer = smart_shelf_them_light_onSecondaryContainer,
    tertiary = smart_shelf_them_light_tertiary,
    onTertiary = smart_shelf_them_light_onTertiary,
    tertiaryContainer = smart_shelf_them_light_tertiaryContainer,
    onTertiaryContainer = smart_shelf_them_light_onTertiaryContainer,
    error = smart_shelf_them_light_error,
    errorContainer = smart_shelf_them_light_errorContainer,
    onError = smart_shelf_them_light_onError,
    onErrorContainer = smart_shelf_them_light_onErrorContainer,
    background = smart_shelf_them_light_background,
    onBackground = smart_shelf_them_light_onBackground,
    surface = smart_shelf_them_light_surface,
    onSurface = smart_shelf_them_light_onSurface,
    surfaceVariant = smart_shelf_them_light_surfaceVariant,
    onSurfaceVariant = smart_shelf_them_light_onSurfaceVariant,
    outline = smart_shelf_them_light_outline,
    inverseOnSurface = smart_shelf_them_light_inverseOnSurface,
    inverseSurface = smart_shelf_them_light_inverseSurface,
    surfaceContainer = smart_shelf_them_light_jar_cap
)
private val DarkThemeColors = darkColorScheme(

    primary = smart_shelf_them_dark_primary,
    onPrimary = smart_shelf_them_dark_onPrimary,
    primaryContainer = smart_shelf_them_dark_primaryContainer,
    onPrimaryContainer = smart_shelf_them_dark_onPrimaryContainer,
    secondary = smart_shelf_them_dark_secondary,
    onSecondary = smart_shelf_them_dark_onSecondary,
    secondaryContainer = smart_shelf_them_dark_secondaryContainer,
    onSecondaryContainer = smart_shelf_them_dark_onSecondaryContainer,
    tertiary = smart_shelf_them_dark_tertiary,
    onTertiary = smart_shelf_them_dark_onTertiary,
    tertiaryContainer = smart_shelf_them_dark_tertiaryContainer,
    onTertiaryContainer = smart_shelf_them_dark_onTertiaryContainer,
    error = smart_shelf_them_dark_error,
    errorContainer = smart_shelf_them_dark_errorContainer,
    onError = smart_shelf_them_dark_onError,
    onErrorContainer = smart_shelf_them_dark_onErrorContainer,
    background = smart_shelf_them_dark_background,
    onBackground = smart_shelf_them_dark_onBackground,
    surface = smart_shelf_them_dark_surface,
    onSurface = smart_shelf_them_dark_onSurface,
    surfaceVariant = smart_shelf_them_dark_surfaceVariant,
    onSurfaceVariant = smart_shelf_them_dark_onSurfaceVariant,
    outline = smart_shelf_them_dark_outline,
    inverseOnSurface = smart_shelf_them_dark_inverseOnSurface,
    inverseSurface = smart_shelf_them_dark_inverseSurface,
    surfaceContainer = smart_shelf_them_dark_jar_cap
)

@Composable
fun SmartShelfTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkThemeColors else LightThemeColors
    val dimens = Dimens()

    CompositionLocalProvider(
        LocalDimens provides dimens
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = SmartShelfTypography,
            shapes = Shapes,
            content = content
        )
    }
}