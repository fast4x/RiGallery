package it.fast4x.rigallery.feature_node.presentation.edit.adjustments

import android.graphics.Bitmap
import it.fast4x.rigallery.feature_node.domain.model.editor.Adjustment
import it.fast4x.rigallery.feature_node.presentation.util.flipHorizontally
import it.fast4x.rigallery.feature_node.presentation.util.flipVertically

data class Flip(
    val horizontal: Boolean,
) : Adjustment {

    override fun apply(bitmap: Bitmap): Bitmap {
        return if (horizontal) bitmap.flipHorizontally() else bitmap.flipVertically()
    }

}