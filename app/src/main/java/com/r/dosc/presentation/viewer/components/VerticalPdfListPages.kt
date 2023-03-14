@file:OptIn(ExperimentalSnapperApi::class)

package com.r.dosc.presentation.viewer.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.memory.MemoryCache
import coil.request.ImageRequest
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.math.sqrt

@Composable
fun VerticalPdfListPages(
    docListState: LazyListState,
    pageCount: Int,
    height: Int,
    file: File,
    context: Context,
    imageLoader: ImageLoader,
    imageLoadingScope: CoroutineScope,
    width: Int,
    mutex: Mutex,
    renderer: PdfRenderer?
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = docListState,
        flingBehavior = rememberSnapperFlingBehavior(docListState)
    ) {
        items(
            count = pageCount,
            key = { index -> "$file-$index" }
        ) { index ->
            val cacheKey = MemoryCache.Key("$file-$index")
            var bitmap by remember { mutableStateOf(imageLoader.memoryCache?.get(cacheKey)?.bitmap) }
            if (bitmap == null) {
                DisposableEffect(file, index) {
                    val job = imageLoadingScope.launch(Dispatchers.IO) {
                        val destinationBitmap =
                            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        mutex.withLock {

                            if (!coroutineContext.isActive) return@launch
                            try {
                                renderer?.let {
                                    it.openPage(index).use { page ->
                                        page.render(
                                            destinationBitmap,
                                            null,
                                            null,
                                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                //Just catch and return in case the renderer is being closed
                                println("im got error $e")
                                return@launch
                            }
                        }
                        bitmap = destinationBitmap
                    }
                    onDispose {
                        job.cancel()
                    }
                }
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .aspectRatio(1f / sqrt(2f))
                        .fillMaxWidth()
                )
            } else {
                val request = ImageRequest.Builder(context)
                    .size(width, height)
                    .memoryCacheKey(cacheKey)
                    .data(bitmap)
                    .build()

                Image(
                    modifier = Modifier
                        .background(Color.White)
                        .aspectRatio(1f / sqrt(2f))
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                    painter = rememberAsyncImagePainter(
                        request,
                        contentScale = ContentScale.Fit
                    ),
                    contentDescription = "Page ${index + 1} of $pageCount"
                )
            }
        }
    }
}