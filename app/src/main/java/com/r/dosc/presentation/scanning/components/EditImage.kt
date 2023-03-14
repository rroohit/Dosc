package com.r.dosc.presentation.scanning.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r.dosc.R
import com.r.dosc.domain.ui.theme.GrayShade_dark
import com.r.dosc.domain.ui.theme.Helper_Text_Color

@Composable
fun EditImage(
    crop: () -> Unit,
    theme: () -> Unit,
    rotate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        EditItem(R.drawable.ic_crop, "Crop", )

//        Spacer(modifier = Modifier.width(16.dp))
//
//        EditItem(R.drawable.ic_black_white, "Theme"){
//            theme()
//        }
//
//        Spacer(modifier = Modifier.width(16.dp))
//
//        EditItem(R.drawable.ic_rotate, "Rotate") {
//            rotate()
//        }


    }

}

@Composable
fun EditItem(
    icon: Int,
    text: String,
    //onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.clickable {
//            onClick()
//        }
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .border(
                    width = 0.4.dp,
                    color = GrayShade_dark,
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(25.dp),
                painter = painterResource(icon),
                contentDescription = text,
                tint = Color.Unspecified
            )
        }

        Text(text = text, fontSize = 13.sp, color = Helper_Text_Color)

    }
}