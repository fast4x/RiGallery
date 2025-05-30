package it.fast4x.rigallery.feature_node.presentation.library

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings.Misc.rememberNoClassification
import it.fast4x.rigallery.feature_node.presentation.common.components.MediaImage
import it.fast4x.rigallery.feature_node.presentation.library.components.LibrarySmallItem
import it.fast4x.rigallery.feature_node.presentation.library.components.dashedBorder
import it.fast4x.rigallery.feature_node.presentation.search.MainSearchBar
import it.fast4x.rigallery.feature_node.presentation.util.Screen
import it.fast4x.rigallery.feature_node.presentation.util.detectPinchGestures
import it.fast4x.rigallery.feature_node.presentation.util.mediaSharedElement

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LibraryScreen(
    navigate: (route: String) -> Unit,
    toggleNavbar: (Boolean) -> Unit,
    paddingValues: PaddingValues,
    isScrolling: MutableState<Boolean>,
    searchBarActive: MutableState<Boolean>,
    //sharedTransitionScope: SharedTransitionScope,
    //animatedContentScope: AnimatedContentScope,
) {


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
                //sharedTransitionScope = sharedTransitionScope,
                //animatedContentScope = animatedContentScope
            ) {
                IconButton(onClick = { navigate(Screen.SettingsScreen()) }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.settings_title)
                    )
                }
            }
        }
    ) {
        val gridState = rememberLazyGridState()
        var level by remember { mutableIntStateOf(2) } //rememberAlbumGridSize()
        AnimatedVisibility(
            visible = level == 0,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            LibraryMediaGrid(
                gridState = gridState,
                paddingValues = it,
                navigate = navigate,
                //sharedTransitionScope = sharedTransitionScope,
                //animatedContentScope = animatedContentScope,
                columns = 1, nextLevel = 1, previousLevel = 0, onZoomLevelChange = { level = it }
            )
        }
        AnimatedVisibility(
            visible = level == 1,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            LibraryMediaGrid(
                gridState = gridState,
                paddingValues = it,
                navigate = navigate,
                //sharedTransitionScope = sharedTransitionScope,
                //animatedContentScope = animatedContentScope,
                columns = 2, nextLevel = 2, previousLevel = 0, onZoomLevelChange = { level = it }
            )
        }
        AnimatedVisibility(
            visible = level == 2,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            LibraryMediaGrid(
                gridState = gridState,
                paddingValues = it,
                navigate = navigate,
                //sharedTransitionScope = sharedTransitionScope,
                //animatedContentScope = animatedContentScope,
                columns = 4, nextLevel = 3, previousLevel = 1, onZoomLevelChange = { level = it }
            )
        }
        AnimatedVisibility(
            visible = level == 3,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            LibraryMediaGrid(
                gridState = gridState,
                paddingValues = it,
                navigate = navigate,
                //sharedTransitionScope = sharedTransitionScope,
                //animatedContentScope = animatedContentScope,
                columns = 6, nextLevel = 3, previousLevel = 2, onZoomLevelChange = { level = it }
            )
        }


    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LibraryMediaGrid(
    gridState: LazyGridState,
    paddingValues: PaddingValues,
    navigate: (String) -> Unit,
    //sharedTransitionScope: SharedTransitionScope,
    //animatedContentScope: AnimatedContentScope,
    columns: Int, nextLevel: Int, previousLevel: Int, onZoomLevelChange: (Int) -> Unit
){

    val viewModel = hiltViewModel<LibraryViewModel>()

    val classifiedCategories by viewModel.classifiedCategories.collectAsStateWithLifecycle()
    val mostPopularCategories by viewModel.mostPopularCategory.collectAsStateWithLifecycle()
    val mostPopularCategoriesKeys by remember {
        derivedStateOf { mostPopularCategories.keys.toList() }
    }
    val noCategoriesFound by remember {
        derivedStateOf { classifiedCategories.isEmpty() }
    }

    var noClassification by rememberNoClassification()

    val displayMode by remember { mutableStateOf(1) }
    var zoom by remember(displayMode) { mutableFloatStateOf(1f) }
    val zoomTransition: Float by animateFloatAsState(
        zoom,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    )
    LazyVerticalGrid(
        state = gridState,
        modifier = Modifier
            .padding(horizontal = 8.dp)
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
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding() + 128.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //Remove temporally  small button in library
//                item(
//                    span = { GridItemSpan(maxLineSpan) },
//                    key = "headerButtons"
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .animateItem()
//                            .fillMaxWidth()
//                            .pinchItem(key = "headerButtons")
//                            .padding(horizontal = 16.dp)
//                            .padding(top = 32.dp),
//                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(
//                                16.dp,
//                                Alignment.CenterHorizontally
//                            )
//                        ) {
//
//                            LibrarySmallItem(
//                                title = stringResource(R.string.trash),
//                                icon = Icons.Outlined.DeleteOutline,
//                                contentColor = MaterialTheme.colorScheme.primary,
//                                useIndicator = true,
//                                indicatorCounter = indicatorState.trashCount,
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .clickable {
//                                        navigate(Screen.TrashedScreen.route)
//                                    }
//                            )
//                            LibrarySmallItem(
//                                title = stringResource(R.string.favorites),
//                                icon = Icons.Outlined.FavoriteBorder,
//                                contentColor = MaterialTheme.colorScheme.error,
//                                useIndicator = true,
//                                indicatorCounter = indicatorState.favoriteCount,
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .clickable {
//                                        navigate(Screen.FavoriteScreen.route)
//                                    }
//                            )
//                        }
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(
//                                16.dp,
//                                Alignment.CenterHorizontally
//                            )
//                        ) {
//                            LibrarySmallItem(
//                                title = stringResource(R.string.vault),
//                                icon = GalleryIcons.Encrypted,
//                                contentColor = MaterialTheme.colorScheme.secondary,
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .clickable {
//                                        navigate(Screen.VaultScreen())
//                                    },
//                                contentDescription = stringResource(R.string.vault)
//                            )
//                            LibrarySmallItem(
//                                title = stringResource(R.string.ignored),
//                                icon = Icons.Outlined.VisibilityOff,
//                                contentColor = MaterialTheme.colorScheme.tertiary,
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .clickable {
//                                        navigate(Screen.IgnoredScreen())
//                                    }
//                            )
//                        }
//                    }
//                }

        if (!noClassification) {
            if (!noCategoriesFound) {
                item(
                    span = { GridItemSpan(maxLineSpan) },
                    key = "ClassifiedCategories"
                ) {
                    Column(
                        modifier = Modifier
                            //.animateItem()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LibrarySmallItem(
                            title = "All Categories",
                            icon = Icons.Outlined.ImageSearch,
                            contentColor = MaterialTheme.colorScheme.primary,
                            useIndicator = true,
                            indicatorCounter = classifiedCategories.size,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigate(Screen.CategoriesScreen())
                                }
                        )
                    }
                }
                if (mostPopularCategories.isNotEmpty()) {
                    items(
                        span = { GridItemSpan(maxLineSpan) },
                        items = mostPopularCategoriesKeys,
                        key = { it!! }
                    ) { category ->
                        Column(
                            modifier = Modifier
                                //.animateItem()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    navigate(Screen.CategoryViewScreen.category(category!!))
                                },
                            horizontalAlignment = Alignment.Start
                        ) {
                            val medias by remember {
                                derivedStateOf {
                                    mostPopularCategories.getOrDefault(
                                        category,
                                        emptyList()
                                    ).sortedByDescending { it.definedTimestamp }
                                }
                            }
                            ListItem(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .padding(bottom = 8.dp),
                                headlineContent = {
                                    Text(
                                        text = category!!,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                },
                                trailingContent = {
                                    Text(
                                        text = medias.size.toString(),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            )
                            LazyRow(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp)),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = medias,
                                    key = { it }
                                ) {
                                    //with(sharedTransitionScope) {
                                        MediaImage(
                                            modifier = Modifier
                                                .size(116.dp)
                                                .clip(RoundedCornerShape(16.dp)),
//                                                .mediaSharedElement(
//                                                    media = it,
//                                                    animatedVisibilityScope = animatedContentScope
//                                                ),
                                            media = it,
                                            selectedMedia = remember { mutableStateListOf() },
                                            selectionState = remember { mutableStateOf(false) },
                                            onItemClick = { media ->
                                                navigate(
                                                    Screen.MediaViewScreen.idAndCategory(
                                                        media.id,
                                                        category!!
                                                    )
                                                )
                                            },
                                            onItemLongClick = {
                                                navigate(
                                                    Screen.CategoryViewScreen.category(
                                                        category!!
                                                    )
                                                )
                                            },
                                            canClick = true
                                        )
                                    //}
                                }
                            }
                        }
                    }
                }
            }
            if (noCategoriesFound) {
                item(
                    span = { GridItemSpan(maxLineSpan) },
                    key = "NoCategories"
                ) {
                    ButtonToStartProcess(
                        modifier = Modifier
                           //.animateItem()
                            .padding(16.dp),
                        title = stringResource(R.string.categorise_your_media)
                    ) {
                        viewModel.startClassification()
                        //navigate(Screen.CategoriesScreen())
                    }
                }
                item(
                    span = { GridItemSpan(maxLineSpan) },
                    key = "Classification_Disclaimer"
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        LibrarySmallItem(
                            //modifier = Modifier
                            //    .animateItem(),
                            title = stringResource(R.string.disclaimer),
                            subtitle = stringResource(R.string.disclaimer_classification),
                            icon = Icons.Default.Info,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                item(
                    span = { GridItemSpan(maxLineSpan) },
                    key = "Classification_Disable"
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        LibrarySmallItem(
                            modifier = Modifier
                                //.animateItem()
                                .clickable {
                                    noClassification = true
                                },
                            title = stringResource(R.string.classification_unwanted),
                            subtitle = stringResource(R.string.classification_unwanted_summary),
                            icon = Icons.Default.QuestionMark,
                            contentColor = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonToStartProcess(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit = {}
) {
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
        )
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .dashedBorder(
                brush = brush,
                shape = RoundedCornerShape(16.dp),
                gapLength = 8.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberVectorPainter(image = Icons.Outlined.ImageSearch),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .drawWithContent {
                    with(drawContext.canvas.nativeCanvas) {
                        val checkPoint = saveLayer(null, null)
                        drawContent()
                        drawRect(
                            brush = brush,
                            blendMode = BlendMode.SrcIn
                        )
                        restoreToCount(checkPoint)
                    }
                }
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(brush = brush),
        )
    }
}