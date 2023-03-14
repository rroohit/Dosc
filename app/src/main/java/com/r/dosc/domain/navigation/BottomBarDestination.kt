package com.r.dosc.domain.navigation

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.r.dosc.presentation.destinations.HomeScreenDestination
import com.r.dosc.presentation.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val title: String,
    val contentDescription: String
) {

    @ExperimentalPermissionsApi
    Home(HomeScreenDestination, "Home","home_screen"),
    Settings(SettingsScreenDestination ,"Setting","settings_screen")
}