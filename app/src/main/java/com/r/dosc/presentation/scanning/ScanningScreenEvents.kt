package com.r.dosc.presentation.scanning

import android.content.Context
import android.net.Uri


sealed class ScanningScreenEvents {
    data class OpenDocPreview(val uri: Uri, val indx: Int, val context: Context) : ScanningScreenEvents()
    object CameraScreen : ScanningScreenEvents()
    data class SavePdf(val context: Context) : ScanningScreenEvents()
    data class RemoveImage(val indx: Int) : ScanningScreenEvents()
}