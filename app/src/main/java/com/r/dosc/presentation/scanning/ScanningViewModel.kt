package com.r.dosc.presentation.scanning

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.airbnb.lottie.compose.LottieConstants
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.r.dosc.di.modules.CamX
import com.r.dosc.domain.models.ImageEditDetails
import com.r.dosc.domain.util.DocumentEssential
import com.roh.cropimage.CropUtil
import com.roh.cropimage.IRect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class ScanningViewModel
@Inject constructor(
    @Named("temp") private val tempDirectory: File,
    @Named("dosc") private val mainDirectory: File,
    private val cameraExecutor: ExecutorService,
    private val camX: CamX,
    private val documentEssential: DocumentEssential,
    private val iDocument: Document
) : ViewModel() {

    var docName = ""

    val listOfImages = mutableStateListOf<Uri>()
    private val listOfImageBitmaps = arrayListOf<ImageEditDetails>()

    private var canvasWidth: Int by mutableIntStateOf(0)
    private var canvasHeight: Int by mutableIntStateOf(0)

    private val _uiEvent = MutableStateFlow<ScanningScreenEvents>(ScanningScreenEvents.CameraScreen)
    val uiEvent = _uiEvent

    val closeScanningScreen = MutableStateFlow(false)
    val bitmapImage = MutableStateFlow<Bitmap?>(null)

    val imageEditDetails = MutableStateFlow<ImageEditDetails?>(null)

    val showDialog = MutableStateFlow(false)
    val captureImage = MutableStateFlow(false)
    val isScanningMode = MutableStateFlow(true)
    val isDocumentPreviewMode = MutableStateFlow(false)
    val iterationsBtn = MutableStateFlow(LottieConstants.IterateForever)
    var scrollIndex = MutableStateFlow(0)
    private val isClickedFirstTime = MutableStateFlow(CaptureButtonAnim.INITIAL)

    init {
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs()
        }
        if (!mainDirectory.exists()) {
            mainDirectory.mkdirs()
        }
    }

    fun onEvent(events: ScanningScreenEvents) {
        when (events) {
            is ScanningScreenEvents.OpenDocPreview -> {
                iterationsBtn.value = 2
                isDocumentPreviewMode.value = true
                isScanningMode.value = false

                viewModelScope.launch {
                    bitmapImage.value = getBitmap(events.context, events.uri.toString())
                    imageEditDetails.value = listOfImageBitmaps.find { it.imgUri == events.uri }
                    _uiEvent.emit(events)
                }
            }

            ScanningScreenEvents.CameraScreen -> {
                bitmapImage.value = null
                imageEditDetails.value = null

                isDocumentPreviewMode.value = false
                isScanningMode.value = true
                viewModelScope.launch {
                    _uiEvent.emit(events)
                }

            }

            is ScanningScreenEvents.RemoveImage -> {
                bitmapImage.value = null
                imageEditDetails.value = null
                listOfImages.removeAt(events.indx)
                listOfImageBitmaps.removeIf { it.index == events.indx }
                if (!isScanningMode.value) {
                    onEvent(ScanningScreenEvents.CameraScreen)
                }

            }

            is ScanningScreenEvents.SavePdf -> {
                bitmapImage.value = null
                imageEditDetails.value = null

                showDialog.value = true
                createPdfDocument(events.context)
            }
        }

    }

    private fun createPdfDocument(context: Context) {
        documentEssential.pdfWriter(iDocument, getFileName())
        iDocument.open()

        viewModelScope.launch {
            var count = 0
            val creatingPdf = async {
                listOfImageBitmaps.forEachIndexed { _, imFile ->
                    count++

                    if (imFile.isEdited) {
                        val newUri = getImageUri(context, imFile)
                        val image: Image =
                            Image.getInstance(documentEssential.compressImage(count, newUri))

                        image.setAbsolutePosition(0f, 0f)

                        iDocument.pageSize = Rectangle(image.width, image.height)

                        iDocument.newPage()

                        iDocument.add(image)

                    } else {
                        val image: Image =
                            Image.getInstance(documentEssential.compressImage(count, imFile.imgUri))

                        image.setAbsolutePosition(0f, 0f)

                        iDocument.pageSize = Rectangle(image.width, image.height)

                        iDocument.newPage()

                        iDocument.add(image)

                    }
                }

            }
            creatingPdf.await()
            delay(2000L)
            showDialog.value = false
            closeScanningScreen.emit(true)
        }

    }


    fun addImage(uri: Uri) {
        viewModelScope.launch {
            listOfImages.add(uri)
            val currImageIndex = listOfImageBitmaps.lastIndex + 1
            val imgEditDetails = ImageEditDetails(
                index = currImageIndex,
                imgUri = uri,
                isEdited = false,
                iRect = IRect()
            )

            listOfImageBitmaps.add(imgEditDetails)
            scrollIndex.emit(listOfImageBitmaps.size)
        }
    }

    fun clickImage(click: Boolean) {
        viewModelScope.launch {
            captureImage.emit(click)
            if (click) {
                if (isClickedFirstTime.value == CaptureButtonAnim.INITIAL) {
                    iterationsBtn.value = 1
                    iterationsBtn.value++
                } else {
                    iterationsBtn.value++
                }

            }
            isClickedFirstTime.emit(CaptureButtonAnim.CLICKED)
        }
    }

    fun getTempOutputDirectory(): File = tempDirectory

    fun getCameraExecutor(): ExecutorService = cameraExecutor

    suspend fun getCameraProvider(): ProcessCameraProvider = camX.getCameraProvider()

    private fun getFileName(): String = if (docName.isNotEmpty()) "$mainDirectory/${
        checkFileExist(
            docName,
            0
        )
    }.pdf" else "$mainDirectory/${getDefaultName()}.pdf"

    private fun checkFileExist(fileName: String, count: Int): String {
        val file = if (count > 0) {
            File("$mainDirectory/$fileName($count).pdf")
        } else {
            File("$mainDirectory/$fileName.pdf")
        }

        return if (!file.exists()) {
            if (count > 0) {
                "$fileName($count)"
            } else {
                fileName
            }

        } else {
            checkFileExist(fileName, count + 1)
        }
    }

    private fun getDefaultName(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        val timeStamp: String = dateFormat.format(currentTime)
        return "dosc-$timeStamp"
    }

    private suspend fun getBitmap(context: Context, imgUrl: String): Bitmap? {
        val loading = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imgUrl)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable

        return (result as BitmapDrawable).bitmap
    }

    private suspend fun getImageUri(context: Context, imageEditDetails: ImageEditDetails): Uri =
        withContext(Dispatchers.IO) {

            val loading = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageEditDetails.imgUri)
                .build()

            val result = (loading.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap

            val cropUtil = CropUtil(bitmap).apply {
                updateOldRectSize(imageEditDetails.iRect)
            }

            val newBitmap = cropUtil.cropImage(canvasWidth, canvasHeight)

            val photoOutputTempFile = File(
                tempDirectory,
                SimpleDateFormat(
                    "yyy-MM-dd-HH-ss-SSS",
                    Locale.getDefault()
                ).format(System.currentTimeMillis()) + ".jpg"
            )

            val fOut = FileOutputStream(photoOutputTempFile)

            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)

            fOut.flush()
            fOut.close()

            Uri.fromFile(photoOutputTempFile)

        }

    fun updateImageCropBound(
        index: Int,
        iRect: IRect
    ) {
        viewModelScope.launch {
            val newImageEditDetails = listOfImageBitmaps[index].copy(
                isEdited = true,
                iRect = iRect
            )
            listOfImageBitmaps[index] = (newImageEditDetails)
        }
    }

    fun updateCropRectSize(width: Int, height: Int) {
        if (canvasWidth <= 0 || canvasHeight <= 0) {
            this.canvasWidth = width
            this.canvasHeight = height
        }

    }

    override fun onCleared() {
        super.onCleared()
        iDocument.close()
        getTempOutputDirectory().deleteRecursively()
    }

}

enum class CaptureButtonAnim {
    INITIAL,
    CLICKED
}

