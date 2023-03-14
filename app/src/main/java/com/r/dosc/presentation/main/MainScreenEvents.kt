package com.r.dosc.presentation.main

import androidx.lifecycle.Lifecycle

sealed class MainScreenEvents {
    data class TopAppBarTitle(val route: String?) : MainScreenEvents()
    data class ShowSnackBar(val uiText: String) : MainScreenEvents()
    data class IsDarkTheme(val isDarkTheme : Boolean) : MainScreenEvents()
    data class IsStartWithFileName(val isStartWithFileName: Boolean) : MainScreenEvents()
    data class OpenDialog(val open: Boolean) : MainScreenEvents()
    data class LifecycleEvents(val  lifecycleEvents: Lifecycle.Event) : MainScreenEvents()

}