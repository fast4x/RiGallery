/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.presentation.trashed

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Constants.Target.TARGET_TRASH
import it.fast4x.rigallery.feature_node.domain.model.AlbumState
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import it.fast4x.rigallery.feature_node.presentation.common.MediaScreen
import it.fast4x.rigallery.feature_node.presentation.trashed.components.AutoDeleteFooter
import it.fast4x.rigallery.feature_node.presentation.trashed.components.EmptyTrash
import it.fast4x.rigallery.feature_node.presentation.trashed.components.TrashedNavActions

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
inline fun <reified T: Media> TrashedGridScreen(
    paddingValues: PaddingValues,
    albumName: String = stringResource(id = R.string.trash),
    handler: MediaHandleUseCase,
    mediaState: State<MediaState<T>>,
    albumsState: State<AlbumState>,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    noinline toggleSelection: (Int) -> Unit,
    noinline navigate: (route: String) -> Unit,
    noinline navigateUp: () -> Unit,
    noinline toggleNavbar: (Boolean) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) = MediaScreen(
    paddingValues = paddingValues,
    target = TARGET_TRASH,
    albumName = albumName,
    handler = handler,
    albumsState = albumsState,
    mediaState = mediaState,
    selectionState = selectionState,
    selectedMedia = selectedMedia,
    toggleSelection = toggleSelection,
    allowHeaders = false,
    enableStickyHeaders = false,
    navActionsContent = { _: MutableState<Boolean>,
                          _: ActivityResultLauncher<IntentSenderRequest> ->
        TrashedNavActions(handler, mediaState, selectedMedia, selectionState)
    },
    emptyContent = { EmptyTrash() },
    aboveGridContent = { AutoDeleteFooter() },
    navigate = navigate,
    navigateUp = navigateUp,
    toggleNavbar = toggleNavbar,
    sharedTransitionScope = sharedTransitionScope,
    animatedContentScope = animatedContentScope,
    isMusic = false
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        selectedMedia.clear()
        selectionState.value = false
    }
}