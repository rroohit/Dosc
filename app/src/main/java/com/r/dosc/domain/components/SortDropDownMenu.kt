package com.r.dosc.domain.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r.dosc.domain.models.SortDropDownList

val dropDownList = listOf(
    SortDropDownList.Title(),
    SortDropDownList.Date()
)

@Composable
fun SortDropDownMenu(
    modifier: Modifier,
    selectedId: Int,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onTitleSelect: () -> Unit,
    onDateSelect: () -> Unit
) {

    LaunchedEffect(true) {
        when (selectedId){
            1 -> {
                dropDownList[0].isSelected = true
                dropDownList[1].isSelected = false

            }
            2 -> {
                dropDownList[0].isSelected = false
                dropDownList[1].isSelected = true

            }
        }
    }

    var isExpanded by remember {
        mutableStateOf(expanded)
    }

    DropdownMenu(
        modifier = modifier,
        expanded = isExpanded,
        onDismissRequest = {
            onDismissRequest()
            isExpanded = false
        }
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = "Sort By"
        )
        dropDownList.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    isExpanded = when (item) {
                        is SortDropDownList.Title -> {
                            dropDownList[0].isSelected = true
                            dropDownList[1].isSelected = false
                            onTitleSelect()
                            false
                        }
                        is SortDropDownList.Date -> {
                            dropDownList[0].isSelected = false
                            dropDownList[1].isSelected = true
                            onDateSelect()
                            false
                        }
                    }
                }
            ) {
                Text(
                    modifier = Modifier.weight(1.4f),
                    text = item.name,
                )
                RadioButton(
                    modifier = Modifier.weight(1f),
                    selected = item.id == selectedId,
                    onClick = {
                        when (item.id){
                            1 -> {
                                dropDownList[0].isSelected = true
                                dropDownList[1].isSelected = false
                                onTitleSelect()

                            }
                            2 -> {
                                dropDownList[0].isSelected = false
                                dropDownList[1].isSelected = true
                                onDateSelect()

                            }
                        }
                        isExpanded = false
                        onDismissRequest()
                    }
                )
            }
        }
    }

}