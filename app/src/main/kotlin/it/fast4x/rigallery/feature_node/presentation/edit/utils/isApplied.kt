package it.fast4x.rigallery.feature_node.presentation.edit.utils

import androidx.annotation.Keep
import it.fast4x.rigallery.feature_node.domain.model.editor.Adjustment
import it.fast4x.rigallery.feature_node.presentation.edit.adjustments.varfilter.VariableFilterTypes

@Keep
fun List<Adjustment>.isApplied(variableFilterTypes: VariableFilterTypes): Boolean {
    return any { it.name == variableFilterTypes.name }
}