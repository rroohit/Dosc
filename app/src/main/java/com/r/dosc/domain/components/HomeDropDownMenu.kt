package com.r.dosc.domain.components

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.r.dosc.domain.models.HomeItemDropDownList

val itemList = listOf(
    HomeItemDropDownList.Share(),
    HomeItemDropDownList.Delete(),
)

@Composable
fun DropDownMenu(
    onDeleteCheck:Boolean,
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    onShare: () -> Unit,
    onDelete: @Composable (HomeItemDropDownList) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(true)
    }

    var onDeleteClicked by remember {
        mutableStateOf(onDeleteCheck)
    }

    DropdownMenu(
        modifier = modifier,
        expanded = isExpanded,
        onDismissRequest = {
            isExpanded = false
            onDismissRequest()
        }
    ) {

        itemList.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    when (item) {
                        is HomeItemDropDownList.Share -> {
                            onShare()
                            isExpanded = false

                            onDismissRequest()

                        }
                        is HomeItemDropDownList.Delete -> {
                            onDeleteClicked = true
                        }
                    }
                },
            ) {
                Text(text = item.name)
            }

        }

    }

    if (onDeleteClicked) {
        onDelete(HomeItemDropDownList.Delete())
        isExpanded = false

    }

}

