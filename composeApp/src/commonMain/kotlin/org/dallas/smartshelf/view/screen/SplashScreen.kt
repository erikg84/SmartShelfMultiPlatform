package org.dallas.smartshelf.view.screen

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
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.view.component.AppLogo
import org.dallas.smartshelf.view.component.Spacer24
import org.dallas.smartshelf.view.component.Spacer32
import org.dallas.smartshelf.viewmodel.SplashViewModel


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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.dp4)
            .clip(RoundedCornerShape(MaterialTheme.dimens.dp2))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
    ) {
        val maxWidthDp = maxWidth

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = 0.2f)
                .offset(x = (progress * maxWidthDp.value * 0.8f).dp)
                .clip(RoundedCornerShape(MaterialTheme.dimens.dp2))
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}
