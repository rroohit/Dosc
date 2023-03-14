package com.r.dosc.domain.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r.dosc.presentation.main.MainScreenEvents
import com.r.dosc.presentation.main.MainViewModel

@Composable
fun DocumentNameDialogBox(
    viewModel: MainViewModel,
    onSubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            viewModel.onEvent(MainScreenEvents.OpenDialog(false))
        },
        title = {
            Text(
                text = "Enter Document Name",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {

                Text(text = "")
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = {
                        if (text.length < 25) {
                            text = it
                        }
                    },
                    singleLine = true,
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = if (isSystemInDarkTheme() || viewModel.isDarkThemeState.value) Color.White else Color.Black
                    ),
                    placeholder = {
                        Text(text = "Type here..", color = Color.LightGray)
                    }
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier
                        .weight(5f)
                        .height(45.dp)
                        .padding(start = 12.dp, end = 6.dp),
                    onClick = {
                        viewModel.onEvent(MainScreenEvents.OpenDialog(false))

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.background
                    )
                ) {
                    Text(text = "Cancel", fontSize = 16.sp)
                }
                Button(
                    modifier = Modifier
                        .weight(5f)
                        .height(45.dp)
                        .padding(start = 6.dp, end = 12.dp),
                    onClick = {
                        if (text.isEmpty()) {
                            viewModel.onEvent(MainScreenEvents.ShowSnackBar("Enter Name"))
                        } else {
                            onSubmit(text)
                        }
                        viewModel.onEvent(MainScreenEvents.OpenDialog(false))

                    },
                ) {
                    Text(text = "Start", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    )
}