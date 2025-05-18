package org.dallas.smartshelf.view.component.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.dallas.smartshelf.view.component.Spacer16
import kotlinx.coroutines.launch
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.viewmodel.OnboardingViewModel

@Composable
fun OnBoardingBottomBarContent(
    pagerState: PagerState,
    onAction: (OnboardingViewModel.Action) -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (pagerState.currentPage == 1) {
            Button(
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = {
                    onAction(OnboardingViewModel.Action.RequestCameraPermission)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.dp16)
            ) {
                Text("Grant Camera Access")
            }
        }
        if (pagerState.currentPage != 1) {
            Button(
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.dp16),
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage + 1 < pagerState.pageCount) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onAction(OnboardingViewModel.Action.CompleteOnboarding)
                        }
                    }
                },
            ) {
                Text(if (pagerState.currentPage == pagerState.pageCount - 1) "Finish" else "Next")
            }
        }
        if (pagerState.currentPage == 1) {
            Button(
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = { onAction(OnboardingViewModel.Action.CompleteOnboarding) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.dp16)
            ) {
                Text("Skip")
            }
        }
        Spacer16()
    }
}
