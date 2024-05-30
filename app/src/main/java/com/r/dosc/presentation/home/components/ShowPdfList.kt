package com.r.dosc.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r.dosc.domain.models.PdfDocumentDetails
import com.r.dosc.domain.ui.theme.Helper_Text_Color
import java.io.File

@Composable
fun ShowPdfList(
    sortSelectedID: Int,
    listOfPdfs: List<PdfDocumentDetails>,
    openDocument: (PdfDocumentDetails, Int) -> Unit,
    onShare: (File) -> Unit,
    onRename: @Composable (PdfDocumentDetails) -> Unit,
    onDelete: @Composable (PdfDocumentDetails) -> Unit,
    onSorIdSelected: (Int) -> Unit
) {

    Column(
        modifier = Modifier.padding(bottom = 56.dp)
    ) {
        HelperTabLayout(
            selectedId = sortSelectedID,
            onSortIdSelect = { id ->
                onSorIdSelected(id)
            }
        )

        LazyColumn {
            listOfPdfs.forEachIndexed { index, pdfDocumentDetails ->
                item {
                    PdfItem(
                        pdfDocumentDetails = pdfDocumentDetails,
                        onDelete = {
                            onDelete(it)
                        },
                        onShare = { file ->
                            onShare(file)
                        },
                        onRename = { pdfFile ->
                            onRename(pdfFile)
                        },
                        openDocument = { doc ->
                            openDocument(doc, index)
                        }
                    )
                    Divider(
                        modifier = Modifier.padding(start = 50.dp, end = 12.dp),
                        color = Helper_Text_Color,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}
