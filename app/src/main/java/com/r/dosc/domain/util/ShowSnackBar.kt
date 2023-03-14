package com.r.dosc.domain.util

import androidx.compose.material.ScaffoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun showSnackBar(
    message: String,
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope,
) {
    coroutineScope.launch {
        scaffoldState.snackbarHostState.showSnackbar(
            message = message
        )
    }
}