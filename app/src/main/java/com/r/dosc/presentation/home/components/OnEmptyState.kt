package com.r.dosc.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.r.dosc.R

@Composable
fun OnEmptyState(
    text: String = "",
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lottie_home_anim)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LottieAnimation(
            composition,
            progress= { progress },
            modifier = Modifier.size(250.dp, 250.dp),
        )

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = text,
            style = TextStyle(
                textAlign = TextAlign.Center,
            ),
            color = Color.LightGray
        )

    }

}