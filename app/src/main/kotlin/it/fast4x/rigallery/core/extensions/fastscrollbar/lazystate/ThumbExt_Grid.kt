package it.fast4x.rigallery.core.extensions.fastscrollbar.lazystate

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

/**
 * Remembers a function to react to [Scrollbar] thumb position displacements for a [LazyGridState]
 * @param itemsAvailable the amount of items in the grid.
 */
@Composable
fun LazyGridState.rememberDraggableScroller(
    itemsAvailable: Int,
): (Float,Float) -> Unit = rememberDraggableScroller(
    itemsAvailable = itemsAvailable,
    scroll = ::scrollToItem,
)

/**
 * Generic function to react to [Scrollbar] thumb displacements in a lazy layout.
 * @param itemsAvailable the total amount of items available to scroll in the layout.
 * @param scroll a function to be invoked when an index has been identified to scroll to.
 */
@Composable
private inline fun rememberDraggableScroller(
    itemsAvailable: Int,
    crossinline scroll: suspend (index: Int,scrollOffset:Int) -> Unit,
): (Float,Float) -> Unit {
    var percentage by remember { mutableFloatStateOf(Float.NaN) }
    var maxPercentage by remember { mutableFloatStateOf(Float.NaN) }
    val itemCount by rememberUpdatedState(itemsAvailable)

    LaunchedEffect(percentage) {
        if (percentage.isNaN()) return@LaunchedEffect
        val indexToFind = (itemCount * percentage).roundToInt()

        // 进行滚动
        scroll(
            indexToFind,// 要滚动到哪一项
            if (!maxPercentage.isNaN() && percentage>=maxPercentage) 999 else 0// 如果已经滚动到最底部，但是实际并没有滚到，就手动设置999的偏移量
        )
    }
    return remember {
        { newPercentage,max ->
            percentage = newPercentage
            maxPercentage = max
        }
    }
}
