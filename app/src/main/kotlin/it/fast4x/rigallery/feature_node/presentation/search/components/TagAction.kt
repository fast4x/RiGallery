package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import it.fast4x.rigallery.R

@Composable
fun TagAction(
    onSearch: () -> Unit = {},
    onSearchCombined: () -> Unit = {},
    onCombine: () -> Unit = {},
    onDismiss: () -> Unit = {},
    tag: String,
) {
    DropdownMenu(
        modifier = Modifier.padding(10.dp),
        expanded = true,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.small,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            excludeFromSystemGesture = true,
            usePlatformDefaultWidth = true,
            clippingEnabled = true
        ),
        onDismissRequest = onDismiss,
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.menu_combine, tag)) },
            onClick = {
                onCombine()
                onDismiss()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.menu_combine_and_search, tag)) },
            onClick = {
                onSearchCombined()
                onDismiss()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.menu_search, tag)) },
            onClick = {
                onSearch()
                onDismiss()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            }
        )
    }
}