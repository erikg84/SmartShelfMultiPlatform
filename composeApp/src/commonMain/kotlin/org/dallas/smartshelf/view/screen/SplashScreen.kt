package com.junevrtech.smartshelf.view.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.times
import com.junevrtech.smartshelf.theme.SmartShelfTheme
import com.junevrtech.smartshelf.theme.dimens
import com.junevrtech.smartshelf.view.component.app.AppLogo
import org.dallas.smartshelf.view.component.Spacer24
import org.dallas.smartshelf.view.component.Spacer32
import com.junevrtech.smartshelf.viewmodel.SplashViewModel


@Composable
fun SplashScreen(onAction: (SplashViewModel.Action) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.dimens.dp16),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppLogo()

                Spacer24()

                Text(
                    text = "Plan, Organize and Manage your Food Inventory",
                    fontSize = MaterialTheme.dimens.sp16,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer32()

                InfiniteProgressBar()
            }
        }
    }
    LaunchedEffect(Unit) {
        onAction(SplashViewModel.Action.NavigateToLogin)
    }
}

@Composable
fun InfiniteProgressBar() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.dp4)
            .clip(RoundedCornerShape(MaterialTheme.dimens.dp2))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = 0.2f)
                .offset(x = progress * (LocalContext.current.resources.displayMetrics.widthPixels.toFloat() * 0.8f).dp)
                .clip(RoundedCornerShape(MaterialTheme.dimens.dp2))
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SmartShelfTheme {
        SplashScreen{}
    }
}