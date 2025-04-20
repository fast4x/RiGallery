package it.fast4x.rigallery.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import it.fast4x.rigallery.core.enums.Option
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionItem
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionSheet
import it.fast4x.rigallery.feature_node.presentation.util.rememberAppBottomSheetState
import kotlinx.coroutines.launch
import kotlin.enums.EnumEntries

@Composable
fun OptionSheetMenu(
    title: String,
    options: List<Option>,
    visible: Boolean,
    onSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val scope = rememberCoroutineScope()
    val appBottomSheetState = rememberAppBottomSheetState()
    LaunchedEffect(appBottomSheetState.isVisible, visible) {
        scope.launch {
            if (visible) appBottomSheetState.show()
            else appBottomSheetState.hide()
        }
    }

    val optionList = remember { mutableStateListOf<OptionItem>() }
    optionList.clear()
    options.forEach { option ->
        optionList += OptionItem(
            icon = option.icon,
            text = option.name,
            onClick = {
                onSelected(option.ordinal)
                onDismiss()
            }
        )
    }

    OptionSheet(
        state = appBottomSheetState,
        onDismiss = onDismiss,
        headerContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        },
        optionList = arrayOf(optionList)
    )

}