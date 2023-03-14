package com.r.dosc.domain.util

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.core.content.FileProvider
import com.r.dosc.BuildConfig
import java.io.File

fun LazyListState.pageIndex(count: Int): String {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()
    if (lastItem != null) {
        return if (lastItem.size + lastItem.offset <= layoutInfo.viewportEndOffset && isScrolledToEnd()) {
            count.toString()
        } else {
            lastItem.index.toString()
        }
    }
    return "1"
}

fun LazyListState.pageIndexHorizontal(): String {
    val ind = firstVisibleItemIndex + 1
    return ind.toString()
}

fun LazyListState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

//get document uri to share
fun File.getPdfUri(context: Context): Uri = FileProvider.getUriForFile(
    context,
    BuildConfig.APPLICATION_ID + ".provider",
    absoluteFile
)