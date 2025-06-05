package it.fast4x.rigallery.core.extensions.fastscrollbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.painter.asEquitable
import it.fast4x.rigallery.R
import kotlinx.coroutines.delay
import it.fast4x.rigallery.core.extensions.fastscrollbar.ThumbStateType.Dormant
import it.fast4x.rigallery.core.extensions.fastscrollbar.ThumbStateType.Dragging
import it.fast4x.rigallery.core.extensions.fastscrollbar.ThumbStateType.Scrolling

@Composable
internal fun draggingColor():Color = MaterialTheme.colorScheme.tertiary.copy(0.8f)

@Composable
internal fun scrollingColor():Color = MaterialTheme.colorScheme.secondary.copy(0.8f)

internal val thumbShape = RoundedCornerShape(6.dp, 6.dp, 6.dp, 6.dp)

@Composable
fun ScrollableState.DraggableScrollbar(
    modifier: Modifier = Modifier,
    state: ScrollbarState,
    orientation: Orientation = Orientation.Vertical,
    currentIsFlingEnd: Boolean = true,
    isShowTrack: Boolean = true,
    isSupperSmall: Boolean = false,
    thumbSize: Dp = if (isSupperSmall) 3.dp else 8.dp,

    onThumbMoved: (Float, Float) -> Unit,
    context: Context,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val trackSize = thumbSize * 1.5f

    Scrollbar(
        modifier = modifier.run {
            when (orientation) {
                Vertical -> width(trackSize).fillMaxHeight()
                Horizontal -> height(trackSize).fillMaxWidth()
            }
        },
        orientation = orientation,
        isSupperSmall = isSupperSmall,
        interactionSource = interactionSource,
        isShowTrack = isShowTrack,
        state = state,
        thumb = {
            Box(
                modifier = Modifier
                    .offset(x = trackSize - thumbSize)
                    .run {
                        when (orientation) {
                            Vertical -> width(thumbSize).fillMaxHeight()
                            Horizontal -> height(thumbSize).fillMaxWidth()
                        }
                    }
                    .scrollThumb(
                        this,
                        interactionSource,
                        currentIsFlingEnd = currentIsFlingEnd,
                        isSupperSmall = isSupperSmall,
                        thumbSize = thumbSize,
                        context = context
                    ),
            )
        },
        onThumbMoved = onThumbMoved,
    )
}


@Composable
private fun Modifier.scrollThumb(
    scrollableState: ScrollableState,
    interactionSource: InteractionSource,
    isSupperSmall: Boolean = false,
    currentIsFlingEnd: Boolean = true,
    thumbSize: Dp = 0.dp,
    context: Context,
): Modifier {
    val colorStateOffsetXState = scrollbarThumbColor_OffsetX(
        scrollableState,
        interactionSource,
        isSupperSmall = isSupperSmall,
        currentIsFlingEnd = currentIsFlingEnd,
        thumbSize = thumbSize,
    )
    val colorState = colorStateOffsetXState.first

    val imageScroller = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.scroll
    )

    val that = this then Modifier
        .offset(x = colorStateOffsetXState.second.value)
        //NOT REQUIRED FOR NOW
//        .graphicsLayer {
//            alpha = 1f - (colorStateOffsetXState.second.value / thumbSize)
//        }
        .drawWithContent(
            onDraw = {
                drawImage(
                    image = imageScroller.asImageBitmap(),
                    dstSize = IntSize(
                        width = size.width.toInt(),
                        height = size.height.toInt()
                    ),
                    colorFilter = ColorFilter.tint(Color.White.copy(2f))
                )
            }
        )


    return that then ScrollThumbElement { colorState.value }
}

/**
 * The color of the scrollbar thumb as a function of its interaction state.
 * @param interactionSource source of interactions in the scrolling container
 */
enum class ThumbStateType {
    Scrolling,
    Dragging,
    Dormant,
}



@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@Composable
fun scrollbarThumbColor_OffsetX(
    scrollableState: ScrollableState,
    interactionSource: InteractionSource,
    isSupperSmall: Boolean = false,
    currentIsFlingEnd: Boolean = true,
    thumbSize: Dp = 0.dp,
): Pair<State<Color>, State<Dp>> {
    var state by remember { mutableStateOf(Dormant) }

    val pressed by interactionSource.collectIsPressedAsState()

    val hovered by interactionSource.collectIsHoveredAsState()

    val dragged by interactionSource.collectIsDraggedAsState()


    val dragging = (dragged || pressed || hovered)

    val isScrolling = /*!dragging ||*/
        (scrollableState.canScrollForward || scrollableState.canScrollBackward) &&
                scrollableState.isScrollInProgress

    println("测试currentIsFlingEnd = ${currentIsFlingEnd}")

    var startOffsetAnimation by remember { mutableStateOf(true) }
    when {
        dragging -> {
            state = Dragging
            startOffsetAnimation = false
        }
        isScrolling -> {
            state = Scrolling
            startOffsetAnimation = false
        }
        currentIsFlingEnd -> {
            state = Dormant
        }
    }

    val color = animateColorAsState(
        targetValue = when (state) {

            Dragging -> draggingColor()
            else -> scrollingColor()
        },
        animationSpec = SpringSpec(stiffness = Spring.StiffnessLow),
        label = "",
    )


    LaunchedEffect(state == Dormant) {
        println("滚动条状态监测: state = $state")
        if (state == Dormant) {
            delay(1500)
            startOffsetAnimation = true
        }
    }

    val offsetX = animateDpAsState(
        targetValue = when {
            startOffsetAnimation -> thumbSize
            else -> 0.dp
        },
        animationSpec = tween(durationMillis = 600),
        label = "",
    )
    return Pair(color, offsetX)
}


private data class ScrollThumbElement(val colorProducer: ColorProducer) :
    ModifierNodeElement<ScrollThumbNode>() {
    override fun create(): ScrollThumbNode = ScrollThumbNode(colorProducer)
    override fun update(node: ScrollThumbNode) {
        node.colorProducer = colorProducer
        node.invalidateDraw()
    }
}


private class ScrollThumbNode(
    var colorProducer: ColorProducer,
) : DrawModifierNode, Modifier.Node() {
    // naive cache outline calculation if size is the same
    private var lastSize: Size? = null
    private var lastLayoutDirection: LayoutDirection? = null
    private var lastOutline: Outline? = null

    override fun ContentDrawScope.draw() {
        val color = colorProducer()
        val outline =
            if (size == lastSize && layoutDirection == lastLayoutDirection) {
                lastOutline!!
            } else {
                thumbShape.createOutline(size, layoutDirection, this)
            }

        if (color != Color.Unspecified) drawOutline(outline, color = color)

        lastOutline = outline
        lastSize = size
        lastLayoutDirection = layoutDirection
    }
}

