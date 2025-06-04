package it.fast4x.rigallery.feature_node.presentation.common.components

import android.R.attr.scaleX
import android.R.attr.scaleY
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.enums.TransitionEffect
import it.fast4x.rigallery.core.presentation.components.Error
import it.fast4x.rigallery.core.presentation.components.LoadingMedia
import it.fast4x.rigallery.core.presentation.components.MediaItemHeader
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaItem
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.model.isBigHeaderKey
import it.fast4x.rigallery.feature_node.domain.model.isHeaderKey
import it.fast4x.rigallery.feature_node.domain.util.isImage
import it.fast4x.rigallery.feature_node.presentation.mediaview.rememberedDerivedState
import it.fast4x.rigallery.feature_node.presentation.util.detectPinchGestures
import it.fast4x.rigallery.feature_node.presentation.util.mediaSharedElement
import it.fast4x.rigallery.feature_node.presentation.util.rememberFeedbackManager
import it.fast4x.rigallery.feature_node.presentation.util.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun <T: Media> MediaGrid(
    gridState: LazyGridState,
    mediaState: State<MediaState<T>>,
    mappedData: SnapshotStateList<MediaItem<T>>,
    paddingValues: PaddingValues,
    allowSelection: Boolean,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    toggleSelection: @DisallowComposableCalls (Int) -> Unit,
    canScroll: Boolean,
    allowHeaders: Boolean,
    aboveGridContent: @Composable() (() -> Unit)?,
    isScrolling: MutableState<Boolean>,
    emptyContent: @Composable () -> Unit,
    onMediaClick: @DisallowComposableCalls (media: T) -> Unit
) {
    LaunchedEffect(gridState.isScrollInProgress) {
        snapshotFlow {
            gridState.isScrollInProgress
        }.collectLatest {
            isScrolling.value = it
        }
    }

    val transitionEffect by Settings.Misc.rememberTransitionEffect()

    val topContent: LazyGridScope.() -> Unit = remember(aboveGridContent) {
        {
            if (aboveGridContent != null) {
                item(
                    span = { GridItemSpan(maxLineSpan) },
                    key = "aboveGrid"
                ) {
                    aboveGridContent.invoke()
                }
            }
        }
    }
    val bottomContent: LazyGridScope.() -> Unit = remember {
        {
            item(
                span = { GridItemSpan(maxLineSpan) },
                key = "loading"
            ) {
                AnimatedVisibility(
                    visible = mediaState.value.isLoading,
                    enter =  TransitionEffect.enter(
                        TransitionEffect.entries[transitionEffect]
                    ),
                    exit =  TransitionEffect.exit(
                        TransitionEffect.entries[transitionEffect]
                    )
                ) {
                    LoadingMedia()
                }
            }

            item(
                span = { GridItemSpan(maxLineSpan) },
                key = "empty"
            ) {
                AnimatedVisibility(
                    visible = mediaState.value.media.isEmpty() && !mediaState.value.isLoading,
                    enter =  TransitionEffect.enter(
                        TransitionEffect.entries[transitionEffect]
                    ),
                    exit =  TransitionEffect.exit(
                        TransitionEffect.entries[transitionEffect]
                    )
                ) {
                    emptyContent()
                }
            }
            item(
                span = { GridItemSpan(maxLineSpan) },
                key = "error"
            ) {
                AnimatedVisibility(visible = mediaState.value.error.isNotEmpty()) {
                    Error(errorMessage = mediaState.value.error)
                }
            }
        }
    }

    AnimatedVisibility(
        visible = allowHeaders
    ) {
        var level by remember { mutableIntStateOf(2) }
        AnimatedVisibility(
            visible = level == 0,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            MediaGridContentWithHeaders(
                mediaState = mediaState,
                gridState = gridState,
                gridCells = GridCells.Fixed(3),
                mappedData = mappedData,
                paddingValues = paddingValues,
                allowSelection = allowSelection,
                selectionState = selectionState,
                selectedMedia = selectedMedia,
                toggleSelection = toggleSelection,
                canScroll = canScroll,
                onMediaClick = onMediaClick,
                topContent = topContent,
                bottomContent = bottomContent,
                columns = 1, nextLevel = 1, previousLevel = 0, onZoomLevelChange = { level = it }
            )
        }
        AnimatedVisibility(
            visible = level == 1,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            MediaGridContentWithHeaders(
                mediaState = mediaState,
                gridState = gridState,
                gridCells = GridCells.Fixed(3),
                mappedData = mappedData,
                paddingValues = paddingValues,
                allowSelection = allowSelection,
                selectionState = selectionState,
                selectedMedia = selectedMedia,
                toggleSelection = toggleSelection,
                canScroll = canScroll,
                onMediaClick = onMediaClick,
                topContent = topContent,
                bottomContent = bottomContent,
                columns = 2, nextLevel = 2, previousLevel = 0, onZoomLevelChange = { level = it }
            )
        }
        AnimatedVisibility(
            visible = level == 2,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            MediaGridContentWithHeaders(
                mediaState = mediaState,
                gridState = gridState,
                gridCells = GridCells.Fixed(3),
                mappedData = mappedData,
                paddingValues = paddingValues,
                allowSelection = allowSelection,
                selectionState = selectionState,
                selectedMedia = selectedMedia,
                toggleSelection = toggleSelection,
                canScroll = canScroll,
                onMediaClick = onMediaClick,
                topContent = topContent,
                bottomContent = bottomContent,
                columns = 4, nextLevel = 3, previousLevel = 1, onZoomLevelChange = { level = it }
            )
        }
        AnimatedVisibility(
            visible = level == 3,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            MediaGridContentWithHeaders(
                mediaState = mediaState,
                gridState = gridState,
                gridCells = GridCells.Fixed(3),
                mappedData = mappedData,
                paddingValues = paddingValues,
                allowSelection = allowSelection,
                selectionState = selectionState,
                selectedMedia = selectedMedia,
                toggleSelection = toggleSelection,
                canScroll = canScroll,
                onMediaClick = onMediaClick,
                topContent = topContent,
                bottomContent = bottomContent,
                columns = 6, nextLevel = 3, previousLevel = 2, onZoomLevelChange = { level = it }
            )
        }
    }

    AnimatedVisibility(
        visible = !allowHeaders
    ) {
        MediaGridContent(
            mediaState = mediaState,
            gridState = gridState,
            gridCells = GridCells.Fixed(3),
            paddingValues = paddingValues,
            allowSelection = allowSelection,
            selectionState = selectionState,
            selectedMedia = selectedMedia,
            toggleSelection = toggleSelection,
            canScroll = canScroll,
            onMediaClick = onMediaClick,
            topContent = topContent,
            bottomContent = bottomContent,
            //sharedTransitionScope = sharedTransitionScope,
            //animatedContentScope = animatedContentScope
        )
    }

}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun <T: Media> MediaGridContentWithHeaders(
    mediaState: State<MediaState<T>>,
    gridState: LazyGridState,
    mappedData: SnapshotStateList<MediaItem<T>>,
    gridCells: GridCells,
    paddingValues: PaddingValues,
    allowSelection: Boolean,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    toggleSelection: @DisallowComposableCalls (Int) -> Unit,
    canScroll: Boolean,
    onMediaClick: @DisallowComposableCalls (media: T) -> Unit,
    topContent: LazyGridScope.() -> Unit,
    bottomContent: LazyGridScope.() -> Unit,
    columns: Int, nextLevel: Int, previousLevel: Int, onZoomLevelChange: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()


    val feedbackManager = rememberFeedbackManager()
    val headers by rememberedDerivedState(mediaState.value) {
        mediaState.value.headers.toMutableStateList()
    }

    val displayMode by remember { mutableIntStateOf(1) }
    var zoom by remember(displayMode) { mutableFloatStateOf(1f) }
    val zoomTransition: Float by animateFloatAsState(
        zoom,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    )

    TimelineScroller(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 32.dp)
            .padding(vertical = 32.dp),
        mappedData = mappedData,
        headers = headers,
        state = gridState,
    ) {
        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("media_grid")
                .pointerInput(Unit) {
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
            contentPadding = paddingValues,
            userScrollEnabled = canScroll,
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            topContent()

            items(
                items = mappedData,
                key = { item -> item.key },
                contentType = { item -> item.key.startsWith("media_") },
                span = { item ->
                    GridItemSpan(if (item.key.isHeaderKey) maxLineSpan else 1)
                }
            ) { it ->
                if (it is MediaItem.Header) {
                    val isChecked = rememberSaveable { mutableStateOf(false) }
                    if (allowSelection) {
                        LaunchedEffect(selectionState.value) {
                            // Uncheck if selectionState is set to false
                            isChecked.value = isChecked.value && selectionState.value
                        }
                        LaunchedEffect(selectedMedia.size) {
                            withContext(Dispatchers.IO) {
                                // Partial check of media items should not check the header
                                isChecked.value = selectedMedia.map { it.id }.containsAll(it.data)
                            }
                        }
                    }
                    MediaItemHeader(
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = tween(durationMillis = 250),
                                fadeOutSpec = tween(durationMillis = 100),
                                placementSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy)
                            ),
                        date = remember { it.text },
                        showAsBig = remember { it.key.isBigHeaderKey },
                        isCheckVisible = selectionState,
                        isChecked = isChecked
                    ) {
                        if (allowSelection) {
                            feedbackManager.vibrate()
                            scope.launch {
                                isChecked.value = !isChecked.value
                                if (isChecked.value) {
                                    val toAdd = it.data.toMutableList().apply {
                                        // Avoid media from being added twice to selection
                                        removeIf {
                                            selectedMedia.map { media -> media.id }.contains(it)
                                        }
                                    }
                                    selectedMedia.addAll(mediaState.value.media.filter {
                                        toAdd.contains(
                                            it.id
                                        )
                                    })
                                } else selectedMedia.removeAll { media -> it.data.contains(media.id) }
                                selectionState.update(selectedMedia.isNotEmpty())
                            }
                        }
                    }
                } else if (it is MediaItem.MediaViewItem) {

                        MediaImage(

                            media = it.media,
                            selectionState = selectionState,
                            selectedMedia = selectedMedia,
                            canClick = canScroll,
                            onItemClick = {
                                if (selectionState.value && allowSelection) {
                                    feedbackManager.vibrate()
                                    toggleSelection(mediaState.value.media.indexOf(it))
                                } else onMediaClick(it)
                            }
                        ) {
                            if (allowSelection) {
                                feedbackManager.vibrate()
                                toggleSelection(mediaState.value.media.indexOf(it))
                            }
                        }

                }
            }


            bottomContent()
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun <T: Media> MediaGridContent(
    mediaState: State<MediaState<T>>,
    gridState: LazyGridState,
    gridCells: GridCells,
    paddingValues: PaddingValues,
    allowSelection: Boolean,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    toggleSelection: @DisallowComposableCalls (Int) -> Unit,
    canScroll: Boolean,
    onMediaClick: @DisallowComposableCalls (media: T) -> Unit,
    topContent: LazyGridScope.() -> Unit,
    bottomContent: LazyGridScope.() -> Unit,
    //sharedTransitionScope: SharedTransitionScope,
    //animatedContentScope: AnimatedContentScope,
) {


    val feedbackManager = rememberFeedbackManager()
    LazyVerticalGrid(
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        columns = gridCells,
        contentPadding = paddingValues,
        userScrollEnabled = canScroll,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        topContent()

        itemsIndexed(
            items = mediaState.value.media,
            key = { _, item -> item.toString() },
            contentType = { _, item -> item.isImage }
        ) { index, media ->

                MediaImage(

                    media = media,
                    selectionState = selectionState,
                    selectedMedia = selectedMedia,
                    canClick = canScroll,
                    onItemClick = {
                        if (selectionState.value && allowSelection) {
                            feedbackManager.vibrate()
                            toggleSelection(index)
                        } else onMediaClick(it)
                    },
                    onItemLongClick = {
                        if (allowSelection) {
                            feedbackManager.vibrate()
                            toggleSelection(index)
                        }
                    }
                )

        }

        bottomContent()
    }
}

