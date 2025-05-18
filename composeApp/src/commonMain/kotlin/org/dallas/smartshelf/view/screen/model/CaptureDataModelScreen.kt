package org.dallas.smartshelf.view.screen.model

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dallas.smartshelf.viewmodel.CapturedDataViewModel
import org.koin.core.parameter.parametersOf

object CaptureDataModelScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val viewModel = getScreenModel<CapturedDataViewModel> { parametersOf(navigator) }
        val viewState by viewModel.viewState.collectAsState()

        // Your UI implementation
        // ...

        // Now you can use actions without navigation callbacks
        Button(
            onClick = { viewModel.onAction(CapturedDataViewModel.Action.SaveProduct) }
        ) {
            Text("Save Product")
        }

        Button(
            onClick = { viewModel.onAction(CapturedDataViewModel.Action.Cancel) }
        ) {
            Text("Cancel")
        }
    }
}