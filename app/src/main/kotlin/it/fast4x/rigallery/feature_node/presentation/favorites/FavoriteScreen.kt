/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.presentation.favorites

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
import it.fast4x.rigallery.core.Constants.Target.TARGET_FAVORITES
import it.fast4x.rigallery.feature_node.domain.model.AlbumState
import it.fast4x.rigallery.feature_node.domain.model.Media.UriMedia
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import it.fast4x.rigallery.feature_node.presentation.common.MediaScreen
import it.fast4x.rigallery.feature_node.presentation.favorites.components.EmptyFavorites
import it.fast4x.rigallery.feature_node.presentation.favorites.components.FavoriteNavActions

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FavoriteScreen(
    paddingValues: PaddingValues,
    albumName: String = stringResource(id = R.string.favorites),
    handler: MediaHandleUseCase,
    mediaState: State<MediaState<UriMedia>>,
    albumsState: State<AlbumState>,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<UriMedia>,
    toggleFavorite: (ActivityResultLauncher<IntentSenderRequest>, List<UriMedia>, Boolean) -> Unit,
    toggleSelection: (Int) -> Unit,
    navigate: (route: String) -> Unit,
    navigateUp: () -> Unit,
    toggleNavbar: (Boolean) -> Unit,
) = MediaScreen(
    paddingValues = paddingValues,
    target = TARGET_FAVORITES,
    albumName = albumName,
    handler = handler,
    albumsState = albumsState,
    mediaState = mediaState,
    selectionState = selectionState,
    selectedMedia = selectedMedia,
    toggleSelection = toggleSelection,
    navActionsContent = { _: MutableState<Boolean>,
                          result: ActivityResultLauncher<IntentSenderRequest> ->
        FavoriteNavActions(toggleFavorite, mediaState, selectedMedia, selectionState, result)
    },
    emptyContent = { EmptyFavorites() },
    navigate = navigate,
    navigateUp = navigateUp,
    toggleNavbar = toggleNavbar,
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        selectedMedia.clear()
        selectionState.value = false
    }
}