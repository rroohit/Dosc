package com.r.dosc.presentation.main.components

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.r.dosc.R
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.util.PermissionViewModel
import com.r.dosc.presentation.main.MainScreenEvents
import com.r.dosc.presentation.main.MainViewModel

@ExperimentalPermissionsApi
@Composable
fun ScanningFloatingButton(
    permissionViewModel: PermissionViewModel,
    mainViewModel: MainViewModel,
    cameraPermissionState: PermissionState,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = Modifier.padding(top = 30.dp),
        backgroundColor = MaterialTheme.colors.primary,
        onClick = {
            when (permissionViewModel.permissionsCamera.value) {
                Permissions.HAS_PERMISSION -> {
                    if (permissionViewModel.isStorageReadGranted.value &&
                        permissionViewModel.isStorageWriteGranted.value
                    ) {
                        //Open Camera
                        if (mainViewModel.isStartWithFileNameState.value) {
                            //start with file name
                            mainViewModel.onEvent(
                                MainScreenEvents.OpenDialog(
                                    true
                                )
                            )

                        } else {
                            onClick()
                        }

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            onClick()
                        } else {

                            mainViewModel.onEvent(
                                MainScreenEvents.ShowSnackBar(
                                    "Storage permission is needed. Enable it from app settings..",
                                )
                            )
                        }
                    }
                }
                Permissions.SHOULD_SHOW_RATIONAL -> {
                    cameraPermissionState.launchPermissionRequest()
                }
                Permissions.IS_PERMANENTLY_DENIED -> {
                    mainViewModel.onEvent(
                        MainScreenEvents.ShowSnackBar(
                            "Camera permission is needed. Enable it from app settings."
                        )
                    )
                }
                else -> Unit
            }
        },
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_file_scan),
            contentDescription = "start scanning"
        )
    }
}