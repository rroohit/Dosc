package com.r.dosc.presentation.scanning.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.r.dosc.R
import com.r.dosc.domain.navigation.ClearRippleTheme
import com.r.dosc.presentation.scanning.ScanningViewModel

@Composable
fun CaptureDocFloatingButton(
    scanningViewModel: ScanningViewModel,
) {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.white_click)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = true,
        iterations = scanningViewModel.iterationsBtn.collectAsState().value,
        cancellationBehavior = LottieCancellationBehavior.Immediately,
    )

    CompositionLocalProvider(
        LocalRippleTheme provides ClearRippleTheme
    ) {
        LottieAnimation(
            composition = composition,
            progress= { progress },
            modifier = Modifier
                .size(250.dp, 250.dp)
                .clickable {
                    scanningViewModel.clickImage(true)
                },

            )
    }
}



