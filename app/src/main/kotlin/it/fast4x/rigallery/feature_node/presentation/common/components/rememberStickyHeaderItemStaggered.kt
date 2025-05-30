package it.fast4x.rigallery.feature_node.presentation.common.components

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.util.titlecaseFirstCharIfItIsLowercase
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaItem
import it.fast4x.rigallery.feature_node.domain.model.isHeaderKey
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T: Media> rememberStickyHeaderItemStaggered(
    gridState: LazyStaggeredGridState,
    headers: SnapshotStateList<MediaItem.Header<T>>,
    mappedData: SnapshotStateList<MediaItem<T>>
): State<String?> {
    val stringToday = stringResource(id = R.string.header_today)
    val stringYesterday = stringResource(id = R.string.header_yesterday)
    val stringJanuary = stringResource(id = R.string.tag_january)
    val stringFebruary = stringResource(id = R.string.tag_february)
    val stringMarch = stringResource(id = R.string.tag_march)
    val stringApril = stringResource(id = R.string.tag_april)
    val stringMay = stringResource(id = R.string.tag_may)
    val stringJune = stringResource(id = R.string.tag_june)
    val stringJuly = stringResource(id = R.string.tag_july)
    val stringAugust = stringResource(id = R.string.tag_august)
    val stringSeptember = stringResource(id = R.string.tag_september)
    val stringOctober = stringResource(id = R.string.tag_october)
    val stringNovember = stringResource(id = R.string.tag_november)
    val stringDecember = stringResource(id = R.string.tag_december)

    /**
     * Remember last known header item
     */
    val stickyHeaderLastItem = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(gridState, headers, mappedData) {
        println("rememberStickyHeaderItemStaggered: mappedData = ${mappedData}")
        println("rememberStickyHeaderItemStaggered: headers = ${headers}")
        println("rememberStickyHeaderItemStaggered: stickyHeaderLastItem = ${gridState.layoutInfo.visibleItemsInfo}")

        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.map { it.offset } }.collectLatest {
            println("rememberStickyHeaderItemStaggered: offset = $it")
        }

        snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
            .collectLatest { visibleItems ->

                val firstItem = visibleItems.firstOrNull()
                val firstHeaderIndex = visibleItems.firstOrNull {
                    it.key.isHeaderKey && !it.key.toString().contains("big")
                }?.index

                val item = firstHeaderIndex?.let(mappedData::getOrNull)
                stickyHeaderLastItem.value = if (item != null && item is MediaItem.Header) {
                    val newItem = item.text
                        .replace("Today", stringToday)
                        .replace("Today", stringToday)
                        .replace("Yesterday", stringYesterday)
                        .replace("January", stringJanuary)
                        .replace("February", stringFebruary)
                        .replace("March", stringMarch)
                        .replace("April", stringApril)
                        .replace("May", stringMay)
                        .replace("June", stringJune)
                        .replace("July", stringJuly)
                        .replace("August", stringAugust)
                        .replace("September", stringSeptember)
                        .replace("October", stringOctober)
                        .replace("November", stringNovember)
                        .replace("December", stringDecember)
                        .titlecaseFirstCharIfItIsLowercase()
                    val newIndex = (headers.indexOf(item) - 1).coerceAtLeast(0)
                    val previousHeader = headers[newIndex].text
                        .replace("Today", stringToday)
                        .replace("Today", stringToday)
                        .replace("Yesterday", stringYesterday)
                        .replace("January", stringJanuary)
                        .replace("February", stringFebruary)
                        .replace("March", stringMarch)
                        .replace("April", stringApril)
                        .replace("May", stringMay)
                        .replace("June", stringJune)
                        .replace("July", stringJuly)
                        .replace("August", stringAugust)
                        .replace("September", stringSeptember)
                        .replace("October", stringOctober)
                        .replace("November", stringNovember)
                        .replace("December", stringDecember)
                        .titlecaseFirstCharIfItIsLowercase()
                    println("rememberStickyHeaderItemStaggered: newItem = $newItem, previousHeader = $previousHeader")
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
    println("rememberStickyHeaderItemStaggered: stickyHeaderLastItem = ${stickyHeaderLastItem.value}")
    return stickyHeaderLastItem
}