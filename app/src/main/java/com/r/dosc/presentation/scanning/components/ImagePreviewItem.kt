package com.r.dosc.presentation.scanning.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.r.dosc.domain.ui.theme.GrayShade_dark
import com.r.dosc.domain.ui.theme.White_Shade


@Composable
fun ImagePreviewItem(
    uri: Uri,
    count: Int,
    borderColor: Color,
    removeImage: (Int) -> Unit,
    onImageClick: (Uri, Int) -> Unit,
) {

    Box(
        modifier = Modifier
            .size(68.dp)
            .padding(end = 12.dp, top = 6.dp, bottom = 6.dp)
            .clickable {
                onImageClick(uri, count - 1)

            },
        contentAlignment = Alignment.Center
    ) {

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .clip(RoundedCornerShape(10.dp)),
            model = uri,
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
        )

        Icon(
            modifier = Modifier
                .size(14.dp)
                .offset(x = 26.dp, y = (-27).dp)
                .clip(RoundedCornerShape(100))
                .background(GrayShade_dark)
                .clickable {
                    removeImage(count - 1)
                }
                .padding(2.dp),
            imageVector = Icons.Rounded.Delete,
            contentDescription = "delete",
            tint = White_Shade
        )


        Box(
            modifier = Modifier
                .size(15.dp)
                .offset(x = (-25).dp, y = 27.dp)
                .clip(RoundedCornerShape(100))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = count.toString(), color = White_Shade, fontSize = 10.sp)
        }

    }

}