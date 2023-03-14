package com.r.dosc.presentation.scanning.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.r.dosc.R
import com.r.dosc.domain.ui.theme.Dark_1


@Composable
fun DocCreatingDialog() {

    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.anim_pdf_generating))
    val progress by animateLottieCompositionAsState(
        clipSpec = LottieClipSpec.Progress(0.2f, 1f),
        composition = composition,
        iterations = Int.MAX_VALUE
    )

    val transparentGradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xBFFFFFFF),
            Color(0x66FFFFFF)
        )
    )

    Dialog(
        onDismissRequest = {},
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = transparentGradientBrush, shape = RoundedCornerShape(12.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                LottieAnimation(
                    modifier = Modifier.size(130.dp),
                    composition = composition,
                    progress = { progress }
                )

                Text(
                    text = "Creating Document",
                    color = Dark_1,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

            }
        }
    }
}