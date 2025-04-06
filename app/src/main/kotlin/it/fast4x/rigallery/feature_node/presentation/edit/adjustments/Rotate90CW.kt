package it.fast4x.rigallery.feature_node.presentation.edit.adjustments

import android.graphics.Bitmap
import it.fast4x.rigallery.feature_node.domain.model.editor.Adjustment
import it.fast4x.rigallery.feature_node.presentation.util.rotate

data class Rotate90CW(
    val angle: Float
) : Adjustment {

    override fun apply(bitmap: Bitmap): Bitmap {
        return bitmap.rotate(angle)
    }

}