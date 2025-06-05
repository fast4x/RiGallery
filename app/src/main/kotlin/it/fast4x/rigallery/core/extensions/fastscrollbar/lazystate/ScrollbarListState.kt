package it.fast4x.rigallery.core.extensions.fastscrollbar.lazystate

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import it.fast4x.rigallery.core.extensions.fastscrollbar.ScrollbarState
import it.fast4x.rigallery.core.extensions.fastscrollbar.lazyutils.interpolateFirstItemIndex
import it.fast4x.rigallery.core.extensions.fastscrollbar.lazyutils.itemVisibilityPercentage
import it.fast4x.rigallery.core.extensions.fastscrollbar.scrollbarStateValue
import kotlin.math.min


@Composable
fun LazyListState.scrollbarState(// 根据[LazyListState]的变化来计算[ScrollbarState]。
    itemsAvailable: Int,// 总数
    itemIndex: (LazyListItemInfo) -> Int = LazyListItemInfo::index,
): ScrollbarState {
    val state = remember { ScrollbarState() }
    LaunchedEffect(this, itemsAvailable) {
        snapshotFlow {
            if (itemsAvailable == 0) return@snapshotFlow null

            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) return@snapshotFlow null

            // 第一个可见的项目 index
            val firstIndex = min(
                a = interpolateFirstItemIndex(
                    visibleItems = visibleItemsInfo,
                    itemSize = { it.size },
                    offset = { it.offset },
                    nextItemOnMainAxis = { first -> visibleItemsInfo.find { it != first } },
                    itemIndex = itemIndex,
                ),
                b = itemsAvailable.toFloat(),
            )
            if (firstIndex.isNaN()) return@snapshotFlow null

            // 一个页面可见的总项目数量
            val itemsVisible = visibleItemsInfo.floatSumOf { itemInfo ->
                itemVisibilityPercentage(
                    itemSize = itemInfo.size,
                    itemStartOffset = itemInfo.offset,
                    viewportStartOffset = layoutInfo.viewportStartOffset,
                    viewportEndOffset = layoutInfo.viewportEndOffset,
                )
            }

            // Float值1
            val thumbTravelPercent = min(
                // 最上面（第一个可见的），占总数的多少
                // 也就是当前滑动到那一项（最上面那一项所占百分比）
                a = firstIndex / itemsAvailable,
                b = 1f,
            )

            // Float值2
            val thumbSizePercent = min(
                // 一个页面可见的数量/ 总数量
                // 也就是单位页面所占百分比，一般固定...
                a = itemsVisible / itemsAvailable,
                b = 1f,
            )

            println("当前一个页面可见的数量：${itemsVisible}")

            // 得到最终值，保存 2 个 Float 值
            scrollbarStateValue(
                thumbSizePercent = thumbSizePercent,
                thumbMovedPercent = when {
                    layoutInfo.reverseLayout -> 1f - thumbTravelPercent
                    else -> thumbTravelPercent
                },
            )
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { state.onScroll(it) }// 传入可以打包的两个Float值
    }
    return state
}

private inline fun <T> List<T>.floatSumOf(selector: (T) -> Float): Float =
    fold(initial = 0f) { accumulator, listItem -> accumulator + selector(listItem) }
