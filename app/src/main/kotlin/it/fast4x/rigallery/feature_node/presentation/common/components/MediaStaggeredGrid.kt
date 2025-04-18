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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.Companion.FullLine
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
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
import com.google.common.collect.Iterables.removeIf
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Constants.Animation.enterAnimation
import it.fast4x.rigallery.core.Constants.Animation.exitAnimation
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
fun <T: Media> MediaStaggeredGrid(
    gridState: LazyStaggeredGridState,
    mediaState: State<MediaState<T>>,
    mappedData: SnapshotStateList<MediaItem<T>>,
    paddingValues: PaddingValues = PaddingValues(5.dp),
    allowSelection: Boolean,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    toggleSelection: @DisallowComposableCalls (Int) -> Unit,
    canScroll: Boolean,
    allowHeaders: Boolean,
    aboveGridContent: @Composable() (() -> Unit)?,
    isScrolling: MutableState<Boolean>,
    emptyContent: @Composable () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onMediaClick: @DisallowComposableCalls (media: T) -> Unit
) {
    LaunchedEffect(gridState.isScrollInProgress) {
        snapshotFlow {
            gridState.isScrollInProgress
        }.collectLatest {
            isScrolling.value = it
        }
    }

    val topContent: LazyStaggeredGridScope.() -> Unit = remember(aboveGridContent) {
        {
            if (aboveGridContent != null) {
                item (
                    span = FullLine,
                    key = "aboveGrid"
                ) {
                    aboveGridContent.invoke()
                }
            }
        }
    }
    val bottomContent: LazyStaggeredGridScope.() -> Unit = remember {
        {
            item(
                span = FullLine,
                key = "loading"
            ) {
                AnimatedVisibility(
                    visible = mediaState.value.isLoading,
                    enter = enterAnimation,
                    exit = exitAnimation
                ) {
                    LoadingMedia()
                }
            }

            item(
                span = FullLine,
                key = "empty"
            ) {
                AnimatedVisibility(
                    visible = mediaState.value.media.isEmpty() && !mediaState.value.isLoading,
                    enter = enterAnimation,
                    exit = exitAnimation
                ) {
                    emptyContent()
                }
            }
            item(
                span = FullLine,
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
            MediaStaggeredGridContentWithHeaders(
                mediaState = mediaState,
                gridState = gridState,
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
            MediaStaggeredGridContentWithHeaders(
                mediaState = mediaState,
                gridState = gridState,
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
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                columns = 2, nextLevel = 2, previousLevel = 0, onZoomLevelChange = { level = it }
            )
        }
        AnimatedVisibility(
            visible = level == 2,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            MediaStaggeredGridContentWithHeaders(
                mediaState = mediaState,
                gridState = gridState,
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
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                columns = 4, nextLevel = 3, previousLevel = 1, onZoomLevelChange = { level = it }
            )
        }
        AnimatedVisibility(
            visible = level == 3,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            MediaStaggeredGridContentWithHeaders(
                mediaState = mediaState,
                gridState = gridState,
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
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                columns = 6, nextLevel = 3, previousLevel = 2, onZoomLevelChange = { level = it }
            )
        }
    }

    AnimatedVisibility(
        visible = !allowHeaders
    ) {
        MediaStaggeredGridContent(
            mediaState = mediaState,
            gridState = gridState,
            gridCells = StaggeredGridCells.Fixed(3),
            paddingValues = paddingValues,
            allowSelection = allowSelection,
            selectionState = selectionState,
            selectedMedia = selectedMedia,
            toggleSelection = toggleSelection,
            canScroll = canScroll,
            onMediaClick = onMediaClick,
            topContent = topContent,
            bottomContent = bottomContent,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun <T: Media> MediaStaggeredGridContentWithHeaders(
    mediaState: State<MediaState<T>>,
    gridState: LazyStaggeredGridState,
    mappedData: SnapshotStateList<MediaItem<T>>,
    paddingValues: PaddingValues,
    allowSelection: Boolean,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    toggleSelection: @DisallowComposableCalls (Int) -> Unit,
    canScroll: Boolean,
    onMediaClick: @DisallowComposableCalls (media: T) -> Unit,
    topContent: LazyStaggeredGridScope.() -> Unit,
    bottomContent: LazyStaggeredGridScope.() -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    columns: Int, nextLevel: Int, previousLevel: Int, onZoomLevelChange: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val stringToday = stringResource(id = R.string.header_today)
    val stringYesterday = stringResource(id = R.string.header_yesterday)
    val feedbackManager = rememberFeedbackManager()
    val headers by rememberedDerivedState(mediaState.value) {
        mediaState.value.headers.toMutableStateList()
    }

    val displayMode by remember { mutableStateOf(1) }
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
        state = rememberLazyGridState(),
    ) {
        LazyVerticalStaggeredGrid(
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
            columns = StaggeredGridCells.Fixed(columns),
            contentPadding = paddingValues,
            userScrollEnabled = canScroll,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalItemSpacing = 5.dp
        ) {
            topContent()

            items(
                items = mappedData,
                key = { item -> item.key },
                contentType = { item -> item.key.startsWith("media_") },
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
                                fadeInSpec = null
                            ),
                        date = remember {
                            it.text
                                .replace("Today", stringToday)
                                .replace("Yesterday", stringYesterday)
                        },
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
                    with(sharedTransitionScope) {
                        MediaImage(
                            modifier = Modifier
                                .mediaSharedElement(
                                    media = it.media,
                                    animatedVisibilityScope = animatedContentScope
                                )
                                .animateItem(
                                    fadeInSpec = null
                                ),
                            media = it.media,
                            staggered = true,
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
            }

            bottomContent()
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun <T: Media> MediaStaggeredGridContent(
    mediaState: State<MediaState<T>>,
    gridState: LazyStaggeredGridState,
    gridCells: StaggeredGridCells,
    paddingValues: PaddingValues,
    allowSelection: Boolean,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    toggleSelection: @DisallowComposableCalls (Int) -> Unit,
    canScroll: Boolean,
    onMediaClick: @DisallowComposableCalls (media: T) -> Unit,
    topContent: LazyStaggeredGridScope.() -> Unit,
    bottomContent: LazyStaggeredGridScope.() -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {


    val feedbackManager = rememberFeedbackManager()
    LazyVerticalStaggeredGrid(
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        columns = gridCells,
        contentPadding = paddingValues,
        userScrollEnabled = canScroll,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        topContent()

        itemsIndexed(
            items = mediaState.value.media,
            key = { _, item -> item.toString() },
            contentType = { _, item -> item.isImage }
        ) { index, media ->
            with(sharedTransitionScope) {
                MediaImage(
                    modifier = Modifier
                        .mediaSharedElement(
                            media = media,
                            animatedVisibilityScope = animatedContentScope
                        )
                        .animateItem(
                            fadeInSpec = null
                        ),
                    media = media,
                    staggered = true,
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
        }

        bottomContent()
    }
}

