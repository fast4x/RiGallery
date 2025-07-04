/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.feature_node.presentation.mediaview

import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.core.BottomSheet
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.rememberBottomSheetState
import it.fast4x.rigallery.core.Constants.DEFAULT_LOW_VELOCITY_SWIPE_DURATION
import it.fast4x.rigallery.core.Constants.DEFAULT_TOP_BAR_ANIMATION_DURATION
import it.fast4x.rigallery.core.Constants.Target.TARGET_TRASH
import it.fast4x.rigallery.core.Settings.Misc.rememberAutoHideOnVideoPlay
import it.fast4x.rigallery.core.Settings.Misc.rememberDateHeaderFormat
import it.fast4x.rigallery.core.Settings.Misc.rememberVideoAutoplay
import it.fast4x.rigallery.core.presentation.components.util.swipe
import it.fast4x.rigallery.feature_node.domain.model.AlbumState
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.model.Vault
import it.fast4x.rigallery.feature_node.domain.model.VaultState
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import it.fast4x.rigallery.feature_node.domain.util.getUri
import it.fast4x.rigallery.feature_node.domain.util.isImage
import it.fast4x.rigallery.feature_node.domain.util.isVideo
import it.fast4x.rigallery.feature_node.domain.util.readUriOnly
import it.fast4x.rigallery.feature_node.presentation.mediaview.components.MediaViewActions2
import it.fast4x.rigallery.feature_node.presentation.mediaview.components.MediaViewAppBar
import it.fast4x.rigallery.feature_node.presentation.mediaview.components.MediaViewDetails
import it.fast4x.rigallery.feature_node.presentation.mediaview.components.media.MediaPreviewComponent
import it.fast4x.rigallery.feature_node.presentation.mediaview.components.video.VideoPlayerController
import it.fast4x.rigallery.feature_node.presentation.util.FullBrightnessWindow
import it.fast4x.rigallery.feature_node.presentation.util.ProvideInsets
import it.fast4x.rigallery.feature_node.presentation.util.ViewScreenConstants.BOTTOM_BAR_HEIGHT
import it.fast4x.rigallery.feature_node.presentation.util.ViewScreenConstants.ImageOnly
import it.fast4x.rigallery.feature_node.presentation.util.getDate
import it.fast4x.rigallery.feature_node.presentation.util.normalize
import it.fast4x.rigallery.feature_node.presentation.util.printWarning
import it.fast4x.rigallery.feature_node.presentation.util.rememberGestureNavigationEnabled
import it.fast4x.rigallery.feature_node.presentation.util.rememberWindowInsetsController
import it.fast4x.rigallery.feature_node.presentation.util.setHdrMode
import it.fast4x.rigallery.feature_node.presentation.util.toggleSystemBars
import it.fast4x.rigallery.ui.theme.BlackScrim
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sketch
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.enums.TransitionEffect
import it.fast4x.rigallery.core.extensions.cast.Cast
import it.fast4x.rigallery.core.extensions.cast.CastMediaType
import it.fast4x.rigallery.core.presentation.components.DragHandle
import it.fast4x.rigallery.core.presentation.components.ModalSheet
import it.fast4x.rigallery.feature_node.domain.model.Event
import it.fast4x.rigallery.feature_node.domain.model.rememberLocationData
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionLayout
import it.fast4x.rigallery.feature_node.presentation.statistics.StatisticsViewModel
import it.fast4x.rigallery.feature_node.presentation.util.ViewScreenConstants.FullyExpanded
import it.fast4x.rigallery.feature_node.presentation.util.rememberAppBottomSheetState
import it.fast4x.rigallery.feature_node.presentation.util.rememberExifInterface
import it.fast4x.rigallery.feature_node.presentation.util.rememberExifMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@Composable
fun <T> rememberedDerivedState(
    key: Any? = Unit,
    block: @DisallowComposableCalls () -> T
): State<T> {
    return remember(key) {
        derivedStateOf(block)
    }
}

@Composable
fun <T> rememberedDerivedState(
    vararg keys: Any? = arrayOf(Unit),
    block: @DisallowComposableCalls () -> T
): State<T> {
    return remember(keys) {
        derivedStateOf(block)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun <T : Media> MediaViewScreen(
    navigateUp: () -> Unit,
    toggleRotate: () -> Unit,
    paddingValues: PaddingValues,
    isStandalone: Boolean = false,
    mediaId: Long,
    target: String? = null,
    mediaState: State<MediaState<out T>>,
    albumsState: State<AlbumState> = remember { mutableStateOf(AlbumState()) },
    handler: MediaHandleUseCase?,
    vaultState: State<VaultState>,
    addMedia: (Vault, T) -> Unit,
    restoreMedia: ((Vault, T, () -> Unit) -> Unit)? = null,
    deleteMedia: ((Vault, T, () -> Unit) -> Unit)? = null,
    currentVault: Vault? = null,
    navigate: (String) -> Unit,
) = ProvideInsets {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val windowInsetsController = rememberWindowInsetsController()

    val initialPage by rememberedDerivedState(mediaId, mediaState.value) {
        mediaState.value.media.indexOfFirst { it.id == mediaId }.coerceAtLeast(0)
    }
    var currentPage by rememberSaveable(initialPage) { mutableIntStateOf(initialPage) }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f,
        pageCount = mediaState.value.media::size
    )

    val currentMedia by rememberedDerivedState(mediaState.value) {
        mediaState.value.media.getOrNull(currentPage)
    }

    var shouldForcePage by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialPage, currentPage, mediaState.value) {
        if (currentPage == 0) {
            if (mediaState.value.isLoading) {
                shouldForcePage = true
            } else {
                if (initialPage != 0 && currentPage != initialPage && shouldForcePage) {
                    pagerState.scrollToPage(initialPage)
                }
                shouldForcePage = false
            }
        }
    }



    val currentDateFormat by rememberDateHeaderFormat()

    val currentDate by rememberedDerivedState {
        currentMedia?.definedTimestamp?.getDate(currentDateFormat) ?: ""
    }
    val canAutoPlay by rememberVideoAutoplay()
    val playWhenReady by rememberedDerivedState(
        currentMedia,
        canAutoPlay
    ) { currentMedia?.isVideo == true && canAutoPlay }
    val isReadOnly by rememberedDerivedState { currentMedia?.readUriOnly == true }
    val showInfo by rememberedDerivedState { currentMedia?.trashed == 0 && !isReadOnly }

    var showUI by rememberSaveable { mutableStateOf(true) }

    BackHandler(!showUI) {
        windowInsetsController.toggleSystemBars(show = true)
        navigateUp()
    }
    var sheetHeightDp by remember { mutableStateOf(1.dp) }
    var lastSheetHeightDp by rememberSaveable { mutableFloatStateOf(0f) }

    val configuration = LocalConfiguration.current
    var lastOrientation by rememberSaveable(configuration.orientation) {
        mutableIntStateOf(
            configuration.orientation
        )
    }
    val isLandscape = remember(configuration) {
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    val isGestureEnabled = rememberGestureNavigationEnabled()
    // Extra padding for navigation bar with 3/2-buttons
    val extraPaddingWithNavButtons by remember(isLandscape, isGestureEnabled) {
        mutableStateOf(
            if (!isGestureEnabled && !isLandscape) {
                32.dp
            } else 0.dp
        )
    }
    //val navigationBarHeight = rememberNavigationBarHeight()
    val bottomBarHeightDefault by remember(isGestureEnabled, isLandscape) {
        mutableStateOf(
            if (!isGestureEnabled && isLandscape) 84.dp
            else BOTTOM_BAR_HEIGHT
        )
    }

    val imageOnlyDetent = remember(bottomBarHeightDefault, extraPaddingWithNavButtons) {
        ImageOnly { bottomBarHeightDefault + extraPaddingWithNavButtons }
    }

    val sheetState = rememberBottomSheetState(
        initialDetent = imageOnlyDetent,
        detents = listOf(imageOnlyDetent, FullyExpanded { sheetHeightDp = it })
    )

    //val sheetStateCast = rememberAppBottomSheetState()
    val sheetStateCast = rememberModalBottomSheetState()

    val userScrollEnabled by rememberedDerivedState { sheetState.currentDetent != FullyExpanded }

    var storedNormalizationTarget by rememberSaveable(configuration.orientation) {
        mutableFloatStateOf(0f)
    }

    val isNormalizationTargetSet by rememberedDerivedState(
        bottomBarHeightDefault,
        currentPage,
        pagerState.currentPage,
        configuration.orientation,
        mediaState.value,
        sheetHeightDp.value,
        lastSheetHeightDp,
        lastOrientation,
        shouldForcePage
    ) {
        lastOrientation == configuration.orientation
                && lastSheetHeightDp.dp == sheetHeightDp
                && currentPage == pagerState.currentPage
                && storedNormalizationTarget > 0f
                && storedNormalizationTarget < 0.7f
                && !mediaState.value.isLoading
                && !shouldForcePage
    }

    LaunchedEffect(sheetHeightDp.value, configuration.orientation) {
        if (!isNormalizationTargetSet) {
            lastSheetHeightDp = sheetHeightDp.value
            lastOrientation = configuration.orientation
        }
    }

    val normalizationTarget by rememberedDerivedState(
        isNormalizationTargetSet,
        mediaState.value,
        currentPage,
        sheetState.offset,
        storedNormalizationTarget
    ) {
        if (isNormalizationTargetSet) {
            storedNormalizationTarget
        } else {
            val newOffset = sheetState.offset
            if (newOffset > 0f && newOffset < 1f) {
                storedNormalizationTarget = newOffset
            }
            storedNormalizationTarget
        }
    }

    LaunchedEffect(shouldForcePage) {
        if (!shouldForcePage) {
            storedNormalizationTarget = sheetState.offset
        }
    }

    val normalizedOffset by rememberedDerivedState(
        normalizationTarget,
        isNormalizationTargetSet
    ) {
        if (isNormalizationTargetSet) {
            sheetState.offset.normalize(minValue = normalizationTarget)
        } else 0f
    }

    LaunchedEffect(mediaState.value) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            if (!mediaState.value.isLoading && mediaState.value.media.isEmpty() && !isStandalone) {
                windowInsetsController.toggleSystemBars(show = true)
                navigateUp()
            }
            if (!mediaState.value.isLoading) {
                currentPage = page
            }
        }
    }

    val pageInteractionSource = remember { MutableInteractionSource() }
    var isAutoAdvanceEnabled by remember { mutableStateOf(false) }

    if (isAutoAdvanceEnabled) {
        LaunchedEffect(pagerState, pageInteractionSource) {
            while (true) {
                delay(4000)
                val nextPage = (pagerState.currentPage + 1) % mediaState.value.media.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    // set HDR Gain map
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        LaunchedEffect(mediaState.value) {
            withContext(Dispatchers.IO) {
                snapshotFlow { pagerState.currentPage }.collectLatest {
                    printWarning("Trying to set HDR mode for page $it")
                    if (currentMedia?.isImage == true) {
                        val request = ImageRequest(context, currentMedia?.getUri().toString()) {
                            crossfade()
                            resizeOnDraw()
                            setExtra(
                                key = "mediaKey",
                                value = currentMedia.toString(),
                            )
                            setExtra(
                                key = "realMimeType",
                                value = currentMedia?.mimeType,
                            )
                        }
                        val result = context.sketch.execute(request)
                        (result.image as? BitmapImage)?.bitmap?.let { bitmap ->
                            val hasGainmap = bitmap.hasGainmap()
                            withContext(Dispatchers.Main.immediate) {
                                context.setHdrMode(hasGainmap)
                            }
                            printWarning("Setting HDR Mode to $hasGainmap")
                        } ?: printWarning("Resulting image null")
                    } else {
                        withContext(Dispatchers.Main.immediate) {
                            context.setHdrMode(false)
                        }
                        printWarning("Not an image, skipping")
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                printWarning("Disposing HDR Mode")
                context.setHdrMode(false)
            }
        }
    }

    val eventViewModel = hiltViewModel<StatisticsViewModel>()

    val bottomPadding =
        remember(configuration.orientation) {
            //if (!isGestureEnabled && isLandscape) 0.dp
            //else
            paddingValues.calculateBottomPadding()
        }

    FullBrightnessWindow {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
//                    .graphicsLayer {
//                        translationY =
//                            -((sheetHeightDp -
//                                    bottomBarHeightDefault -
//                                    bottomPadding -
//                                    extraPaddingWithNavButtons -
//                                    if (!isGestureEnabled && isLandscape) navigationBarHeight else 0.dp
//                                    ).toPx() * normalizedOffset)
//                    },
                userScrollEnabled = userScrollEnabled,
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapAnimationSpec = tween(
                        easing = FastOutLinearInEasing,
                        durationMillis = DEFAULT_LOW_VELOCITY_SWIPE_DURATION
                    ),
                    snapPositionalThreshold = 0.3f
                ),
                key = { index ->
                    mediaState.value.media.getOrNull(index) ?: "empty"
                },
                pageSpacing = 16.dp,
                beyondViewportPageCount = 0
            ) { index ->

                val pageOffset = (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
                val mediaSize by animateFloatAsState(
                    targetValue = if (pageOffset != 0.0f) 0.6f else 1f,
                    animationSpec = tween( durationMillis = 300 )
                )

                val media by rememberedDerivedState(mediaState.value) {
                    mediaState.value.media.getOrNull(
                        index
                    )
                }

                //println("MediaViewScreen: currentMedia uri ${currentMedia?.getUri().toString()}")

                val canPlay = remember(playWhenReady, currentMedia, media, currentPage) {
                    mutableStateOf(playWhenReady && currentMedia == media && currentPage == index)
                }

                var checkEvent by remember { mutableStateOf(false) }
                LaunchedEffect(media) {
                    checkEvent = true
                }

                val transitionEffect by Settings.Misc.rememberTransitionEffect()

                AnimatedVisibility(
                    visible = media != null,
                    enter =  TransitionEffect.enter(
                        TransitionEffect.entries[transitionEffect]
                    ),
                    exit =  TransitionEffect.exit(
                        TransitionEffect.entries[transitionEffect]
                    )
                ) {

                    if (checkEvent) {
                        eventViewModel.insertEvent(
                            Event(
                                id = UUID.randomUUID().mostSignificantBits,
                                mediaId = media!!.id,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        checkEvent = false
                    }

                    var offset by remember {
                        mutableStateOf(IntOffset(0, 0))
                    }

                    LaunchedEffect(showUI, sheetState.currentDetent) {
                        if (showUI && sheetState.currentDetent == imageOnlyDetent) {
                            delay(5.seconds)
                            showUI = false
                            windowInsetsController.toggleSystemBars(false)
                        }
                    }


                        MediaPreviewComponent(
                            media = media,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = mediaSize
                                    scaleY = mediaSize
                                },
                            uiEnabled = showUI,
                            playWhenReady = canPlay,
                            onItemClick = {
                                if (sheetState.currentDetent == imageOnlyDetent) {
                                    showUI = !showUI
                                    windowInsetsController.toggleSystemBars(showUI)
                                }
                            },
                            onSwipeDown = {
                                windowInsetsController.toggleSystemBars(show = true)
                                navigateUp()
                            },
                            onSwipeUp = {
                                scope.launch {
                                    if (showUI) {
                                        if (sheetState.currentDetent == imageOnlyDetent) {
                                            sheetState.animateTo(FullyExpanded)
                                        } else {
                                            sheetState.animateTo(imageOnlyDetent)
                                        }
                                    }
                                }
                            },
                            offset = offset,
                            videoController = { player, isPlaying, currentTime, totalTime, buffer, frameRate ->
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    val hideUiOnPlay by rememberAutoHideOnVideoPlay()
                                    LaunchedEffect(isPlaying.value, hideUiOnPlay) {
                                        if (isPlaying.value && showUI && hideUiOnPlay) {
                                            delay(2.seconds)
                                            showUI = false
                                            windowInsetsController.toggleSystemBars(false)
                                        }
                                    }
                                    val width =
                                        remember(context) { context.resources.displayMetrics.widthPixels }
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                translationX = width / 1.5f
                                            }
                                            .align(Alignment.TopEnd)
                                            .clip(CircleShape)
                                            .combinedClickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                onDoubleClick = {
                                                    scope.launch {
                                                        currentTime.longValue += 10 * 1000
                                                        player.seekTo(currentTime.longValue)
                                                        delay(100)
                                                        player.play()
                                                    }
                                                },
                                                onClick = {
                                                    if (sheetState.currentDetent == imageOnlyDetent) {
                                                        showUI = !showUI
                                                        windowInsetsController.toggleSystemBars(
                                                            showUI
                                                        )
                                                    }
                                                }
                                            )
                                            .swipe(onOffset = { offset = it }) {
                                                windowInsetsController.toggleSystemBars(show = true)
                                                navigateUp()
                                            }
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                translationX = -width / 1.5f
                                            }
                                            .align(Alignment.TopStart)
                                            .clip(CircleShape)
                                            .combinedClickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                onDoubleClick = {
                                                    scope.launch {
                                                        currentTime.longValue -= 10 * 1000
                                                        player.seekTo(currentTime.longValue)
                                                        delay(100)
                                                        player.play()
                                                    }
                                                },
                                                onClick = {
                                                    if (sheetState.currentDetent == imageOnlyDetent) {
                                                        showUI = !showUI
                                                        windowInsetsController.toggleSystemBars(
                                                            showUI
                                                        )
                                                    }
                                                }
                                            )
                                            .swipe(onOffset = { offset = it }) {
                                                windowInsetsController.toggleSystemBars(show = true)
                                                navigateUp()
                                            }
                                    )

                                    AnimatedVisibility(
                                        visible = showUI,
                                        enter =  TransitionEffect.enter(
                                            TransitionEffect.entries[transitionEffect]
                                        ),
                                        exit =  TransitionEffect.exit(
                                            TransitionEffect.entries[transitionEffect]
                                        ),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        VideoPlayerController(
                                            paddingValues = paddingValues,
                                            player = player,
                                            isPlaying = isPlaying,
                                            currentTime = currentTime,
                                            totalTime = totalTime,
                                            buffer = buffer,
                                            toggleRotate = toggleRotate,
                                            frameRate = frameRate
                                        )
                                    }
                                }
                            }
                        )

                }
            }

            val currMedia = currentMedia ?: return@FullBrightnessWindow
            val exifInterface = rememberExifInterface(currMedia, true)

            val exifMetadata = rememberExifMetadata(currMedia, exifInterface)
            val locationData = rememberLocationData(exifMetadata, currMedia)
            MediaViewAppBar(
                modifier = Modifier.padding(
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                ),
                showUI = showUI,
                showInfo = showInfo,
                showDate = remember(currentMedia) {
                    currentMedia?.timestamp != 0L
                },
                currentDate = currentDate,
                paddingValues = paddingValues,
                onShowInfo = {
                    scope.launch {
                        if (showUI) {
                            if (sheetState.currentDetent == imageOnlyDetent) {
                                sheetState.animateTo(FullyExpanded)
                            } else {
                                sheetState.animateTo(imageOnlyDetent)
                            }
                        }
                    }
                },
                onGoBack = {
                    scope.launch {
                        if (sheetState.currentDetent == FullyExpanded) {
                            sheetState.animateTo(imageOnlyDetent)
                        } else {
                            navigateUp()
                        }
                    }
                },
                locationData = locationData
            )
            LaunchedEffect(showUI) {
                if (!showUI && (sheetState.currentDetent == FullyExpanded || sheetState.targetDetent == FullyExpanded)) {
                    sheetState.animateTo(imageOnlyDetent)
                }
            }
            BackHandler(sheetState.currentDetent == FullyExpanded) {
                scope.launch {
                    sheetState.animateTo(imageOnlyDetent)
                }
            }
            val bottomSheetAlpha by animateFloatAsState(
                targetValue = if (showUI) 1f else 0f,
                animationSpec = tween(DEFAULT_TOP_BAR_ANIMATION_DURATION),
                label = "MediaViewActionsAlpha"
            )
            val transitionEffect by Settings.Misc.rememberTransitionEffect()

            var showCast by remember { mutableStateOf(false) }

            BottomSheet(
                state = sheetState,
                enabled = showUI && target != TARGET_TRASH && showInfo,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        alpha = bottomSheetAlpha
                    }
                    .padding(
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                    )
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val actionsAlpha by animateFloatAsState(
                        targetValue = 1f - normalizedOffset,
                        label = "MediaViewActions2Alpha"
                    )

                    AnimatedVisibility(
                        visible = currentMedia != null,
                        enter =  TransitionEffect.enter(
                            TransitionEffect.entries[transitionEffect]
                        ),
                        exit =  TransitionEffect.exit(
                            TransitionEffect.entries[transitionEffect]
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .graphicsLayer {
                                    alpha = actionsAlpha
                                    translationY =
                                        bottomBarHeightDefault.toPx() * normalizedOffset
                                }
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, BlackScrim)
                                    )
                                )
                                .padding(
                                    bottom = bottomPadding + extraPaddingWithNavButtons
                                )
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            MediaViewActions2(
                                currentMedia = currentMedia,
                                handler = handler,
                                showDeleteButton = !isReadOnly,
                                enabled = showUI,
                                deleteMedia = deleteMedia,
                                restoreMedia = restoreMedia,
                                currentVault = currentVault,
                                infoMedia = {
                                    scope.launch {
                                        if (showUI) {
                                            if (sheetState.currentDetent == imageOnlyDetent) {
                                                sheetState.animateTo(FullyExpanded)
                                            } else {
                                                sheetState.animateTo(imageOnlyDetent)
                                            }
                                        }
                                    }
                                },
                                isAutoAdvanceEnabled = isAutoAdvanceEnabled,
                                onAutoAdvance = { isAutoAdvanceEnabled = it },
                                onCast = {
                                    scope.launch {
                                        sheetStateCast.show()
                                    }
                                }
                            )
                        }
                    }

                    MediaViewDetails(
                        albumsState = albumsState,
                        vaultState = vaultState,
                        currentMedia = currentMedia,
                        handler = handler,
                        addMediaToVault = addMedia,
                        restoreMedia = restoreMedia,
                        currentVault = currentVault,
                        navigate = navigate,
                    )
                }
            }

            if (sheetStateCast.isVisible)
                ModalBottomSheet(
                    sheetState = sheetStateCast,
                    onDismissRequest = {
                        scope.launch {
                            sheetStateCast.hide()
                        }
                    },
                    dragHandle = { DragHandle() }
                ) {
                    Cast(
                        currentMedia!!.path,
                        when {
                            currentMedia!!.isVideo -> CastMediaType.VIDEO
                            else -> CastMediaType.PHOTO
                        }
                    )
                }
        }
    }

}