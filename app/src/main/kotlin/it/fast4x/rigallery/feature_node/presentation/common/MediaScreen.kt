/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.presentation.common

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Constants.Target.TARGET_TRASH
import it.fast4x.rigallery.core.Constants.cellsList
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.Settings.Misc.rememberGridSize
import it.fast4x.rigallery.core.enums.MediaType
import it.fast4x.rigallery.core.enums.Option
import it.fast4x.rigallery.core.presentation.components.EmptyMedia
import it.fast4x.rigallery.core.presentation.components.NavigationActions
import it.fast4x.rigallery.core.presentation.components.NavigationButton
import it.fast4x.rigallery.core.presentation.components.OptionSheetMenu
import it.fast4x.rigallery.core.presentation.components.SelectionSheet
import it.fast4x.rigallery.feature_node.domain.model.AlbumState
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import it.fast4x.rigallery.feature_node.presentation.common.components.MediaGridView
import it.fast4x.rigallery.feature_node.presentation.common.components.TwoLinedDateToolbarTitle
import it.fast4x.rigallery.feature_node.presentation.search.MainSearchBar
import it.fast4x.rigallery.feature_node.presentation.util.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun <T: Media> MediaScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    albumId: Long = remember { -1L },
    target: String? = remember { null },
    albumName: String,
    handler: MediaHandleUseCase,
    albumsState: State<AlbumState> = remember { mutableStateOf(AlbumState()) },
    mediaState: State<MediaState<T>>,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    toggleSelection: (Int) -> Unit,
    allowHeaders: Boolean = true,
    showMonthlyHeader: Boolean = true,
    enableStickyHeaders: Boolean = true,
    allowNavBar: Boolean = false,
    customDateHeader: String? = null,
    customViewingNavigation: ((media: T) -> Unit)? = null,
    navActionsContent: @Composable (RowScope.(expandedDropDown: MutableState<Boolean>, result: ActivityResultLauncher<IntentSenderRequest>) -> Unit),
    emptyContent: @Composable () -> Unit = { EmptyMedia() },
    aboveGridContent: @Composable (() -> Unit)? = remember { null },
    navigate: (route: String) -> Unit,
    navigateUp: () -> Unit,
    toggleNavbar: (Boolean) -> Unit,
    isScrolling: MutableState<Boolean> = remember { mutableStateOf(false) },
    searchBarActive: MutableState<Boolean> = remember { mutableStateOf(false) },
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onActivityResult: (result: ActivityResult) -> Unit,
) {
    val showSearchBar = remember { albumId == -1L && target == null }
    var canScroll by rememberSaveable { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { canScroll },
        flingAnimationSpec = null
    )

    LaunchedEffect(selectionState.value) {
        if (allowNavBar) {
            toggleNavbar(!selectionState.value)
        }
    }

    var showMediaTypeMenu by remember { mutableStateOf(false) }
    var showMediaType by Settings.Misc.rememberShowMediaType()

    Box(
        modifier = Modifier
            .padding(
                start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
            )
    ) {
        Scaffold(
            modifier = Modifier
                .then(
                    if (!showSearchBar)
                        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    else Modifier
                ),
            topBar = {
                if (!showSearchBar) {
                    LargeTopAppBar(
                        title = {
                            TwoLinedDateToolbarTitle(
                                albumName = albumName,
                                dateHeader = customDateHeader ?: mediaState.value.dateHeader
                            )
                        },
                        navigationIcon = {
                            NavigationButton(
                                albumId = albumId,
                                target = target,
                                navigateUp = navigateUp,
                                clearSelection = {
                                    selectionState.value = false
                                    selectedMedia.clear()
                                },
                                selectionState = selectionState,
                                alwaysGoBack = true,
                            )
                        },
                        actions = {
                            NavigationActions(
                                actions = navActionsContent,
                                onActivityResult = onActivityResult
                            )
                        },
                        scrollBehavior = scrollBehavior
                    )
                } else {
                    MainSearchBar(
                        bottomPadding = paddingValues.calculateBottomPadding(),
                        navigate = navigate,
                        toggleNavbar = toggleNavbar,
                        selectionState = remember(selectedMedia) {
                            if (selectedMedia.isNotEmpty()) selectionState else null
                        },
                        isScrolling = isScrolling,
                        activeState = searchBarActive,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                    ) {
                        NavigationActions(
                            actions = navActionsContent,
                            onActivityResult = onActivityResult
                        )
                    }
                }
            }
        ) { it ->
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
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
                            text = MediaType.entries[showMediaType].title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable{
                                    showMediaTypeMenu = true
                                }
                                .padding(5.dp)
                        )
//                        IconButton(onClick = { showMediaTypeMenu = true }) {
//                            Icon(
//                                imageVector = MediaType.entries[showMediaType].icon,
//                                contentDescription = null
//                            )
//                        }

                        Text(text = mediaState.value.media.size.toString(),
                            fontStyle = MaterialTheme.typography.labelSmall.fontStyle)
                    }

                    MediaGridView(
                        mediaState = mediaState,
                        allowSelection = true,
                        showSearchBar = showSearchBar,
                        searchBarPaddingTop = remember(paddingValues) {
                            paddingValues.calculateTopPadding()
                        },
                        enableStickyHeaders = enableStickyHeaders,
                        paddingValues = remember(paddingValues, it) {
                            PaddingValues(
                                start = 5.dp,
                                end = 5.dp,
                                top = 5.dp, //it.calculateTopPadding(),
                                bottom = 5.dp, //paddingValues.calculateBottomPadding() + 128.dp
                            )
                        },
                        canScroll = canScroll,
                        selectionState = selectionState,
                        selectedMedia = selectedMedia,
                        allowHeaders = allowHeaders,
                        showMonthlyHeader = showMonthlyHeader,
                        toggleSelection = toggleSelection,
                        aboveGridContent = aboveGridContent,
                        isScrolling = isScrolling,
                        emptyContent = emptyContent,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    ) {
                        if (customViewingNavigation == null) {
                            val albumRoute = "albumId=$albumId"
                            val targetRoute = "target=$target"
                            val param =
                                if (target != null) targetRoute else albumRoute
                            navigate(Screen.MediaViewScreen.route + "?mediaId=${it.id}&$param")
                        } else {
                            customViewingNavigation(it)
                        }
                    }

                }

            OptionSheetMenu(
                title = "Media type",
                options = MediaType.entries.map{ option ->
                    Option(
                        ordinal = option.ordinal,
                        name = option.name,
                        title = option.title,
                        icon = option.icon
                    )
                },
                visible = showMediaTypeMenu,
                onSelected = { showMediaType = it },
                onDismiss = { showMediaTypeMenu = false }
            )

        }
        if (target != TARGET_TRASH) {
            SelectionSheet(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                selectedMedia = selectedMedia,
                selectionState = selectionState,
                albumsState = albumsState,
                handler = handler
            )
        }
    }
}