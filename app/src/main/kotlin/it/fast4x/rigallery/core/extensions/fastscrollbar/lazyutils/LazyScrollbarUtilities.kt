package it.fast4x.rigallery.core.extensions.fastscrollbar.lazyutils

import androidx.compose.foundation.gestures.ScrollableState
import kotlin.math.abs

/**
 * Linearly interpolates the index for the first item in [visibleItems] for smooth scrollbar
 * progression.
 * @param visibleItems a list of items currently visible in the layout.
 * @param itemSize a lookup function for the size of an item in the layout.
 * @param offset a lookup function for the offset of an item relative to the start of the view port.
 * @param nextItemOnMainAxis a lookup function for the next item on the main axis in the direction
 * of the scroll.
 * @param itemIndex a lookup function for index of an item in the layout relative to
 * the total amount of items available.
 *
 * @return a [Float] in the range [firstItemPosition..nextItemPosition) where nextItemPosition
 * is the index of the consecutive item along the major axis.
 * */
internal inline fun <LazyState : ScrollableState, LazyStateItem> LazyState.interpolateFirstItemIndex(
    visibleItems: List<LazyStateItem>,
    crossinline itemSize: LazyState.(LazyStateItem) -> Int,
    crossinline offset: LazyState.(LazyStateItem) -> Int,
    crossinline nextItemOnMainAxis: LazyState.(LazyStateItem) -> LazyStateItem?,
    crossinline itemIndex: (LazyStateItem) -> Int,
): Float {
    if (visibleItems.isEmpty()) return 0f

    val firstItem = visibleItems.first()
    val firstItemIndex = itemIndex(firstItem)

    if (firstItemIndex < 0) return Float.NaN

    val firstItemSize = itemSize(firstItem)
    if (firstItemSize == 0) return Float.NaN

    val itemOffset = offset(firstItem).toFloat()
    val offsetPercentage = abs(itemOffset) / firstItemSize

    val nextItem = nextItemOnMainAxis(firstItem) ?: return firstItemIndex + offsetPercentage

    val nextItemIndex = itemIndex(nextItem)

    return firstItemIndex + ((nextItemIndex - firstItemIndex) * offsetPercentage)
}


// 所有项目可见的百分比
internal fun itemVisibilityPercentage(
    itemSize: Int,// 项目数量
    itemStartOffset: Int,// 项目相对于视口开始处(viewportStartOffset)的总偏移量，一般就是项目的总偏移量

    viewportStartOffset: Int,// 视口开始处的偏移量，一般为 0
    viewportEndOffset: Int,// 是否底部的偏移量，一般就是视口高度
): Float {
    // 项目数量为 0 ——————可见百分比为 0f
    if (itemSize == 0) return 0f

    // 计算项目的结束位置
    val itemEnd = itemStartOffset + itemSize
    // 计算项目在视口开始处的偏移量（startOffset）和。
    // 开始----？---开始[   ]----------------------
    val startOffset = when {
        // 如果itemStartOffset大于viewportStartOffset，表示项目在视口的起始位置之前，因此startOffset为0。
        itemStartOffset > viewportStartOffset -> 0
        // 否则，计算项目起始位置和视口起始位置之间的绝对差值。
        else -> abs(abs(viewportStartOffset) - abs(itemStartOffset))
    }
    // 在视口结束处的偏移量（endOffset）
    val endOffset = when {
        // 如果项目结束位置小于viewportEndOffset，表示项目在视口的结束位置之前，因此endOffset为0。
        itemEnd < viewportEndOffset -> 0
        // 否则，计算项目结束位置和视口结束位置之间的绝对差值。
        // ------[   ]结束------？-----结束
        else -> abs(abs(itemEnd) - abs(viewportEndOffset))
    }

    // 每一项占据的px大小
    val size = itemSize.toFloat()
    return (size - startOffset - endOffset) / size
}
