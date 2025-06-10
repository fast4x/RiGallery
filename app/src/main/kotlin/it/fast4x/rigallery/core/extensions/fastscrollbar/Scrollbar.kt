package it.fast4x.rigallery.core.extensions.fastscrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 *一个用于绘制滚动条的compose控件
 * @param orientation滚动条的滚动方向
 * @param state描述滚动条位置的状态
 * @param minThumbSize滚动条拇指的最小尺寸
 * @param interactionSource允许观察滚动条的状态
 * @param thumb一个用于绘制滚动条thumb的组合组件
 * @param onThumbMoved一个函数，用于响应由direct引起的滚动条位移
 *用户对滚动条拇指的交互，例如实现快速滚动
 */
@Composable
fun Scrollbar(
    modifier: Modifier = Modifier,
    isShowTrack: Boolean,
    isSupperSmall: Boolean,
    //itemSize: Int,
    orientation: Orientation,
    state: ScrollbarState,
    interactionSource: MutableInteractionSource? = null,
    minThumbSize: Dp = 50.dp,
    onThumbMoved: ((Float, Float) -> Unit)? = null,
    thumb: @Composable () -> Unit,
) {

    // 使用[Offset.Unspecified]和[Float.NaN]，而不是null，以防止对基本类型进行不必要的装箱
    var pressedOffset by remember { mutableStateOf(Offset.Unspecified) }
    var longPressedOffset by remember { mutableStateOf(Offset.Unspecified) }
    var draggedOffset by remember { mutableStateOf(Offset.Unspecified) }

    // 用于在滚动实现跟上时立即在UI中显示拖动反馈
    var interactionThumbTravelPercent by remember { mutableFloatStateOf(Float.NaN) }
    // 轨道的大小
    var track by remember { mutableStateOf(Scrollbar_Track(packedValue = 0)) }

    val density = LocalDensity.current
    // 1. 拇指的长度（高度）
    val thumbSizePx = if (isSupperSmall) max(
        a = state.thumbSizePercent * track.size,// 动态
        b = with(density) { minThumbSize.toPx() },// 最小大小
    ) else with(density) { 50.dp.toPx() }// 长度（目前之间固定就好，如果想设置动态并且设置最小大小，也可以使用下面的代码）
    // 滚动条【轨道容器】
    //----------------------------滚动条所在的容器：也就是轨道------------------------------------//
    Box(
        modifier = modifier//.background(if (isShowTrack) Color.LightGray.copy(0.55f) else Color.Transparent)
            .run {
                val withHover = interactionSource?.let(::hoverable) ?: this
                when (orientation) {
                    Vertical -> withHover.fillMaxHeight()
                    Horizontal -> withHover.fillMaxWidth()
                }
            }
            // 获取轨道的宽高....
            .onGloballyPositioned { coordinates ->
                val scrollbarStartCoordinate = orientation.valueOf(coordinates.positionInRoot())
                track = Scrollbar_Track(
                    max = scrollbarStartCoordinate,
                    min = scrollbarStartCoordinate + orientation.valueOf(coordinates.size),
                )
            }
            // Process scrollbar presses
            /*---------------------按下（点击），-------------------------*/
            .pointerInput(Unit) {
                // 如果是超小模式，则不可以点击、拖动滚动条（应该只是作为装饰使用的！）
                if (isSupperSmall) return@pointerInput
                /*------------------------------点击（长按）-----------------------------------------*/
                // 说明：未来防止点击误触，所以点击轨道，并不会定位。只有点击并长按之后才会进行定位。
                // 点击只是点击
                /*detectTapGestures(
                    onPress = { offset ->
                        // 点击方式
                        val initialPress = PressInteraction.Press(offset)
                        interactionSource?.tryEmit(initialPress)// 将这个点击的交互，发送给交互源 interactionSource
                        pressedOffset = offset
                        interactionSource?.tryEmit(
                            when {
                                tryAwaitRelease() -> PressInteraction.Release(initialPress)
                                else -> PressInteraction.Cancel(initialPress)
                            },
                        )
                        // 停止按下
                        pressedOffset = Offset.Unspecified

                    },
                )*/
            }
            // Process scrollbar drags
            /*---------------------拖动，-------------------------*/
            .pointerInput(Unit) {
                if (isSupperSmall) return@pointerInput

                // 定义拖动交互对象，用于存储拖动交互信息
                var dragInteraction: DragInteraction.Start? = null

                // ——————拖动交互开始的回调
                val onDragStart: (Offset) -> Unit = { offset ->
                    val start = DragInteraction.Start()// 拖动交互开始对象
                    dragInteraction = start
                    interactionSource?.tryEmit(start)// 将这个对象传给【交互源】
                    draggedOffset =
                        offset //+ Offset(-thumbSizePxHalf,-thumbSizePxHalf)// 将拖动偏移量存到这个
                }
                // ——————拖动结束
                val onDragEnd: () -> Unit = {
                    dragInteraction?.let { interactionSource?.tryEmit(DragInteraction.Stop(it)) }
                    draggedOffset = Offset.Unspecified
                }
                // ——————拖动取消
                val onDragCancel: () -> Unit = {
                    dragInteraction?.let { interactionSource?.tryEmit(DragInteraction.Cancel(it)) }
                    draggedOffset = Offset.Unspecified
                }
                // ——————正在拖动...
                val onDrag: (change: PointerInputChange, dragAmount: Float) -> Unit =
                    onDrag@{ change, delta ->
                        if (draggedOffset == Offset.Unspecified) return@onDrag
                        draggedOffset = when (orientation) {
                            Vertical -> draggedOffset.copy(
                                // 使用绝对位置坐标——————而不是相对偏移量
                                y = change.position.y//draggedOffset.y + delta,
                            )

                            Horizontal -> draggedOffset.copy(
                                x = change.position.y//draggedOffset.x + delta,
                            )
                        }
                    }

                when (orientation) {
                    Horizontal -> detectHorizontalDragGestures(
                        onDragStart = onDragStart,
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragCancel,
                        onHorizontalDrag = onDrag,
                    )

                    Vertical -> detectVerticalDragGestures(
                        onDragStart = onDragStart,
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragCancel,
                        onVerticalDrag = onDrag,
                    )
                }
            },
    ) {
        // 轨道内的————滚动条【拇指容器】——自定义布局

        Layout(content = { thumb() }) { measurables, constraints ->
            val measurable = measurables.first()

            // 拇指当前移动所在的轨道上位置的百分比，最大值不会超过某个值，比如0.8（80%）
            val thumbTrackSizePercent = state.thumbTrackSizePercent
            val thumbTravelPercent = max(
                a = when {
                    // 页面自己滚动,使用这个值(释放拖动的时候)
                    interactionThumbTravelPercent.isNaN() -> {
                        //state.thumbMovedPercent // 这个值最高约0.84
                        // 根据线性插值公式：y = y0 + (x - x0)/(x1 - x0)*(y1-y0)
                        // x0 =  0% ,y0 = 0%
                        // x1 =  最高值0.84 = 84% ,y0 = 100%
                        // 代入公式：y = （x/最高值） * 100，但是我们不是计算百分比，所以不需要乘以100
                        // 所以： y = x/最高值
                        // 注意：当总项目数量不足一个页面的时候，thumbTrackSizePercent == 0f
                        if (thumbTrackSizePercent != 0f)
                            min(
                                a = state.thumbMovedPercent / state.thumbTrackSizePercent,
                                b = 1f,
                            )
                        else
                            state.thumbMovedPercent
                    }
                    // 手动拖动拇指
                    else -> interactionThumbTravelPercent// 这个值最高可达 1f
                },
                b = 0f,// 保证不会小于 0 %
            )
            // 拇指的位置 = 轨道的高度 x 拇指当前移动位置的百分比
            val thumbMovedPx = (track.size - thumbSizePx) * thumbTravelPercent

            // 布局容器的
            val updatedConstraints = when (orientation) {
                Horizontal -> {
                    constraints.copy(
                        minWidth = thumbSizePx.roundToInt(),
                        maxWidth = thumbSizePx.roundToInt(),
                    )
                }

                Vertical -> {
                    constraints.copy(
                        minHeight = thumbSizePx.roundToInt(),
                        maxHeight = thumbSizePx.roundToInt(),
                    )
                }
            }

            val y = when (orientation) {
                Horizontal -> 0
                Vertical -> max(
                    a = thumbMovedPx.roundToInt(),
                    b = 0,
                )
            }

            println("Scrollbar: thumbMovedPx = $thumbMovedPx y = $y")
            println("Scrollbar: constraints = ${constraints}")

            val x = when (orientation) {
                Horizontal -> thumbMovedPx.roundToInt()
                Vertical -> 0
            }

            val placeable = measurable.measure(updatedConstraints)
            //println("Scrollbar: placeable width--height = ${placeable.width}--${placeable.height}")
            layout(placeable.width, placeable.height) {
                // 放置布局里面的内容孩子（拇指）
                placeable.place(x, y)
            }
        }
    }


    if (onThumbMoved == null) return
    // 函数：根据偏移量更新数据
    val updateData: (Offset) -> Unit = { offset ->
        val currentTravel = track.thumbPosition(
            // 传入当前拖动的偏移量尺寸
            dimension = orientation.valueOf(offset),
            thumbSizePx = thumbSizePx,
        )
        // 1、通知页面同步滚动
        // 此处也需要使用线性插值：当拇指滚动到约84%的时候，屏幕上的就应该滚动到 100%了，
        // 所以就是需要乘以一个数
        // 但是注意，因为我们需要减去拇指本身的距离，所以，
        onThumbMoved(
            currentTravel * (state.thumbTrackSizePercent),
            state.thumbTrackSizePercent,
        )// 传入,注意它使用的不是线性插值版本
        // 2、通知拇指本身同步滚动
        interactionThumbTravelPercent =
            currentTravel //- (state.thumbSizePercent * state.thumbMovedPercent)
    }
    // Process presses
    //-----------------------处理点击的-------------------------------------//
    LaunchedEffect(Unit) {
        //将 Compose 的 State 转换为 Flow
        snapshotFlow { pressedOffset }.collect { pressedOffset ->
            if (pressedOffset == Offset.Unspecified) {
                interactionThumbTravelPercent = Float.NaN
                return@collect
            }
            // 计算用户交互中的拇指的位置（也就是用户点击拖动之后的位置）
            updateData(pressedOffset)
        }
    }
    // Process drags
    // 拖动的进度————处理拖动事件
    LaunchedEffect(Unit) {
        snapshotFlow { draggedOffset }.collect { draggedOffset ->
            if (draggedOffset == Offset.Unspecified) {
                interactionThumbTravelPercent = Float.NaN
                return@collect
            }

            val currentTravel1 = track.thumbPosition0(
                // 传入当前拖动的偏移量尺寸
                dimension = orientation.valueOf(draggedOffset),
            )
            updateData(draggedOffset)
        }
    }
}

// 当用户长按【滚动条轨道】而不是拖动滚动条拇指时，滚动之间的延迟。
private const val SCROLLBAR_PRESS_DELAY_MS = 10L

// 当长按【滚动条轨道】时滚动条的位移百分比。
private const val SCROLLBAR_PRESS_DELTA_PCT = 0.02f

/*unpackFloat2函数是Android Jetpack Compose UI工具库中的一个辅助函数，
    它的作用是从一个由两个Float值打包成的Long值中提取出第二个Float值。
    这个函数的存在是为了在需要将两个Float值存储为一个Long值以节省空间或满足特定API要求时，能够方便地进行打包和解包操作。

    在Compose UI或其他需要高效处理浮点数对的场景中，packFloats和unpackFloat1、unpackFloat2这类函数非常有用。
    例如，在处理坐标、尺寸或颜色渐变等需要成对处理浮点数的情况下，能够通过这种方式减少内存占用，并可能提升性能。
    */
// 以像素为单位返回【滚动条轨道】的大小
// 用于计算两个浮点数之间的差值，例如在滚动条轨道的尺寸计算中，其中第一个浮点数可能代表轨道的起始位置，而第二个浮点数可能代表轨道的结束位置。
// 差值 Scrollbar_Track.size 就是轨道的长度。
/*这段代码使用了unpackFloat1和unpackFloat2函数，这两个函数分别从同一个packedValue中解包出两个Float值。
    这个packedValue是一个Long类型的值，它通过之前某处的packFloats函数调用将两个Float值打包成一个Long值。
    这两个Float值通常代表着某些与滚动条轨道相关的度量，比如开始位置和结束位置或者宽度和高度。

    unpackFloat1(packedValue)从packedValue中解包出第一个Float值。
    unpackFloat2(packedValue)从packedValue中解包出第二个Float值。

    Scrollbar_Track.size通过获取这两个解包出的Float值之间的差值来计算大小。
    具体来说，它通过从第二个Float值中减去第一个Float值来得到这个差值。
    这个差值反映了某种空间维度的大小，可能是长度、宽度或高度，这取决于packedValue中原本两个Float值的具体含义。
*/
private val Scrollbar_Track.size
    get() = unpackFloat2(packedValue) - unpackFloat1(packedValue)

// 普通版：以百分比形式返回【滚动条滑块】在【轨道上的位置】
private fun Scrollbar_Track.thumbPosition0(
    dimension: Float,// 当前的偏移值
): Float {
    return max(
//    // 当前偏移量 ➗ 总长度 = 百分比
        a = min(a = dimension / size, b = 1f),
        b = 0f,
    )
}

// 以百分比形式返回【滚动条滑块】在【轨道上的位置】
private fun Scrollbar_Track.thumbPosition(
    dimension: Float,// 当前的偏移值
    thumbSizePx: Float,// 拇指的距离
): Float {

    /*
    * 线性插值是一种在两个数值之间按比例分配的方法
    * 当 偏移量在 【0 ~ 一半拇指】的时候，计算的百分比为 0%
    * 当 偏移量在 【总长度 - 一半拇指 ~ 总长度】的时候，计算的百分比为 100%
    * 当 偏移量在 【中间情况的】的时候，计算的百分比均匀的在 0% ~ 100% 范围内变化
    * 线性插值的公式是：y = y0 + (x-x0)/(x1-x0) * (y1 - y0)
    * 其中 x = dimension
    *     x0、x1 是值的起始和结束点，这里分别是 x0 = thumbSizePxHalf 和 x1 = size-thumbSizePxHalf。
    *     y0 y1 是x0 x1对应的计算结果百分比，这里分别是 0% 和 100%
    * 带入计算公式，就得到最后的百分比值：
    * y = (x - x0)/(x1 - x0) * 100
    * y = (dimension - thumbSizePxHalf)/(size-thumbSizePxHalf - thumbSizePxHalf) * 100
    *   = (dimension - thumbSizePxHalf)/(size - thumbSizePxHalf*2f)*100
    *
    * 因为此处我们计算的是0f~1f，所以不需要乘以 100(其实是因为乘以1.0f = 100%不需要写而已)
    * */
    return max(
        a = min(
            a = (dimension - thumbSizePx / 2) / (size - thumbSizePx),
            b = 1f,// 不能超过这个数
        ),
        b = 0f,
    )
}

// 返回[offset]沿[this]指定的轴方向的值。
internal fun Orientation.valueOf(offset: Offset) =
    when (this) {
        Horizontal -> offset.x
        Vertical -> offset.y
    }

// 返回[intSize]沿[this]指定的轴方向的值
internal fun Orientation.valueOf(intSize: IntSize) =
    when (this) {
        Horizontal -> intSize.width
        Vertical -> intSize.height
    }

// 返回[intOffset]沿[this]指定轴方向的值。
internal fun Orientation.valueOf(intOffset: IntOffset) =
    when (this) {
        Horizontal -> intOffset.x
        Vertical -> intOffset.y
    }