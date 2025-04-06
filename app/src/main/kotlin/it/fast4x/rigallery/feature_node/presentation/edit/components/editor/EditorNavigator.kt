package it.fast4x.rigallery.feature_node.presentation.edit.components.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import it.fast4x.rigallery.core.Constants.Animation.navigateInAnimation
import it.fast4x.rigallery.core.Constants.Animation.navigateUpAnimation
import it.fast4x.rigallery.feature_node.domain.model.editor.Adjustment
import it.fast4x.rigallery.feature_node.domain.model.editor.DrawMode
import it.fast4x.rigallery.feature_node.domain.model.editor.DrawType
import it.fast4x.rigallery.feature_node.domain.model.editor.EditorDestination
import it.fast4x.rigallery.feature_node.domain.model.editor.EditorItems.Adjust
import it.fast4x.rigallery.feature_node.domain.model.editor.EditorItems.Crop
import it.fast4x.rigallery.feature_node.domain.model.editor.EditorItems.Filters
import it.fast4x.rigallery.feature_node.domain.model.editor.EditorItems.Markup
import it.fast4x.rigallery.feature_node.domain.model.editor.ImageFilter
import it.fast4x.rigallery.feature_node.domain.model.editor.PathProperties
import it.fast4x.rigallery.feature_node.presentation.edit.adjustments.varfilter.VariableFilterTypes
import it.fast4x.rigallery.feature_node.presentation.edit.components.adjustment.AdjustScrubber
import it.fast4x.rigallery.feature_node.presentation.edit.components.adjustment.AdjustSection
import it.fast4x.rigallery.feature_node.presentation.edit.components.cropper.CropperSection
import it.fast4x.rigallery.feature_node.presentation.edit.components.filters.FiltersSelector
import it.fast4x.rigallery.feature_node.presentation.edit.components.markup.MarkupSelector
import kotlin.math.roundToInt

@Composable
fun EditorNavigator(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appliedAdjustments: List<Adjustment>,
    targetImage: Bitmap?,
    targetUri: Uri?,
    onAdjustItemLongClick: (VariableFilterTypes) -> Unit = {},
    onAdjustmentChange: (Adjustment) -> Unit = {},
    onAdjustmentPreview: (Adjustment) -> Unit = {},
    onToggleFilter: (ImageFilter) -> Unit = {},
    startCropping: () -> Unit = {},
    drawMode: DrawMode,
    setDrawMode: (DrawMode) -> Unit,
    drawType: DrawType,
    setDrawType: (DrawType) -> Unit,
    currentPathProperty: PathProperties,
    setCurrentPathProperty: (PathProperties) -> Unit,
    isSupportingPanel: Boolean = false
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = EditorDestination.Editor,
        enterTransition = { navigateInAnimation },
        exitTransition = { navigateUpAnimation },
        popEnterTransition = { navigateInAnimation },
        popExitTransition = { navigateUpAnimation }
    ) {
        composable<EditorDestination.Editor> {
            EditorSelector(
                isSupportingPanel = isSupportingPanel,
                onItemClick = { editorItem ->
                    val dest = when (editorItem) {
                        Adjust -> EditorDestination.Adjust
                        Crop -> EditorDestination.Crop
                        Filters -> EditorDestination.Filters
                        Markup -> EditorDestination.Markup
                    }
                    navController.navigate(dest)
                }
            )
        }

        composable<EditorDestination.Adjust> {
            AdjustSection(
                appliedAdjustments = appliedAdjustments,
                isSupportingPanel = isSupportingPanel,
                onItemClick = { adjustment ->
                    navController.navigate(EditorDestination.AdjustDetail(adjustment))
                },
                onLongItemClick = onAdjustItemLongClick
            )
        }

        composable<EditorDestination.AdjustDetail> {
            val params = it.toRoute<EditorDestination.AdjustDetail>()
            val isRotate = params.adjustment == VariableFilterTypes.Rotate

            AdjustScrubber(
                modifier = Modifier.padding(bottom = 16.dp),
                adjustment = params.adjustment,
                displayValue = { value ->
                    (value * if (isRotate) 1f else 100f).roundToInt()
                        .toString() + if (isRotate) "°" else ""
                },
                onAdjustmentChange = onAdjustmentChange,
                onAdjustmentPreview = onAdjustmentPreview,
                appliedAdjustments = appliedAdjustments,
                isSupportingPanel = isSupportingPanel
            )
        }

        composable<EditorDestination.Filters> {
            FiltersSelector(
                bitmap = targetImage!!,
                onClick = onToggleFilter,
                appliedAdjustments = appliedAdjustments,
                isSupportingPanel = isSupportingPanel
            )
        }

        composable<EditorDestination.Crop> {
            CropperSection(
                isSupportingPanel = isSupportingPanel,
                onActionClick = {
                    val adjustment = it.asAdjustment()
                    if (adjustment != null) {
                        onAdjustmentChange(adjustment)
                    } else {
                        startCropping()
                    }
                }
            )
        }

        composable<EditorDestination.Markup> {
            MarkupSelector(
                drawMode = drawMode,
                setDrawMode = setDrawMode,
                drawType = drawType,
                setDrawType = setDrawType,
                isSupportingPanel = isSupportingPanel,
                currentPathProperty = currentPathProperty,
                setCurrentPathProperty = setCurrentPathProperty
            )
        }

        composable<EditorDestination.ExternalEditor> {
            ExternalEditor(
                currentUri = targetUri,
                isSupportingPanel = isSupportingPanel
            )
        }
    }
}

