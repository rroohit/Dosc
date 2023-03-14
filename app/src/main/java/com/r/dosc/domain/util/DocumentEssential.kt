package com.r.dosc.domain.util

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class DocumentEssential @Inject
constructor(
    private val context: Context,
    @Named("temp") private val tempDirectory: File,
) {
    fun pdfWriter(iDocument: Document, fileName: String): PdfWriter =
        PdfWriter.getInstance(
            iDocument,
            FileOutputStream(fileName)
        )

    suspend fun compressImage(count: Int, imgFileUri: Uri): String {

        val photoOutputTempFile = File(
            tempDirectory,
            SimpleDateFormat(
                " ($count) yyy-MM-dd-HH-ss-SSS",
                Locale.getDefault()
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val compressedImageFile: File = Compressor.compress(context, imgFileUri.toFile()) {
                default()
                destination(photoOutputTempFile)
            }

        return compressedImageFile.absolutePath

    }



}