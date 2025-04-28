package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.feature_node.presentation.library.components.dashedBorder

@Composable
fun HistoryItem(
    historyQuery: Pair<String, String>,
    search: (String) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {

    FilterChip(
        onClick = { search(historyQuery.second) },
        label = { Text(text = historyQuery.second) },
        selected = true,
        leadingIcon = {
            val icon = if (onDelete != null)
                Icons.Outlined.History
            else Icons.Outlined.Search
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        },
        trailingIcon = {
            if (onDelete != null) {
                IconButton(
                    onClick = {
                        val timestamp =
                            if (historyQuery.first.length < 10) "" else "${historyQuery.first}/"
                        onDelete("$timestamp${historyQuery.second}")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            }
        },
        colors = FilterChipDefaults.filterChipColors().copy(
            selectedContainerColor = MaterialTheme.colorScheme.background,
            selectedLabelColor = MaterialTheme.colorScheme.onBackground,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .dashedBorder(MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.medium, 1.dp, 4.dp, 2.dp)
            .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)

    )

//    InputChip(
//        onClick = { search(historyQuery.second) },
//        label = {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                Text(
//                    text = historyQuery.second
//
//                )
//            }
//        },
//        selected = true,
//        leadingIcon = {
////            val icon = if (onDelete != null)
////                Icons.Outlined.History
////            else Icons.Outlined.Search
////            Icon(
////                imageVector = icon,
////                contentDescription = null,
////                modifier = Modifier.size(InputChipDefaults.AvatarSize)
////            )
//        },
//        trailingIcon = {
//            if (onDelete != null) {
//                IconButton(
//                    onClick = {
//                        val timestamp =
//                            if (historyQuery.first.length < 10) "" else "${historyQuery.first}/"
//                        onDelete("$timestamp${historyQuery.second}")
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Outlined.Close,
//                        contentDescription = null,
//                        modifier = Modifier.size(InputChipDefaults.AvatarSize)
//                    )
//                }
//            }
//        },
//    )
//    SuggestionChip(
//        onClick = { search(historyQuery.second) },
//        label = {
//            Text(
//                text = historyQuery.second,
//                modifier = Modifier
//            )
//        }
//    )

//    ListItem(
//        headlineContent = {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = historyQuery.second,
//                    modifier = Modifier
//                        .weight(1f)
//                )
//                if (onDelete != null) {
//                    IconButton(
//                        onClick = {
//                            val timestamp = if (historyQuery.first.length < 10) "" else "${historyQuery.first}/"
//                            onDelete("$timestamp${historyQuery.second}")
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Outlined.Close,
//                            contentDescription = null
//                        )
//                    }
//                }
//            }
//        },
//        leadingContent = {
//            val icon = if (onDelete != null)
//                Icons.Outlined.History
//            else Icons.Outlined.Search
//            Icon(
//                imageVector = icon,
//                contentDescription = null
//            )
//        },
//        colors = ListItemDefaults.colors(
//            containerColor = Color.Transparent
//        ),
//        modifier = Modifier
//            .clickable { search(historyQuery.second) }
//            //.border(1.dp, MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.medium)
//            .dashedBorder(
//                color = MaterialTheme.colorScheme.onBackground,
//                shape = MaterialTheme.shapes.medium,
//                strokeWidth = 1.dp,
//                dashLength = 4.dp,
//                gapLength = 2.dp,
//                cap = StrokeCap.Round
//            )
//            .background(MaterialTheme.colorScheme.background)
//
//    )
//    Spacer(modifier = Modifier.height(8.dp))
}
