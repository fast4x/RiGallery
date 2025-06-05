package it.fast4x.rigallery.core.extensions.fastscrollbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.packFloats


@Immutable
@JvmInline
value class Scrollbar_StateValue internal constructor(// 滚动条核心属性的类定义
    internal val packedValue: Long,
)

fun scrollbarStateValue(
    thumbSizePercent: Float,// 滚动条的拇指大小占总轨道大小的百分比。 指的是滑块宽度(用于水平滚动条)或高度(用于垂直滚动条)。
    thumbMovedPercent: Float,// 拇指移动的距离占总轨道距离的百分比
) = Scrollbar_StateValue(// 使用列出的参数属性，创建一个Scrollbar_StateValue对象
    packFloats(// 把这两个 Float 值打包，在需要使用的时候再解包
        val1 = thumbSizePercent,
        val2 = thumbMovedPercent,
    ),
)