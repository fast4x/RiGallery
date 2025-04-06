package it.fast4x.rigallery.feature_node.domain.model.editor

import androidx.annotation.Keep
import androidx.compose.ui.graphics.ColorMatrix

@Keep
interface ImageFilter : Adjustment {

    fun colorMatrix(): ColorMatrix?
}