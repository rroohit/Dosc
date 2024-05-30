package com.r.dosc.domain.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeleteDialogBox(
    onDelete:  () -> Unit,
    onDismissRequest: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismissRequest()
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "You are about to delete a document",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        minLines = 1
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "This will delete your document permanently \nAre you sure?",
                        fontSize = 16.sp,
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
                            showDialog = false
                            onDismissRequest()

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
                            showDialog = false
                            onDelete()

                        },
                    ) {
                        Text(text = "Delete", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        )
    }
}