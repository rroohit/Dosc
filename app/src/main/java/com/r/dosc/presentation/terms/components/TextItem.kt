package com.r.dosc.presentation.terms.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextItem(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 24.dp),
    ) {
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = title,
            fontSize = 20.sp,
            color = MaterialTheme.colors.onBackground,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = description,
            color = if (isSystemInDarkTheme()) Color.DarkGray else Color.Gray,
            fontSize = 15.sp,
            letterSpacing = 0.5.sp
        )
    }
}
