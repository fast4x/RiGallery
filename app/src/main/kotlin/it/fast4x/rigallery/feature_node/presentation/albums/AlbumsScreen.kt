/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.presentation.albums

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Constants.Animation.enterAnimation
import it.fast4x.rigallery.core.Constants.Animation.exitAnimation
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.Settings.Album.LastSort
import it.fast4x.rigallery.core.Settings.Album.rememberLastSort
import it.fast4x.rigallery.core.enums.AlbumsSortOrder
import it.fast4x.rigallery.core.enums.MediaType
import it.fast4x.rigallery.core.enums.Option
import it.fast4x.rigallery.core.presentation.components.EmptyAlbum
import it.fast4x.rigallery.core.presentation.components.Error
import it.fast4x.rigallery.core.presentation.components.FilterButton
import it.fast4x.rigallery.core.presentation.components.FilterKind
import it.fast4x.rigallery.core.presentation.components.FilterOption
import it.fast4x.rigallery.core.presentation.components.LoadingAlbum
import it.fast4x.rigallery.core.presentation.components.OptionSheetMenu
import it.fast4x.rigallery.feature_node.domain.model.Album
import it.fast4x.rigallery.feature_node.domain.model.AlbumState
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.util.MediaOrder
import it.fast4x.rigallery.feature_node.domain.util.OrderType
import it.fast4x.rigallery.feature_node.presentation.albums.components.AlbumComponent
import it.fast4x.rigallery.feature_node.presentation.albums.components.CarouselPinnedAlbums
import it.fast4x.rigallery.feature_node.presentation.search.MainSearchBar
import it.fast4x.rigallery.feature_node.presentation.util.Screen
import it.fast4x.rigallery.feature_node.presentation.util.detectPinchGestures
import it.fast4x.rigallery.feature_node.presentation.util.mediaSharedElement
import it.fast4x.rigallery.feature_node.presentation.util.rememberActivityResult

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AlbumsScreen(
    navigate: (route: String) -> Unit,
    toggleNavbar: (Boolean) -> Unit,
    mediaState: State<MediaState<Media.UriMedia>>,
    albumsState: State<AlbumState>,
    paddingValues: PaddingValues,
    filterOptions: SnapshotStateList<FilterOption>,
    isScrolling: MutableState<Boolean>,
    searchBarActive: MutableState<Boolean>,
    onAlbumClick: (Album) -> Unit,
    onAlbumLongClick: (Album) -> Unit,
    onMoveAlbumToTrash: (ActivityResultLauncher<IntentSenderRequest>, Album) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val lastSort by rememberLastSort()
    LaunchedEffect(lastSort) {
        val selectedFilter = filterOptions.first { it.filterKind == lastSort.kind }
        selectedFilter.onClick(
            when (selectedFilter.filterKind) {
                FilterKind.DATE -> MediaOrder.Date(lastSort.orderType)
                FilterKind.NAME -> MediaOrder.Label(lastSort.orderType)
            }
        )
    }

    var finalPaddingValues by remember(paddingValues) { mutableStateOf(paddingValues) }

    Scaffold(
        modifier = Modifier.padding(
            start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
            end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
        ),
        topBar = {
            MainSearchBar(
                bottomPadding = paddingValues.calculateBottomPadding(),
                navigate = navigate,
                toggleNavbar = toggleNavbar,
                isScrolling = isScrolling,
                activeState = searchBarActive,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
            ) {
                IconButton(onClick = { navigate(Screen.SettingsScreen.route) }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.settings_title)
                    )
                }
            }
        }
    ) { innerPaddingValues ->
        LaunchedEffect(innerPaddingValues) {
            finalPaddingValues = PaddingValues(
                top = innerPaddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 16.dp + 64.dp
            )
        }

        val gridState = rememberLazyGridState()
        var level by remember { mutableIntStateOf(1) } //rememberAlbumGridSize()
        AnimatedVisibility(
            visible = level == 0,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            AlbumMediaGrid(
                gridState = gridState,
                mediaState = mediaState,
                albumsState = albumsState,
                finalPaddingValues = finalPaddingValues,
                filterOptions = filterOptions,
                onAlbumClick = onAlbumClick,
                onAlbumLongClick = onAlbumLongClick,
                onMoveAlbumToTrash = onMoveAlbumToTrash,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                columns = 1, nextLevel = 1, previousLevel = 0, onZoomLevelChange = { level = it }
            )
        }
        AnimatedVisibility(
            visible = level == 1,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            AlbumMediaGrid(
                gridState = gridState,
                mediaState = mediaState,
                albumsState = albumsState,
                finalPaddingValues = finalPaddingValues,
                filterOptions = filterOptions,
                onAlbumClick = onAlbumClick,
                onAlbumLongClick = onAlbumLongClick,
                onMoveAlbumToTrash = onMoveAlbumToTrash,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                columns = 2, nextLevel = 1, previousLevel = 0, onZoomLevelChange = { level = it }
            )
        }
//        AnimatedVisibility(
//            visible = level == 2,
//            enter = scaleIn() + fadeIn(),
//            exit = scaleOut() + fadeOut()
//        ) {
//            AlbumMediaGrid(
//                gridState = gridState,
//                mediaState = mediaState,
//                albumsState = albumsState,
//                finalPaddingValues = finalPaddingValues,
//                filterOptions = filterOptions,
//                onAlbumClick = onAlbumClick,
//                onAlbumLongClick = onAlbumLongClick,
//                onMoveAlbumToTrash = onMoveAlbumToTrash,
//                sharedTransitionScope = sharedTransitionScope,
//                animatedContentScope = animatedContentScope,
//                columns = 4, nextLevel = 3, previousLevel = 1, onZoomLevelChange = { level = it }
//            )
//        }
//        AnimatedVisibility(
//            visible = level == 2,
//            enter = scaleIn() + fadeIn(),
//            exit = scaleOut() + fadeOut()
//        ) {
//            AlbumMediaGrid(
//                gridState = gridState,
//                mediaState = mediaState,
//                albumsState = albumsState,
//                finalPaddingValues = finalPaddingValues,
//                filterOptions = filterOptions,
//                onAlbumClick = onAlbumClick,
//                onAlbumLongClick = onAlbumLongClick,
//                onMoveAlbumToTrash = onMoveAlbumToTrash,
//                sharedTransitionScope = sharedTransitionScope,
//                animatedContentScope = animatedContentScope,
//                columns = 6, nextLevel = 3, previousLevel = 2, onZoomLevelChange = { level = it }
//            )
//        }

    }
    /** Error State Handling Block **/
    AnimatedVisibility(
        visible = albumsState.value.error.isNotEmpty(),
        enter = enterAnimation,
        exit = exitAnimation
    ) {
        Error(errorMessage = albumsState.value.error)
    }
    /** ************ **/
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AlbumMediaGrid(
    gridState: LazyGridState,
    mediaState: State<MediaState<Media.UriMedia>>,
    albumsState: State<AlbumState>,
    finalPaddingValues: PaddingValues,
    filterOptions: SnapshotStateList<FilterOption>,
    onAlbumClick: (Album) -> Unit,
    onAlbumLongClick: (Album) -> Unit,
    onMoveAlbumToTrash: (ActivityResultLauncher<IntentSenderRequest>, Album) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    columns: Int, nextLevel: Int, previousLevel: Int, onZoomLevelChange: (Int) -> Unit
){
    val displayMode by remember { mutableStateOf(1) }
    var zoom by remember(displayMode) { mutableFloatStateOf(1f) }
    val zoomTransition: Float by animateFloatAsState(
        zoom,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    )

    var showAlbumsOrderMenu by remember { mutableStateOf(false) }
    var albumsLastSort by rememberLastSort()
    var albumsOrder = remember(albumsLastSort) { albumsLastSort.orderType }
    var albumsKind = remember(albumsLastSort) { albumsLastSort.kind }


    LazyVerticalGrid(
        state = gridState,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize().pointerInput(Unit) {
                detectPinchGestures(
                    pass = PointerEventPass.Initial,
                    onGesture = { centroid: Offset, newZoom: Float ->
                        val newScale = (zoom * newZoom)
                        if (newScale > 1.25f) {
                            onZoomLevelChange.invoke(previousLevel)
                        } else if (newScale < 0.75f) {
                            onZoomLevelChange.invoke(nextLevel)
                        } else {
                            zoom = newScale
                        }
                    },
                    onGestureEnd = { zoom = 1f }
                )
            }
            .graphicsLayer {
                scaleX = zoomTransition
                scaleY = zoomTransition
            },
        columns = GridCells.Fixed(columns),
        contentPadding = finalPaddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) },
            key = "pinnedAlbums"
        ) {
            AnimatedVisibility(
                visible = albumsState.value.albumsPinned.isNotEmpty(),
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(vertical = 24.dp),
                        text = stringResource(R.string.pinned_albums_title),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    CarouselPinnedAlbums(
                        albumList = albumsState.value.albumsPinned,
                        onAlbumClick = onAlbumClick,
                        onAlbumLongClick = onAlbumLongClick
                    )
                }
            }
        }
        item(
            span = { GridItemSpan(maxLineSpan) },
            key = "filterButton"
        ) {
            AnimatedVisibility(
                visible = albumsState.value.albumsUnpinned.isNotEmpty(),
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(vertical = 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = AlbumsSortOrder.entries[albumsKind.ordinal].title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                showAlbumsOrderMenu = true
                            }
                            .padding(5.dp)
                    )
                    IconButton(
                        onClick = {
                            albumsOrder =
                                if (albumsOrder == OrderType.Ascending) OrderType.Descending else OrderType.Ascending
                            albumsLastSort = albumsLastSort.copy(orderType = albumsOrder)
                        }
                    ) {
                        Icon(
                            imageVector = remember(albumsOrder) {
                                if (albumsOrder == OrderType.Descending)
                                    Icons.Outlined.KeyboardDoubleArrowDown
                                else Icons.Outlined.KeyboardDoubleArrowUp
                            },
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                    }
                }
                OptionSheetMenu(
                    title = "Sort order",
                    options = AlbumsSortOrder.entries.map{ option ->
                        Option(
                            ordinal = option.ordinal,
                            name = option.name,
                            title = option.title,
                            icon = option.icon
                        )
                    },
                    visible = showAlbumsOrderMenu,
                    onSelected = { albumsLastSort = LastSort(albumsOrder, FilterKind.entries[it]) },
                    onDismiss = { showAlbumsOrderMenu = false }
                )

//                FilterButton(
//                    filterOptions = filterOptions.toTypedArray()
//                )
            }
        }
        items(
            items = albumsState.value.albumsUnpinned,
            key = { item -> item.toString() }
        ) { item ->
            val trashResult = rememberActivityResult()
            with(sharedTransitionScope) {
                AlbumComponent(
                    modifier = Modifier
                        .animateItem(),
                    thumbnailModifier = Modifier
                        .mediaSharedElement(
                            album = item,
                            animatedVisibilityScope = animatedContentScope
                        ),
                    album = item,
                    onItemClick = onAlbumClick,
                    onTogglePinClick = onAlbumLongClick,
                    onMoveAlbumToTrash = {
                        onMoveAlbumToTrash(trashResult, it)
                    }
                )
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) },
            key = "albumDetails"
        ) {
            AnimatedVisibility(
                visible = mediaState.value.media.isNotEmpty() && albumsState.value.albums.isNotEmpty(),
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(vertical = 24.dp),
                    text = stringResource(
                        R.string.images_videos,
                        mediaState.value.media.size
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) },
            key = "emptyAlbums"
        ) {
            AnimatedVisibility(
                visible = albumsState.value.albums.isEmpty() && albumsState.value.error.isEmpty(),
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                EmptyAlbum()
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) },
            key = "loadingAlbums"
        ) {
            AnimatedVisibility(
                visible = albumsState.value.isLoading,
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                LoadingAlbum()
            }
        }
    }
}