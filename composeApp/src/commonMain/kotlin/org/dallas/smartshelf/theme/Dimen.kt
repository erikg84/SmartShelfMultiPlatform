package org.dallas.smartshelf.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.staticCompositionLocalOf


@Immutable
data class Dimens(
    val dp0: Dp = 0.dp,
    val dp2: Dp = 2.dp,
    val dp4: Dp = 4.dp,
    val dp8: Dp = 8.dp,
    val dp12: Dp = 12.dp,
    val dp16: Dp = 16.dp,
    val dp24: Dp = 24.dp,
    val dp32: Dp = 32.dp,
    val dp40: Dp = 40.dp,
    val dp48: Dp = 48.dp,
    val dp56: Dp = 56.dp,
    val dp64: Dp = 64.dp,
    val dp72: Dp = 72.dp,
    val dp80: Dp = 80.dp,
    val dp88: Dp = 88.dp,
    val dp96: Dp = 96.dp,

    val sp0: TextUnit = 0.sp,
    val sp8: TextUnit = 8.sp,
    val sp16: TextUnit = 16.sp,
    val sp24: TextUnit = 24.sp,
    val sp28: TextUnit = 28.sp,
    val sp32: TextUnit = 32.sp,
    val sp40: TextUnit = 40.sp,
    val sp48: TextUnit = 48.sp,
    val sp56: TextUnit = 56.sp,
    val sp64: TextUnit = 64.sp,
    val sp72: TextUnit = 72.sp,
    val sp80: TextUnit = 80.sp,
    val sp88: TextUnit = 88.sp,
    val sp96: TextUnit = 96.sp
)

val LocalDimens = staticCompositionLocalOf { Dimens() }

val MaterialTheme.dimens: Dimens
    @Composable
    get() = LocalDimens.current