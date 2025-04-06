package it.fast4x.rigallery.feature_node.presentation.edit.adjustments

import android.graphics.Bitmap
import it.fast4x.rigallery.feature_node.domain.model.editor.Adjustment

data class Markup(val newBitmap: Bitmap): Adjustment {

    override fun apply(bitmap: Bitmap): Bitmap {
        return newBitmap
    }

}