package com.r.dosc.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r.dosc.domain.components.SortDropDownMenu
import com.r.dosc.domain.ui.theme.Helper_Text_Color

@Composable
fun HelperTabLayout(
    selectedId: Int,
    onSortIdSelect: (Int) -> Unit
) {
    var click by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, end = 12.dp, start = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(5f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Documents ", color = Helper_Text_Color)
        }

        Row(
            modifier = Modifier
                .weight(2f)
                .clickable {
                    click = true
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "filter_list",
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(text = "Sort")

            if (click) {
                SortDropDownMenu(
                    modifier = Modifier,
                    selectedId = selectedId,
                    expanded = true,
                    onDismissRequest = {
                        click = false

                    },
                    onTitleSelect = {
                        onSortIdSelect(1)
                        click = false

                    },
                    onDateSelect = {
                        click = false
                        onSortIdSelect(2)

                    }
                )
            }
        }
    }
}