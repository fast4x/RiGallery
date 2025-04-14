/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.presentation.common.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dokar.pinchzoomgrid.PinchZoomGridScope
import it.fast4x.rigallery.core.Constants.Animation.enterAnimation
import it.fast4x.rigallery.core.Constants.Animation.exitAnimation
import it.fast4x.rigallery.core.Settings.Misc.rememberAutoHideSearchBar
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.model.isHeaderKey
import it.fast4x.rigallery.feature_node.domain.model.isIgnoredKey
import it.fast4x.rigallery.feature_node.presentation.mediaview.rememberedDerivedState
import it.fast4x.rigallery.feature_node.presentation.util.roundDpToPx
import it.fast4x.rigallery.feature_node.presentation.util.roundSpToPx
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun <T : Media> PinchZoomGridScope.MediaGridView(
    mediaState: State<MediaState<T>>,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    searchBarPaddingTop: Dp = 0.dp,
    showSearchBar: Boolean = remember { false },
    allowSelection: Boolean = remember { false },
    selectionState: MutableState<Boolean> = remember { mutableStateOf(false) },
    selectedMedia: SnapshotStateList<T> = remember { mutableStateListOf() },
    toggleSelection: @DisallowComposableCalls (Int) -> Unit = {},
    canScroll: Boolean = true,
    allowHeaders: Boolean = true,
    enableStickyHeaders: Boolean = false,
    showMonthlyHeader: Boolean = true,
    aboveGridContent: @Composable (() -> Unit)? = null,
    isScrolling: MutableState<Boolean>,
    emptyContent: @Composable () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onMediaClick: @DisallowComposableCalls (media: T) -> Unit = {},
) {
    val mappedData by rememberedDerivedState(mediaState, showMonthlyHeader) {
        (if (showMonthlyHeader) mediaState.value.mappedMediaWithMonthly
        else mediaState.value.mappedMedia).toMutableStateList()
    }

    println("MediaGridView: showMonthlyHeader = $showMonthlyHeader, mappedData = $mappedData")

    BackHandler(
        enabled = selectionState.value && allowSelection,
        onBack = {
            selectionState.value = false
            selectedMedia.clear()
        }
    )

    /**
     * Workaround for a small bug
     * That shows the grid at the bottom after content is loaded
     */
    val lastLoadingState by remember { mutableStateOf(mediaState.value.isLoading) }
    LaunchedEffect(gridState, mediaState.value) {
        snapshotFlow { mediaState.value.isLoading }
            .distinctUntilChanged()
            .collectLatest { isLoading ->
                if (!isLoading && lastLoadingState) {
                    gridState.scrollToItem(0)
                }
            }
    }

    AnimatedVisibility(
        visible = enableStickyHeaders
    ) {
        val headers by rememberedDerivedState(mediaState.value) {
            mediaState.value.headers.toMutableStateList()
        }
        val stickyHeaderItem by rememberStickyHeaderItem(
            gridState = gridState,
            headers = headers,
            mappedData = mappedData
        )

        val hideSearchBarSetting by rememberAutoHideSearchBar()
        val searchBarPadding by animateDpAsState(
            targetValue = remember(
                isScrolling.value,
                showSearchBar,
                searchBarPaddingTop,
                hideSearchBarSetting
            ) {
                if (showSearchBar && (!isScrolling.value || !hideSearchBarSetting)) {
                    SearchBarDefaults.InputFieldHeight + searchBarPaddingTop + 8.dp
                } else if (showSearchBar && isScrolling.value) searchBarPaddingTop else 0.dp
            },
            label = "searchBarPadding"
        )

        val density = LocalDensity.current
        val searchBarHeightPx = WindowInsets.statusBars.getTop(density)
        val searchBarPaddingPx by remember(density, searchBarPadding) {
            derivedStateOf { with(density) { searchBarPadding.roundToPx() } }
        }

        StickyHeaderGrid(
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            headerMatcher = { item -> item.key.isHeaderKey || item.key.isIgnoredKey },
            searchBarOffset = { if (showSearchBar) 28.roundSpToPx(density) + searchBarPaddingPx else 0 },
            toolbarOffset = { if (showSearchBar) 0 else 64.roundDpToPx(density) + searchBarHeightPx },
            stickyHeader = {
                // TODO MEDIAGRID STICKY HEADER FOR GROUP ITEM
//                val show by remember(
//                    mediaState,
//                    stickyHeaderItem
//                ) {
//                    derivedStateOf {
//                        mediaState.value.media.isNotEmpty() && stickyHeaderItem != null
//                    }
//                }
//                AnimatedVisibility(
//                    visible = show,
//                    enter = enterAnimation,
//                    exit = exitAnimation
//                ) {
//                    val text by rememberedDerivedState(stickyHeaderItem) { stickyHeaderItem ?: "" }
//
//                    Row(
//                        modifier = Modifier.padding(top = 24.dp + searchBarPadding, bottom = 24.dp)
//                            .padding(horizontal = 5.dp)
//                            .background(MaterialTheme.colorScheme.inverseOnSurface)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
//                                    MaterialTheme.shapes.medium)
//                                .padding(start = 10.dp)
//                                .padding(vertical = 5.dp)
//                                //.background(MaterialTheme.colorScheme.background)
//                                .fillMaxWidth()
//                        ){
//                            Text(
//                                text = text,
//                                style = MaterialTheme.typography.titleLarge,
//                                color = MaterialTheme.colorScheme.onSurface,
//                                modifier = Modifier
////                                .background(
////                                    Brush.verticalGradient(
////                                        listOf(
////                                            // 3.dp is the elevation the LargeTopAppBar use
////                                            MaterialTheme.colorScheme.surfaceColorAtElevation(
////                                                3.dp
////                                            ),
////                                            Color.Transparent
////                                        )
////                                    )
////                                )
//                                    //.padding(horizontal = 16.dp)
//                                    //.padding(top = 24.dp + searchBarPadding, bottom = 24.dp)
////                                    .fillMaxWidth()
//                            )
//                        }
//
//                    }
//
//
//                }
            }
        ) {
            MediaGrid(
                gridState = gridState,
                mediaState = mediaState,
                mappedData = mappedData,
                paddingValues = paddingValues,
                allowSelection = allowSelection,
                selectionState = selectionState,
                selectedMedia = selectedMedia,
                toggleSelection = toggleSelection,
                canScroll = canScroll,
                allowHeaders = allowHeaders,
                aboveGridContent = aboveGridContent,
                isScrolling = isScrolling,
                emptyContent = emptyContent,
                onMediaClick = onMediaClick,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        }
    }
    AnimatedVisibility(
        visible = !enableStickyHeaders
    ) {
        MediaGrid(
            gridState = gridState,
            mediaState = mediaState,
            mappedData = mappedData,
            paddingValues = paddingValues,
            allowSelection = allowSelection,
            selectionState = selectionState,
            selectedMedia = selectedMedia,
            toggleSelection = toggleSelection,
            canScroll = canScroll,
            allowHeaders = allowHeaders,
            aboveGridContent = aboveGridContent,
            isScrolling = isScrolling,
            emptyContent = emptyContent,
            onMediaClick = onMediaClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )
    }

}