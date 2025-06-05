package it.fast4x.rigallery.core.extensions.fastscrollbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.packFloats

@Immutable
@JvmInline
value class Scrollbar_Track(// // 轨道核心属性的类定义
    val packedValue: Long,
) {
    constructor(
        max: Float,
        min: Float,
    ) : this(packFloats(max, min))
}