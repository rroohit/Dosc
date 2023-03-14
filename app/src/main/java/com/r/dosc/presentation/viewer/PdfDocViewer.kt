package com.r.dosc.presentation.viewer

import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.imageLoader
import com.r.dosc.R
import com.r.dosc.domain.components.DeleteDialogBox
import com.r.dosc.domain.ui.theme.GrayShade_light
import com.r.dosc.domain.util.getPdfUri
import com.r.dosc.domain.util.pageIndex
import com.r.dosc.domain.util.pageIndexHorizontal
import com.r.dosc.presentation.home.HomeViewModel
import com.r.dosc.presentation.viewer.components.HorizontalPdfListPages
import com.r.dosc.presentation.viewer.components.VerticalPdfListPages
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.IOException
import kotlin.math.sqrt

@Destination
@Composable
fun PdfDocViewer(
    isDarkTheme: Boolean = true,
    navigator: DestinationsNavigator,
    file: File,
    homeViewModel: HomeViewModel,
    viewerViewModel: PdfDocViewerViewModel = hiltViewModel(),
) {
    val docListState = rememberLazyListState()

    val topPadding by remember {
        derivedStateOf {
            docListState.firstVisibleItemScrollOffset <= 0
        }
    }

    var onDeleteClicked by remember {
        mutableStateOf(false)
    }

    val rendererScope = rememberCoroutineScope()
    val mutex = remember { Mutex() }
    val renderer by produceState<PdfRenderer?>(null, file) {
        rendererScope.launch(Dispatchers.IO) {
            val input = if (file.exists()) {
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            } else {
                ParcelFileDescriptor.open(viewerViewModel.getErrorFile(), ParcelFileDescriptor.MODE_READ_ONLY)
            }
            val error: ParcelFileDescriptor = ParcelFileDescriptor.open(viewerViewModel.getErrorFile(), ParcelFileDescriptor.MODE_READ_ONLY)

            value = try {
                PdfRenderer(input)
            } catch (e: IOException) {
                PdfRenderer(error)
            }

        }
        awaitDispose {
            val currentRenderer = value
            rendererScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    currentRenderer?.close()
                }
            }
        }
    }
    val context = LocalContext.current
    val imageLoader = LocalContext.current.imageLoader
    val imageLoadingScope = rememberCoroutineScope()
    val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }


    var orientation by remember {
        mutableStateOf(true)
    }
    //orientation true = vertical doc list view
    //orientation false = Horizontal doc list view

    val pageCountText: String by remember {
        derivedStateOf {
            if (orientation) docListState.pageIndex(pageCount) else docListState.pageIndexHorizontal()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = if(isSystemInDarkTheme() || isDarkTheme) Color.DarkGray else Color.LightGray,
        topBar = {

            TopAppBar(
                title = {
                    Text(
                        text = file.name,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.navigateUp()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
                actions = {

                    IconButton(
                        onClick = {
                            onDeleteClicked = true
                        }

                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "delete_document",
                        )
                    }

                    if (orientation) {
                        IconButton(
                            onClick = {
                                orientation = false
                            }

                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_horizontal),
                                contentDescription = "change_view_to_horizontal",
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                orientation = true

                            }

                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_vertical),
                                contentDescription = "change_view_to_vertical",
                            )
                        }
                    }

                    IconButton(
                        onClick = {

                            val pdfUri: Uri = file.getPdfUri(context)

                            val shareDocument = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, pdfUri)
                                type = "application/pdf"
                            }

                            val share = Intent.createChooser(shareDocument, "share_pdf_document")
                            context.startActivity(share)

                        }

                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "share_selected_document",
                        )
                    }

                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
                modifier = Modifier
                    .height(35.dp)
                    .offset(x = (20).dp),
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    bottomStart = 20.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                ),
                backgroundColor = GrayShade_light.copy(alpha = 0.5f),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)

            ) {
                if (pageCount <= 1){
                    Text(text = "1/$pageCount", fontSize = 15.sp)

                } else {
                    Text(text = "$pageCountText/$pageCount", fontSize = 15.sp)
                }
            }
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
            val height = (width * sqrt(2f)).toInt()

            Column(
                modifier = Modifier.padding(bottom = 8.dp),
                Arrangement.Center
            ) {

                if (orientation) {

                    AnimatedVisibility(visible = topPadding) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                    }

                    VerticalPdfListPages(
                        docListState,
                        pageCount,
                        height,
                        file,
                        context,
                        imageLoader,
                        imageLoadingScope,
                        width,
                        mutex,
                        renderer,
                    )

                } else {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )

                    HorizontalPdfListPages(
                        docListState,
                        pageCount,
                        height,
                        file,
                        context,
                        imageLoader,
                        imageLoadingScope,
                        width,
                        mutex,
                        renderer,
                    )
                }
            }
        }
    }

    if (onDeleteClicked) {
        DeleteDialogBox(
            onDelete = {
                viewerViewModel.deleteDocument(file)
                homeViewModel.updateDocList()
                navigator.navigateUp()
            },
            onDismissRequest = {}
        )

    }

}

