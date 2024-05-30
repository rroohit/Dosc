package com.r.dosc.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r.dosc.data.preference.PreferenceStorage
import com.r.dosc.domain.models.PdfDocumentDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class HomeViewModel
@Inject constructor(
    @Named("dosc") private val mainDirectory: File,
    private val prefStorage: PreferenceStorage,
) : ViewModel() {

    val sortTypeId = MutableStateFlow(2)
    private val listOfPdfDocuments = MutableStateFlow<List<PdfDocumentDetails>>(emptyList())

    val listPdf = combine(listOfPdfDocuments, sortTypeId) { list, id ->
        if (id == 1) {
            list.sortedBy {
                it.documentName
            }
        } else {
            list.sortedByDescending {
                it.timestamp
            }
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            sortTypeId.value = prefStorage.sortTypeId.first()
        }
        getAllPdfDocuments()
    }

    fun updateSortType(typeId: Int) {
        viewModelScope.launch {
            sortTypeId.emit(typeId)
            prefStorage.setSortId(typeId)
        }
    }

    fun deleteDocument(pdfDoc: PdfDocumentDetails) {
        val file = File("${pdfDoc.filePath}")
        file.deleteRecursively()
        updateDocList()
    }

    fun renameThePdfFile(
        pdfDoc: PdfDocumentDetails,
        newFileName: String,
        onError: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        val oldFile = pdfDoc.file

        if (!oldFile.exists()) {
            // File not exist
            onError()
            return
        }

        val newFile = File(oldFile.parent, newFileName)
        val success = oldFile.renameTo(newFile)

        if (success) {
            updateDocList()
            onSuccess()
        } else {
            onError()
        }
    }

    private fun getAllPdfDocuments() {
        val list = mutableListOf<PdfDocumentDetails>()
        mainDirectory.listFiles()?.forEach { file ->
            val pdfDocumentDetails = PdfDocumentDetails(
                documentName = file.name,
                filePath = file.absolutePath,
                noOfPages = "",
                docSize = getFileSize(file.length()),
                dateCreated = getFileDate(file.lastModified()),
                timestamp = file.lastModified(),
                file = getPdfDocument(file.absolutePath)
            )
            list.add(pdfDocumentDetails)
        }

        viewModelScope.launch {
            listOfPdfDocuments.emit(list)
        }
    }

    fun updateDocList() {
        viewModelScope.launch {
            listOfPdfDocuments.emit(emptyList())
            getAllPdfDocuments()
        }
    }

    private fun getFileSize(length: Long): String {
        val size = (length / 1024)
        return if (size < 1024) {
            "$size kB"
        } else {
            "${(size / 1024)} MB"
        }
    }

    private fun getFileDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(timestamp)
    }

    private fun getPdfDocument(path: String?): File = File(path.toString())

}