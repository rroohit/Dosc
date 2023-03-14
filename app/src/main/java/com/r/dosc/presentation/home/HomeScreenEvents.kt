package com.r.dosc.presentation.home

sealed class HomeScreenEvents{
    data class DismissDropDown(val dismiss: Boolean): HomeScreenEvents()
}
