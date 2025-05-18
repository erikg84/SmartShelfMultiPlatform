package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import org.dallas.smartshelf.view.component.dialog.CameraPermissionDeniedDialog
import org.dallas.smartshelf.view.component.onboarding.OnBoardingBottomBarContent
import org.dallas.smartshelf.view.component.onboarding.OnBoardingScreenContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.util.PermissionHandler
import org.dallas.smartshelf.util.handleEvent
import org.dallas.smartshelf.util.rememberPermissionHandler
import org.dallas.smartshelf.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewState: OnboardingViewModel.ViewState,
    onAction: (OnboardingViewModel.Action) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val permissionHandler = rememberPermissionHandler { isGranted ->
        if (isGranted) {
            onAction(OnboardingViewModel.Action.CameraPermissionGranted)
        } else {
            onAction(OnboardingViewModel.Action.CameraPermissionDenied)
        }
    }

    Scaffold(
        bottomBar = {
            OnBoardingBottomBarContent(
                pagerState = pagerState,
                onAction = onAction
            )
        },
        content = { padding ->
            OnBoardingScreenContent(
                modifier = Modifier.padding(padding),
                pagerState = pagerState
            )
        }
    )

    if (viewState.isPermissionDeniedDialogVisible) {
        CameraPermissionDeniedDialog(
            onConfirm = {
                onAction(OnboardingViewModel.Action.CameraPermissionDeniedDialogConfirmed)
            },
            onCancel = {
                onAction(OnboardingViewModel.Action.DismissDialog)
            }
        )
    }

    handleEvent(viewState.consumableEvent, permissionHandler, pagerState, scope)
}

private fun handleEvent(
    consumableEvent: ConsumableEvent<OnboardingViewModel.Event>,
    permissionHandler: PermissionHandler,
    pagerState: PagerState,
    scope: CoroutineScope
) {
    consumableEvent.handleEvent { event ->
        when (event) {
            is OnboardingViewModel.Event.RequestCameraPermission -> permissionHandler.requestCameraPermission()
            is OnboardingViewModel.Event.CameraPermissionGranted,
            is OnboardingViewModel.Event.CameraPermissionDeniedDialogConfirmed -> scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }
}
