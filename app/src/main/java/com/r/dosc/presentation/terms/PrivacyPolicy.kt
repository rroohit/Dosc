package com.r.dosc.presentation.terms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.r.dosc.R
import com.r.dosc.domain.models.PrivacyData
import com.r.dosc.presentation.terms.components.TextItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay



@Destination
@Composable
fun PrivacyPolicyScreen(
    navigator: DestinationsNavigator
) {

    val privacyList = listOf(
        PrivacyData(title = "Privacy Policy", stringResource(id = R.string.txt_privacy_policy)),
        PrivacyData(title = "Children’s Privacy", stringResource(id = R.string.txt_children_privacy)),
        PrivacyData(title = "Changes to This Privacy Policy", stringResource(id = R.string.txt_change_privacy)),
        PrivacyData(title = "Contact Us", stringResource(id = R.string.txt_contact_us))
    )

    var isShowTopBar by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(true) {
        delay(200L)
        isShowTopBar = true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AnimatedVisibility(
                visible = isShowTopBar,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(
                    animationSpec = tween(
                        900,
                        easing = { 1f })
                ),
                exit = fadeOut(animationSpec = tween(100, easing = { 1f }))
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Privacy Policy",
                            fontSize = 21.sp,
                            color = Color.White,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigator.navigateUp()

                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack, contentDescription = "back"
                            )
                        }
                    }
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            privacyList.forEach { data ->
                item {
                    TextItem(title = data.title, description = data.description)
                }
            }
        }
    }

}