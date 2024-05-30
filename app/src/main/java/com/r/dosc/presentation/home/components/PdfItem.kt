package com.r.dosc.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r.dosc.R
import com.r.dosc.domain.components.DropDownMenu
import com.r.dosc.domain.models.PdfDocumentDetails
import com.r.dosc.domain.ui.theme.GrayShade_dark
import java.io.File

@Composable
fun PdfItem(
    pdfDocumentDetails: PdfDocumentDetails,
    onDelete: @Composable (PdfDocumentDetails) -> Unit,
    onShare: (File) -> Unit,
    onRename: @Composable (PdfDocumentDetails) -> Unit,
    openDocument: (PdfDocumentDetails) -> Unit
) {

    var showDropDown by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                openDocument(pdfDocumentDetails)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .weight(17f)
                .size(25.dp),
            painter = painterResource(id = R.drawable.ic_pdf),
            contentDescription = "icon",
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.weight(66f)
        ) {
            Text(
                text = pdfDocumentDetails.documentName, fontSize = 19.sp, maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row {
                Text(
                    text = pdfDocumentDetails.dateCreated,
                    fontSize = 14.sp,
                    color = GrayShade_dark
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(text = pdfDocumentDetails.docSize, fontSize = 14.sp, color = GrayShade_dark)

                Spacer(modifier = Modifier.width(6.dp))

            }
        }

        Box(
            modifier = Modifier.weight(16f),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = {
                    showDropDown = !showDropDown
                }
            ) {
                // More vert icon for share, rename, delete option drop down
                Icon(
                    modifier = Modifier.size(25.dp),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "more",
                    tint = GrayShade_dark
                )
                if (showDropDown) {
                    DropDownMenu(
                        onDeleteCheck = false,
                        modifier = Modifier,
                        onShare = {
                            onShare(pdfDocumentDetails.file)
                        },
                        onRename = {
                            onRename(pdfDocumentDetails)
                        },
                        onDelete = {
                            onDelete(pdfDocumentDetails)
                        },
                        onDismissRequest = {
                            showDropDown = false
                        }
                    )
                }
            }
        }
    }
}

