package com.r.dosc.presentation.scanning.components

import android.net.Uri
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.r.dosc.presentation.scanning.ScanningViewModel
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

@Composable
fun CameraView(
    modifier: Modifier,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    scanningViewModel: ScanningViewModel
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }

    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }


    LaunchedEffect(Unit) {
        val cameraProvider = scanningViewModel.getCameraProvider()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalyzer
            )

        } catch (e: Exception) {
            //handle error
        }

        scanningViewModel.captureImage.collectLatest { click ->
            when (click) {
                true -> {
                    takePhoto(
                        imageCapture = imageCapture,
                        outputDir = scanningViewModel.getTempOutputDirectory(),
                        executorService = scanningViewModel.getCameraExecutor(),
                        onImageCaptured = { uri ->
                            onImageCaptured(uri)
                        },
                        onError = onError
                    )
                    scanningViewModel.clickImage(false)

                }
                else -> Unit
            }
        }

    }

    AndroidView(
        {
            previewView
        },
        modifier = modifier
    )
}

private fun takePhoto(
    imageCapture: ImageCapture,
    outputDir: File,
    executorService: ExecutorService,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {

    val photoOutputTempFile = File(
        outputDir,
        SimpleDateFormat(
            "yyy-MM-dd-HH-ss-SSS",
            Locale.getDefault()
        ).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoOutputTempFile).build()

    imageCapture.takePicture(
        outputOptions,
        executorService,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val saveUri: Uri = Uri.fromFile(photoOutputTempFile)
                onImageCaptured(saveUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

