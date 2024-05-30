package com.r.dosc.domain.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r.dosc.presentation.main.MainViewModel

@Composable
fun RenamePdfDialogBox(
    viewModel: MainViewModel,
    onRenameFileDone: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }
    var newName by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Rename pdf file",
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
                        value = newName,
                        onValueChange = {
                            if (newName.length < 25) {
                                newName = it
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
                            Text(text = "Enter new name...", color = Color.LightGray)
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
                            // Dismiss the dialog box
                            showDialog = false
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
                            // On Rename clicked
                            showDialog = false
                            onRenameFileDone(newName)
                        },
                    ) {
                        // Positive button
                        Text(text = "Rename", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        )
    }
}