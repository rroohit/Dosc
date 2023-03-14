package com.r.dosc.domain.models

import java.io.File

data class PdfDocumentDetails(
    val documentName: String = "",
    val filePath: String? = null,
    val noOfPages: String = "",
    val docSize: String,
    val dateCreated: String,
    val timestamp: Long,
    val file: File
)
