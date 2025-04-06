package it.fast4x.rigallery.feature_node.presentation.common.components

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaItem
import it.fast4x.rigallery.feature_node.domain.model.isHeaderKey

@Composable
fun <T: Media> rememberStickyHeaderItem(
    gridState: LazyGridState,
    headers: SnapshotStateList<MediaItem.Header<T>>,
    mappedData: SnapshotStateList<MediaItem<T>>
): State<String?> {
    val stringToday = stringResource(id = R.string.header_today)
    val stringYesterday = stringResource(id = R.string.header_yesterday)

    /**
     * Remember last known header item
     */
    val stickyHeaderLastItem = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(gridState, headers, mappedData) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val firstItem = visibleItems.firstOrNull()
                val firstHeaderIndex = visibleItems.firstOrNull {
                    it.key.isHeaderKey && !it.key.toString().contains("big")
                }?.index

                val item = firstHeaderIndex?.let(mappedData::getOrNull)
                stickyHeaderLastItem.value = if (item != null && item is MediaItem.Header) {
                    val newItem = item.text
                        .replace("Today", stringToday)
                        .replace("Yesterday", stringYesterday)
                    val newIndex = (headers.indexOf(item) - 1).coerceAtLeast(0)
                    val previousHeader = headers[newIndex].text
                        .replace("Today", stringToday)
                        .replace("Yesterday", stringYesterday)
                    if (firstItem != null && !firstItem.key.isHeaderKey) {
                        previousHeader
                    } else {
                        newItem
                    }
                } else {
                    stickyHeaderLastItem.value
                }
            }
    }
    return stickyHeaderLastItem
}