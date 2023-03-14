package com.r.dosc.domain.util

import android.Manifest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.r.dosc.domain.constants.Permissions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@ExperimentalPermissionsApi
@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {

    val permissionsCamera: MutableState<Permissions> = mutableStateOf(Permissions.NOT_REQUESTED)

    val permissionsStorageRead: MutableState<Permissions> =
        mutableStateOf(Permissions.NOT_REQUESTED)

    val permissionsStorageWrite: MutableState<Permissions> =
        mutableStateOf(Permissions.NOT_REQUESTED)

    val isStorageReadGranted = mutableStateOf(false)
    val isStorageWriteGranted = mutableStateOf(false)


    fun onPermissionState(permissionsState: MultiplePermissionsState) {
        permissionsState.permissions.forEach { perm ->
            when (perm.permission) {
                Manifest.permission.CAMERA -> {
                    when {
                        perm.hasPermission -> {
                            permissionsCamera.value = Permissions.HAS_PERMISSION
                        }
                        perm.shouldShowRationale -> {
                            permissionsCamera.value = Permissions.SHOULD_SHOW_RATIONAL
                        }
                        perm.isPermanentlyDenied() -> {
                            permissionsCamera.value = Permissions.IS_PERMANENTLY_DENIED
                        }
                    }
                }
                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    when {
                        perm.hasPermission -> {
                            isStorageReadGranted.value = true
                            permissionsStorageRead.value = Permissions.HAS_PERMISSION
                        }
                        perm.shouldShowRationale -> {
                            permissionsStorageRead.value = Permissions.SHOULD_SHOW_RATIONAL
                        }
                        perm.isPermanentlyDenied() -> {
                            permissionsStorageRead.value = Permissions.IS_PERMANENTLY_DENIED
                        }
                    }
                }
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                    when {
                        perm.hasPermission -> {
                            isStorageWriteGranted.value = true
                            permissionsStorageWrite.value = Permissions.HAS_PERMISSION
                        }
                        perm.shouldShowRationale -> {
                            permissionsStorageWrite.value = Permissions.SHOULD_SHOW_RATIONAL
                        }
                        perm.isPermanentlyDenied() -> {
                            permissionsStorageWrite.value = Permissions.IS_PERMANENTLY_DENIED
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
fun PermissionState.isPermanentlyDenied(): Boolean {
    return !shouldShowRationale && !hasPermission
}