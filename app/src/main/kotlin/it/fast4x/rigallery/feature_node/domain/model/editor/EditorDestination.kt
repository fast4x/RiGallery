package it.fast4x.rigallery.feature_node.domain.model.editor

import it.fast4x.rigallery.feature_node.presentation.edit.adjustments.varfilter.VariableFilterTypes
import kotlinx.serialization.Serializable

@Serializable
sealed class EditorDestination {

    @Serializable
    data object Editor : EditorDestination()

    @Serializable
    data object Transform : EditorDestination()

    @Serializable
    data object Adjust : EditorDestination()

        @Serializable
        data class AdjustDetail(val adjustment: VariableFilterTypes) : EditorDestination()

    @Serializable
    data object Filters : EditorDestination()

    @Serializable
    data object Markup : EditorDestination()

        @Serializable
        data object ExternalEditor : EditorDestination()

}