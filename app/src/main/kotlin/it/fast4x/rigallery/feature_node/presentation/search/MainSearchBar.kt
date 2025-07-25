/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0 license
 */

package it.fast4x.rigallery.feature_node.presentation.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.Settings.Misc.rememberAutoHideSearchBar
import it.fast4x.rigallery.core.Settings.Search.rememberSearchHistory
import it.fast4x.rigallery.core.Settings.Search.rememberSearchTagsHistory
import it.fast4x.rigallery.core.enums.TransitionEffect
import it.fast4x.rigallery.core.presentation.components.EmptyMedia
import it.fast4x.rigallery.feature_node.presentation.common.MediaViewModel
import it.fast4x.rigallery.feature_node.presentation.common.components.MediaGridView
import it.fast4x.rigallery.feature_node.presentation.search.components.SearchBarElevation.Collapsed
import it.fast4x.rigallery.feature_node.presentation.search.components.SearchBarElevation.Expanded
import it.fast4x.rigallery.feature_node.presentation.search.components.SearchHistory
import it.fast4x.rigallery.feature_node.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MainSearchBar(
    bottomPadding: Dp,
    selectionState: MutableState<Boolean>? = null,
    navigate: (String) -> Unit,
    toggleNavbar: (Boolean) -> Unit,
    isScrolling: MutableState<Boolean>,
    activeState: MutableState<Boolean>,
    menuItems: @Composable (RowScope.() -> Unit)? = null,
) {
    var historySet by rememberSearchHistory()
    var historyTagsSet by rememberSearchTagsHistory()
    var canQuery by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val mediaViewModel: MediaViewModel = hiltViewModel<MediaViewModel>().also {
        it.mediaFlow.collectAsStateWithLifecycle()
    }

    val mediaWithLocation = mediaViewModel.mediaWithLocation.collectAsStateWithLifecycle()
    //println("MainSearchBar: mediaWithLocation: ${mediaWithLocation.value.size}")

    val state = mediaViewModel.searchMediaState.collectAsStateWithLifecycle()

    val mediaFlowState = mediaViewModel.mediaFlow.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val transitionEffect by Settings.Misc.rememberTransitionEffect()

    LaunchedEffect(state.value.media, query, canQuery) {
        //println("MainSearchBar: LaunchedEffect query changed: $query and canQuery: $canQuery and state.value.media: ${state.value.media}")
        if (query.isNotEmpty() && state.value.media.isEmpty() && canQuery) {

            if (query.isNotEmpty()) {
                if (query.startsWith("#"))
                    historyTagsSet = historyTagsSet.toMutableSet().apply {
                        removeIf { historyQuery -> historyQuery.contains(query) }
                        add("${System.currentTimeMillis()}/$query")
                    }
                else
                    historySet = historySet.toMutableSet().apply {
                        removeIf { historyQuery -> historyQuery.contains(query) }
                        add("${System.currentTimeMillis()}/$query")
                    }
            }

            mediaViewModel.queryMedia(query)
        }
    }

    val lastQueryIsEmpty =
        remember(mediaViewModel.lastQuery.value) { mediaViewModel.lastQuery.value.isEmpty() }

    //val mediaIsEmpty = remember(state) { state.value.media.isEmpty() && !state.value.isLoading }

//    LaunchedEffect(query) {
//        println("MainSearchBar: LaunchedEffect query changed: $query and canQuery: $canQuery and state.value.media: ${state.value.media}")
//        if (query.isNotEmpty() && state.value.media.isEmpty() && canQuery) {
//            mediaViewModel.queryMedia(query)
//        }
//    }

    val alpha by animateFloatAsState(
        targetValue = if (selectionState != null && selectionState.value) 0.6f else 1f,
        label = "alpha"
    )
    val elevation by animateDpAsState(
        targetValue = if (activeState.value) Expanded() else Collapsed(),
        label = "elevation"
    )
    LaunchedEffect(activeState.value) {
        if (selectionState == null) {
            toggleNavbar(!activeState.value)
        }
    }
    val hideSearchBarSetting by rememberAutoHideSearchBar()

    var shouldHide by remember { mutableStateOf(if (hideSearchBarSetting) isScrolling.value else false) }

    LaunchedEffect(isScrolling.value) {
        snapshotFlow { isScrolling.value }
            .distinctUntilChanged()
            .collectLatest {
                shouldHide = if (hideSearchBarSetting) it else false
            }
    }

    Box(
        modifier = Modifier
            .semantics { isTraversalGroup = true }
            .zIndex(1f)
            .alpha(alpha)
            .fillMaxWidth()
    ) {
        val searchBarAlpha by animateFloatAsState(
            targetValue = if (shouldHide) 0f else 1f,
            label = "searchBarAlpha"
        )
        val onActiveChange: (Boolean) -> Unit = { activeState.value = it }
        val colors = SearchBarDefaults.colors(
            dividerColor = Color.Transparent,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )

        SearchBar(
            shape = MaterialTheme.shapes.small,
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = {
                        println("MainSearchBar: onQueryChange query: $it")
                        scope.launch {
                            query = it
                            if (it != mediaViewModel.lastQuery.value && mediaViewModel.lastQuery.value.isNotEmpty()) {
                                mediaViewModel.clearQuery()
                                canQuery = false
                            }
                        }
                    },
                    onSearch = {
                        println("MainSearchBar: onSearch query: $it")
                        if (it.isNotEmpty()) {
                            if (it.startsWith("#"))
                                historyTagsSet = historyTagsSet.toMutableSet().apply {
                                    removeIf { historyQuery -> historyQuery.contains(it) }
                                    add("${System.currentTimeMillis()}/$it")
                                }
                            else
                                historySet = historySet.toMutableSet().apply {
                                    removeIf { historyQuery -> historyQuery.contains(it) }
                                    add("${System.currentTimeMillis()}/$it")
                                }
                        }
                        mediaViewModel.queryMedia(it)
                        canQuery = true
                    },
                    expanded = activeState.value,
                    onExpandedChange = onActiveChange,
                    enabled = remember(selectionState?.value, shouldHide) {
                        (selectionState == null || !selectionState.value) && !shouldHide
                    },
                    placeholder = {
                        Text(text = stringResource(id = R.string.searchbar_title))
                    },
                    leadingIcon = {
                        IconButton(
                            enabled = remember(selectionState) {
                                selectionState == null
                            },
                            onClick = {
                                scope.launch {
                                    activeState.value = !activeState.value
                                    if (query.isNotEmpty()) query = ""
                                    mediaViewModel.clearQuery()
                                }
                            }
                        ) {
                                val leadingIcon = remember(activeState.value) {
                                    if (activeState.value)
                                        Icons.AutoMirrored.Outlined.ArrowBack else Icons.Outlined.Search
                                }
                                Icon(
                                    imageVector = leadingIcon,
                                    modifier = Modifier.fillMaxHeight(),
                                    contentDescription = null
                                )
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            enabled = query.isNotEmpty(),
                            onClick = {
                                scope.launch {
                                    canQuery = true
                                    if (query.isNotEmpty()) query = ""
                                    mediaViewModel.clearQuery()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                modifier = Modifier.fillMaxHeight(),
                                contentDescription = null
                            )
                        }

                        // TODO ICON ACTION IN THE SEARCH BAR
//                        Row {
//                            androidx.compose.animation.AnimatedVisibility(
//                                visible = !activeState.value,
//                            ) {
//                                menuItems?.invoke(this@Row)
//                            }
//                        }
                    },
                    interactionSource = remember { MutableInteractionSource() },
                )
            },
            expanded = activeState.value,
            onExpandedChange = onActiveChange,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .alpha(searchBarAlpha)
                .fillMaxWidth(),
            colors = colors,
            tonalElevation = elevation,
            content = {

                AnimatedVisibility(
                    visible = lastQueryIsEmpty,
                    enter =  TransitionEffect.enter(
                        TransitionEffect.entries[transitionEffect]
                    ),
                    exit =  TransitionEffect.exit(
                        TransitionEffect.entries[transitionEffect]
                    )
                ) {
                    SearchHistory(
                        mediaWithLocation,
                        mediaFlowState
                    ) { it, maybeCanQuery ->
                        canQuery = maybeCanQuery
                        query = it
                        if (maybeCanQuery)
                            mediaViewModel.queryMedia(it)
                    }
                }

                AnimatedVisibility(
                    visible = !lastQueryIsEmpty,
                    enter =  TransitionEffect.enter(
                        TransitionEffect.entries[transitionEffect]
                    ),
                    exit =  TransitionEffect.exit(
                        TransitionEffect.entries[transitionEffect]
                    )
                ) {

//                    if (mediaIsEmpty) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 72.dp),
//                            verticalArrangement = Arrangement.spacedBy(16.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Icon(
//                                imageVector = Icons.Outlined.ImageSearch,
//                                contentDescription = null,
//                                tint = MaterialTheme.colorScheme.primary,
//                                modifier = Modifier
//                                    .size(64.dp)
//                            )
//                            Text(
//                                text = stringResource(R.string.no_media_found),
//                                style = MaterialTheme.typography.titleMedium
//                            )
//                        }
//                    } else {
                        val pd = PaddingValues(
                            bottom = bottomPadding + 16.dp
                        )
                        var canScroll by rememberSaveable { mutableStateOf(true) }

                            MediaGridView(
                                mediaState = state,
                                paddingValues = pd,
                                canScroll = canScroll,
                                isScrolling = remember { mutableStateOf(false) },
                                onMediaClick = {
                                    navigate(Screen.MediaViewScreen.route + "?mediaId=${it.id}&query=true")
                                },
                                emptyContent = {
                                        EmptyMedia()
                                },
                            )

                        // emptymedia include loading indicator
//                            androidx.compose.animation.AnimatedVisibility(
//                                visible = state.value.isLoading,
//                                enter = enterAnimation,
//                                exit = exitAnimation
//                            ) {
//                                LoadingMedia()
//                            }


                    //}
                }
            },
        )
    }

    BackHandler(activeState.value) {
        scope.launch {
            if (mediaViewModel.lastQuery.value.isEmpty()) {
                activeState.value = false
            }
            canQuery = false
            query = ""
            mediaViewModel.clearQuery()
        }
    }
}
