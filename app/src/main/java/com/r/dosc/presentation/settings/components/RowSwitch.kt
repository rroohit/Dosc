package com.r.dosc.presentation.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowSwitch(
    title: String,
    helperText: String,
    toggle: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(top = 13.dp)
                .weight(8f)
        ) {
            Text(
                text = title,
                fontSize = 21.sp,
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = helperText,
                color = Color.LightGray,
                fontSize = 15.sp,
            )
        }

        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.TopCenter

        ) {
            Switch(
                checked = toggle,
                onCheckedChange = onCheckChange,
                colors = SwitchDefaults.colors(
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray
                )
            )

        }

    }

}