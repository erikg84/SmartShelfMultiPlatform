package org.dallas.smartshelf.view.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.dallas.smartshelf.theme.dimens

@Composable
fun SmartSpacer(size: Dp) {
    Spacer(modifier = Modifier.size(size))
}

@Composable
fun RowScope.Spacer4() {
    SmartSpacer(MaterialTheme.dimens.dp4)
}

@Composable
fun ColumnScope.Spacer4() {
    SmartSpacer(MaterialTheme.dimens.dp4)
}

@Composable
fun RowScope.Spacer8() {
    SmartSpacer(MaterialTheme.dimens.dp8)
}

@Composable
fun ColumnScope.Spacer8() {
    SmartSpacer(MaterialTheme.dimens.dp8)
}

@Composable
fun RowScope.Spacer12() {
    SmartSpacer(MaterialTheme.dimens.dp12)
}

@Composable
fun ColumnScope.Spacer12() {
    SmartSpacer(MaterialTheme.dimens.dp12)
}

@Composable
fun ColumnScope.Spacer16() {
    SmartSpacer(MaterialTheme.dimens.dp16)
}

@Composable
fun RowScope.Spacer16() {
    SmartSpacer(MaterialTheme.dimens.dp16)
}

@Composable
fun RowScope.Spacer24() {
    SmartSpacer(MaterialTheme.dimens.dp24)
}

@Composable
fun ColumnScope.Spacer24() {
    SmartSpacer(MaterialTheme.dimens.dp24)
}

@Composable
fun ColumnScope.Spacer32() {
    SmartSpacer(MaterialTheme.dimens.dp32)
}

@Composable
fun RowScope.Spacer40() {
    SmartSpacer(MaterialTheme.dimens.dp40)
}

@Composable
fun RowScope.WeightedSpacer(weight: Float = 1f) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun ColumnScope.WeightedSpacer(weight: Float = 1f) {
    Spacer(modifier = Modifier.weight(weight))
}