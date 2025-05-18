package org.dallas.smartshelf.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.dallas.smartshelf.theme.dimens

@Composable
fun AppLogo() {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer16()
        AppLogoText()
    }
}


@Composable
private fun AppLogoText(modifier: Modifier = Modifier) {
    Text(
        text = "SMART SHELF",
        fontSize = MaterialTheme.dimens.sp40,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}