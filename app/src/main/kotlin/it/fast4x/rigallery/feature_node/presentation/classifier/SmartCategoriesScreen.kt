package it.fast4x.rigallery.feature_node.presentation.classifier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.fast4x.rigallery.R
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.presentation.common.components.MediaImage
import it.fast4x.rigallery.feature_node.presentation.common.components.ScannerButton
import it.fast4x.rigallery.feature_node.presentation.common.components.TwoLinedDateToolbarTitle
import it.fast4x.rigallery.feature_node.presentation.library.ButtonToStartProcess
import it.fast4x.rigallery.feature_node.presentation.library.components.LibrarySmallItem
import it.fast4x.rigallery.feature_node.presentation.util.Screen
import it.fast4x.rigallery.feature_node.presentation.util.detectPinchGestures

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartCategoriesScreen(
    navigateUp: () -> Unit,
    navigate: (String) -> Unit,
) {
    val viewModel = hiltViewModel<CategoriesViewModel>()
    val categories by viewModel.classifiedCategories.collectAsStateWithLifecycle()
    val categoriesWithMedia by viewModel.categoriesWithMedia.collectAsStateWithLifecycle()
    val classifiedCount by viewModel.classifiedMediaCount.collectAsStateWithLifecycle()

    val categoriesIsEmpty by remember {
        derivedStateOf { categories.isEmpty() }
    }

    var canScroll by rememberSaveable { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { canScroll }
    )

    Box {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        TwoLinedDateToolbarTitle(
                            albumName = stringResource(R.string.smart_categories),
                            dateHeader = stringResource(R.string.classified_media, classifiedCount)
                        )
                    },
                    navigationIcon = {
//                        IconButton(onClick = navigateUp) {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                contentDescription = stringResource(R.string.back_cd)
//                            )
//                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddings ->

            val gridState = rememberLazyGridState()
            var level by remember { mutableIntStateOf(2) } //rememberAlbumGridSize()
            AnimatedVisibility(
                visible = level == 0,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                CategoriesMediaGrid(
                    gridState = gridState,
                    categoriesWithMedia = categoriesWithMedia,
                    categoriesIsEmpty = categoriesIsEmpty,
                    paddings = paddings,
                    canScroll = canScroll,
                    navigate = navigate,
                    columns = 1, nextLevel = 1, previousLevel = 0, onZoomLevelChange = { level = it }
                )
            }
            AnimatedVisibility(
                visible = level == 1,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                CategoriesMediaGrid(
                    gridState = gridState,
                    categoriesWithMedia = categoriesWithMedia,
                    categoriesIsEmpty = categoriesIsEmpty,
                    paddings = paddings,
                    canScroll = canScroll,
                    navigate = navigate,
                    columns = 2, nextLevel = 2, previousLevel = 0, onZoomLevelChange = { level = it }
                )
            }
            AnimatedVisibility(
                visible = level == 2,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                CategoriesMediaGrid(
                    gridState = gridState,
                    categoriesWithMedia = categoriesWithMedia,
                    categoriesIsEmpty = categoriesIsEmpty,
                    paddings = paddings,
                    canScroll = canScroll,
                    navigate = navigate,
                    columns = 4, nextLevel = 3, previousLevel = 1, onZoomLevelChange = { level = it }
                )
            }
            AnimatedVisibility(
                visible = level == 3,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                CategoriesMediaGrid(
                    gridState = gridState,
                    categoriesWithMedia = categoriesWithMedia,
                    categoriesIsEmpty = categoriesIsEmpty,
                    paddings = paddings,
                    canScroll = canScroll,
                    navigate = navigate,
                    columns = 6, nextLevel = 3, previousLevel = 2, onZoomLevelChange = { level = it }
                )
            }

        }
    }
}

@Composable
fun CategoriesMediaGrid(
    gridState: LazyGridState,
    categoriesWithMedia: List<Media.ClassifiedMedia>,
    categoriesIsEmpty: Boolean,
    paddings: PaddingValues,
    canScroll: Boolean,
    navigate: (String) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
    columns: Int, nextLevel: Int, previousLevel: Int, onZoomLevelChange: (Int) -> Unit
){
    val displayMode by remember { mutableStateOf(1) }
    var zoom by remember(displayMode) { mutableFloatStateOf(1f) }
    val zoomTransition: Float by animateFloatAsState(
        zoom,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    )
    LazyVerticalGrid(
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
            .fillMaxSize()
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
        contentPadding = remember(paddings) {
            PaddingValues(
                top = paddings.calculateTopPadding() + 24.dp,
                bottom = paddings.calculateBottomPadding() + 128.dp,
                start = 24.dp,
                end = 24.dp
            )
        },
        userScrollEnabled = canScroll,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(
            items = categoriesWithMedia,
            key = { it.category!! }
        ) { item ->
            Column(
                //modifier = Modifier.animateItem(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MediaImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    media = item,
                    selectedMedia = remember { mutableStateListOf() },
                    selectionState = remember { mutableStateOf(false) },
                    onItemClick = {
                        navigate(Screen.CategoryViewScreen.category(item.category!!))
                    },
                    onItemLongClick = { },
                    canClick = true
                )

                Text(
                    text = item.category!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        item(
            key = "Categories_scan",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            val isRunning by viewModel.isRunning.collectAsStateWithLifecycle()
            val progress by viewModel.progress.collectAsStateWithLifecycle()

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                    //.animateItem()
                    //.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (categoriesIsEmpty) {
                    ButtonToStartProcess(
                        title = stringResource(R.string.create_smart_categories),
                    ) {
                        viewModel.startClassification()
                    }
                }

                if (!categoriesIsEmpty || isRunning) {
                    ScannerButton(
                        scanForNewText = stringResource(R.string.create_smart_categories),
                        image = Icons.Outlined.Category,
                        isRunning = isRunning,
                        indicatorCounter = progress,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .combinedClickable(
                                onLongClick = {
                                    if (isRunning) viewModel.stopClassification()
                                },
                                onClick = {
                                    if (!isRunning) viewModel.startClassification()
                                    else viewModel.stopClassification()
                                }
                            )
                    )
                }

                if (!categoriesIsEmpty && !isRunning) {
                    LibrarySmallItem(
                        title = stringResource(R.string.delete_smart_categories),
                        //subtitle = stringResource(R.string.delete_all_categories_summary),
                        icon = Icons.Default.Delete,
                        contentColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .animateItem()
                            .clickable(onClick = viewModel::deleteClassifications)
                    )
                }

                LibrarySmallItem(
                    title = stringResource(R.string.info),
                    subtitle = stringResource(R.string.info_create_smart_categories),
                    icon = Icons.Default.Info,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    containerColor = Color.Transparent,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

