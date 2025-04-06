package it.fast4x.rigallery.feature_node.domain.model.editor

import android.graphics.Bitmap
import androidx.annotation.Keep
import it.fast4x.rigallery.feature_node.presentation.util.sentenceCase

@Keep
interface Adjustment {
    fun apply(bitmap: Bitmap): Bitmap

    val name: String get() = this::class.simpleName.toString().sentenceCase()

}