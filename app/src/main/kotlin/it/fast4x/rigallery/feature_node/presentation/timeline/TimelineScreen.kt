/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.feature_node.presentation.timeline

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import it.fast4x.rigallery.BuildConfig
import it.fast4x.rigallery.feature_node.domain.model.AlbumState
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import it.fast4x.rigallery.feature_node.presentation.common.MediaScreen
import it.fast4x.rigallery.feature_node.presentation.timeline.components.TimelineNavActions

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
inline fun <reified T: Media> TimelineScreen(
    paddingValues: PaddingValues,
    albumId: Long = -1L,
    albumName: String = BuildConfig.APPLICATION_NAME,
    handler: MediaHandleUseCase,
    mediaState: State<MediaState<T>>,
    albumsState: State<AlbumState>,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    allowNavBar: Boolean = true,
    allowHeaders: Boolean = true,
    enableStickyHeaders: Boolean = true,
    noinline toggleSelection: (Int) -> Unit,
    noinline navigate: @DisallowComposableCalls (route: String) -> Unit,
    noinline navigateUp: @DisallowComposableCalls () -> Unit,
    noinline toggleNavbar: (Boolean) -> Unit,
    isScrolling: MutableState<Boolean>,
    searchBarActive: MutableState<Boolean> = mutableStateOf(false),
    showSearchBar: MutableState<Boolean>,
) {

    MediaScreen(
        paddingValues = paddingValues,
        albumId = albumId,
        target = null,
        albumName = albumName,
        handler = handler,
        albumsState = albumsState,
        mediaState = mediaState,
        selectionState = selectionState,
        selectedMedia = selectedMedia,
        toggleSelection = toggleSelection,
        allowHeaders = allowHeaders,
        showMonthlyHeader = true,
        enableStickyHeaders = enableStickyHeaders,
        allowNavBar = allowNavBar,
        navActionsContent = { expandedDropDown: MutableState<Boolean>, _ ->
            TimelineNavActions(
                albumId = albumId,
                handler = handler,
                expandedDropDown = expandedDropDown,
                mediaState = mediaState,
                selectedMedia = selectedMedia,
                selectionState = selectionState,
                navigate = navigate,
                navigateUp = navigateUp
            )
        },
        navigate = navigate,
        navigateUp = navigateUp,
        toggleNavbar = toggleNavbar,
        isScrolling = isScrolling,
        showSearch = showSearchBar,
        searchBarActive = searchBarActive,

    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedMedia.clear()
            selectionState.value = false
        }
    }
}