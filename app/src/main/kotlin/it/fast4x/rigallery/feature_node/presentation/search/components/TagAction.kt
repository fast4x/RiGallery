package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionItem
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionSheet
import it.fast4x.rigallery.feature_node.presentation.util.rememberAppBottomSheetState
import it.fast4x.rigallery.ui.theme.Shapes
import kotlinx.coroutines.launch

@Composable
fun TagAction(
    onSearch: () -> Unit = {},
    onSearchCombined: () -> Unit = {},
    onCombine: () -> Unit = {},
    onDismiss: () -> Unit = {},
    tag: String,
    tags: MutableList<String>,
    show: MutableState<Boolean>,
) {

    val appBottomSheetState = rememberAppBottomSheetState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val optionList = remember {
        mutableListOf(
            OptionItem(
                text = context.getString(R.string.menu_combine, tag),
                //containerColor = tertiaryContainer,
                //contentColor = onTertiaryContainer,
                onClick = {
                    onCombine()
                    onDismiss()
                    scope.launch {
                        appBottomSheetState.hide()
                    }
                }
            ),
            OptionItem(
                text = context.getString(R.string.menu_combine_and_search, tag),
                //containerColor = tertiaryContainer,
                //contentColor = onTertiaryContainer,
                onClick = {
                    onSearchCombined()
                    onDismiss()
                    scope.launch {
                        appBottomSheetState.hide()
                    }
                }
            ),
            OptionItem(
                text = context.getString(R.string.menu_search, tag),
                //containerColor = tertiaryContainer,
                //contentColor = onTertiaryContainer,
                onClick = {
                    onSearch()
                    onDismiss()
                    scope.launch {
                        appBottomSheetState.hide()
                    }
                }
            ),
        )
    }

    OptionSheet(
        state = appBottomSheetState,
        optionList = arrayOf(optionList),
        headerContent = {

                Text(
                    text = tag,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        //.fillMaxWidth()
                        .padding(4.dp)
                        .clip(Shapes.small)
                )

        }
    )


    LaunchedEffect(show.value) {
        if (show.value)
            appBottomSheetState.show()
        else appBottomSheetState.hide()
    }

//    DropdownMenu(
//        modifier = Modifier.padding(10.dp),
//        expanded = true,
//        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
//        shape = MaterialTheme.shapes.small,
//        properties = PopupProperties(
//            focusable = true,
//            dismissOnBackPress = true,
//            dismissOnClickOutside = true,
//            excludeFromSystemGesture = true,
//            usePlatformDefaultWidth = true,
//            clippingEnabled = true
//        ),
//        onDismissRequest = onDismiss,
//    ) {
//        DropdownMenuItem(
//            text = { Text(text = stringResource(R.string.menu_combine, tag)) },
//            onClick = {
//                onCombine()
//                onDismiss()
//            },
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Outlined.Check,
//                    contentDescription = null
//                )
//            }
//        )
//        DropdownMenuItem(
//            text = { Text(text = stringResource(R.string.menu_combine_and_search, tag)) },
//            onClick = {
//                onSearchCombined()
//                onDismiss()
//            },
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Outlined.Search,
//                    contentDescription = null
//                )
//            }
//        )
//        DropdownMenuItem(
//            text = { Text(text = stringResource(R.string.menu_search, tag)) },
//            onClick = {
//                onSearch()
//                onDismiss()
//            },
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Outlined.Search,
//                    contentDescription = null
//                )
//            }
//        )
//    }
}