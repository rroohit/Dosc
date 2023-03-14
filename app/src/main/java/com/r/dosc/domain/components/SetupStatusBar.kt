package com.r.dosc.domain.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.systemuicontroller.SystemUiController
import com.r.dosc.domain.ui.theme.Ocean_Red
import com.r.dosc.presentation.main.MainScreenEvents
import com.r.dosc.presentation.main.MainViewModel

@Composable
fun SetUpStatusBar(
    systemUiController: SystemUiController,
    lifecycleOwner: LifecycleOwner,
    viewModel: MainViewModel,
    updateStatusBar: Boolean
) {
    if (viewModel.isDarkThemeState.value) {
        systemUiController.setStatusBarColor(
            color = Ocean_Red
        )
        systemUiController.setNavigationBarColor(
            color = MaterialTheme.colors.primarySurface
        )
    } else {
        systemUiController.setStatusBarColor(
            color = Ocean_Red
        )
        systemUiController.setNavigationBarColor(
            color = MaterialTheme.colors.surface
        )
    }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                viewModel.onEvent(MainScreenEvents.LifecycleEvents(event))
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )


    if (updateStatusBar && viewModel.lifecycleEvent.value == Lifecycle.Event.ON_START) {
        if (viewModel.isDarkThemeState.value) {
            systemUiController.setStatusBarColor(
                color = Ocean_Red
            )
            systemUiController.setNavigationBarColor(
                color = MaterialTheme.colors.surface
            )
        } else {
            systemUiController.setStatusBarColor(
                color = Ocean_Red
            )
            systemUiController.setNavigationBarColor(
                color = MaterialTheme.colors.surface
            )
        }
    }

}
