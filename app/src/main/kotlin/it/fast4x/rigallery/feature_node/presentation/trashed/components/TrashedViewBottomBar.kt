/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.presentation.trashed.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Constants
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.enums.TransitionEffect
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import it.fast4x.rigallery.feature_node.presentation.mediaview.components.BottomBarColumn
import it.fast4x.rigallery.feature_node.presentation.util.rememberActivityResult
import it.fast4x.rigallery.ui.theme.BlackScrim
import kotlinx.coroutines.launch

@Stable
@NonRestartableComposable
@Composable
fun TrashedViewBottomBar(
    modifier: Modifier = Modifier,
    handler: MediaHandleUseCase,
    showUI: Boolean,
    paddingValues: PaddingValues,
    currentMedia: Media.UriMedia?,
    currentIndex: Int,
    onDeleteMedia: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val result = rememberActivityResult()
    val transitionEffect by Settings.Misc.rememberTransitionEffect()
    AnimatedVisibility(
        visible = showUI,
        enter =  TransitionEffect.enter(
            TransitionEffect.entries[transitionEffect]
        ),
        exit =  TransitionEffect.exit(
            TransitionEffect.entries[transitionEffect]
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, BlackScrim)
                    )
                )
                .padding(
                    top = 24.dp,
                    bottom = paddingValues.calculateBottomPadding()
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Restore Component
            BottomBarColumn(
                currentMedia = currentMedia,
                imageVector = Icons.Outlined.RestoreFromTrash,
                title = stringResource(id = R.string.trash_restore)
            ) {
                scope.launch {
                    onDeleteMedia.invoke(currentIndex)
                    handler.trashMedia(result = result, arrayListOf(it), trash = false)
                }
            }
            // Delete Component
            BottomBarColumn(
                currentMedia = currentMedia,
                imageVector = Icons.Outlined.DeleteOutline,
                title = stringResource(id = R.string.trash_delete)
            ) {
                scope.launch {
                    onDeleteMedia.invoke(currentIndex)
                    handler.deleteMedia(result = result, arrayListOf(it))
                }
            }
        }
    }
}