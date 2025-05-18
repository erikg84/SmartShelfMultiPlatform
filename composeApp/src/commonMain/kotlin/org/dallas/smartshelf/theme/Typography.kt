package org.dallas.smartshelf.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi

// In multiplatform, we'll use the default font family for now
// Later we can add platform-specific font implementations
private val DefaultFontFamily = FontFamily.Default

@OptIn(ExperimentalResourceApi::class)
val SmartShelfTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 28.sp
    ),
    displayMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp
    ),
    displaySmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp
    ),
    titleMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontSize = 14.sp
    ),
)