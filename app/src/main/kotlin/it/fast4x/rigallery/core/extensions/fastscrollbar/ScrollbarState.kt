package it.fast4x.rigallery.core.extensions.fastscrollbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2

class ScrollbarState {
    private var packedValue by mutableLongStateOf(0L)

    // 传过来可以打包的值
    internal fun onScroll(stateValue: Scrollbar_StateValue) {
        packedValue = stateValue.packedValue
    }
    // 返回【滚动条的拇指】大小占【总轨道】大小的百分比
    // -----------[-?%-]---------------------
    // 也就是【拇指它本身】---这个长度固定，比如：0.108945064
    val thumbSizePercent
        get() = unpackFloat1(packedValue)// 解包：获取包里面的第一个值

    // 返回【拇指可以自由移动的最大距离】占【总轨迹大小】的百分比
    // [---]----------------?%----------------
    // 也就是【总路径 - 拇指长度】---这个长度也固定，比如：0.8910549
    val thumbTrackSizePercent
        get() = 1f - thumbSizePercent

    // 返回【拇指已经移动的距离】占【总轨迹大小】的百分比，最大可能也就0.84..（测试值）
    // -----?%------[---]---------------------
    // 也就是【拇指走过的路程】
    val thumbMovedPercent
        get() = unpackFloat2(packedValue)// 解包：获取包里面的第2个值

}