package it.fast4x.rigallery.core.extensions.fastscrollbar.lazystate

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

/**
 * Remembers a function to react to [Scrollbar] thumb position displacements for a [LazyListState]
 * @param itemsAvailable the amount of items in the list.
 */
@Composable
fun LazyListState.rememberDraggableScroller(
    itemsAvailable: Int,
): (Float,Float) -> Unit = rememberDraggableScroller(
    itemsAvailable = itemsAvailable,
    scroll = ::scrollToItem,// LazyListState 里面的
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
    // 手指滚动位置的百分比
    var percentage by remember { mutableFloatStateOf(Float.NaN) }
    var maxPercentage by remember { mutableFloatStateOf(Float.NaN) }
    // 项目总数
    val itemCount by rememberUpdatedState(itemsAvailable)

    // 根据拖动（点击）的位置百分比，滚动到相应位置
    LaunchedEffect(percentage) {
        if (percentage.isNaN()) return@LaunchedEffect
        // 根据当前手指滚动所在位置的百分比，来寻找【要滚动到的那一项】
        // 计算滚动到哪一项：
        /*如果浮点数正好位于两个整数的中间（即“平局”情况），则四舍五入到正无穷方向的整数。
            特殊案例处理：
            如果Float的值大于Int.MAX_VALUE（即2,147,483,647），则四舍五入结果为Int.MAX_VALUE。
            如果Float的值小于Int.MIN_VALUE（即-2,147,483,648），则四舍五入结果为Int.MIN_VALUE。*/
        val indexToFind = (itemCount * percentage).roundToInt()
//        println("测试当前拖动处理百分比和要滚动到哪一个:${percentage2}-${itemCount * percentage}-${indexToFind}")

        // 计算完要滚动到哪一项之后，就执行滚动
//        val indexToFind2 = if(indexToFind>20) indexToFind+1 else indexToFind
//        val i2 = if (!maxPercentage.isNaN() && percentage>=maxPercentage) itemCount else indexToFind
        //scroll(indexToFind)
        // 如果是最后一个,就再往下偏移尽可能大的
        scroll(
            indexToFind,// 要滚动到哪一项
            if (!maxPercentage.isNaN() && percentage>=maxPercentage) 999 else 0// 如果已经滚动到最底部，但是实际并没有滚到，就手动设置999的偏移量
        )
    }
    return remember {
        // 函数：使用传来的值更新 当前拇指位置百分比
        { newPercentage,max ->
            percentage = newPercentage
            maxPercentage = max
        }
    }
}
