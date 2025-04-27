/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.core.presentation.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Constants
import it.fast4x.rigallery.core.Constants.Animation.navigateInAnimation
import it.fast4x.rigallery.core.Constants.Animation.navigateUpAnimation
import it.fast4x.rigallery.core.Constants.Target.TARGET_FAVORITES
import it.fast4x.rigallery.core.Constants.Target.TARGET_TRASH
import it.fast4x.rigallery.core.Settings.Album.rememberHideTimelineOnAlbum
import it.fast4x.rigallery.core.Settings.Misc.rememberLastScreen
import it.fast4x.rigallery.core.Settings.Misc.rememberTimelineGroupByMonth
import it.fast4x.rigallery.core.presentation.components.util.OnLifecycleEvent
import it.fast4x.rigallery.core.presentation.components.util.permissionGranted
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.presentation.albums.AlbumsScreen
import it.fast4x.rigallery.feature_node.presentation.albums.AlbumsViewModel
import it.fast4x.rigallery.feature_node.presentation.classifier.CategoriesScreen
import it.fast4x.rigallery.feature_node.presentation.classifier.CategoryViewModel
import it.fast4x.rigallery.feature_node.presentation.classifier.CategoryViewScreen
import it.fast4x.rigallery.feature_node.presentation.common.ChanneledViewModel
import it.fast4x.rigallery.feature_node.presentation.common.MediaViewModel
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionItem
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionSheet
import it.fast4x.rigallery.feature_node.presentation.dateformat.DateFormatScreen
import it.fast4x.rigallery.feature_node.presentation.favorites.FavoriteScreen
import it.fast4x.rigallery.feature_node.presentation.ignored.IgnoredScreen
import it.fast4x.rigallery.feature_node.presentation.ignored.setup.IgnoredSetup
import it.fast4x.rigallery.feature_node.presentation.library.LibraryScreen
import it.fast4x.rigallery.feature_node.presentation.mediaview.MediaViewScreen
import it.fast4x.rigallery.feature_node.presentation.music.MusicScreen
import it.fast4x.rigallery.feature_node.presentation.settings.SettingsScreen
import it.fast4x.rigallery.feature_node.presentation.setup.SetupScreen
import it.fast4x.rigallery.feature_node.presentation.timeline.TimelineScreen
import it.fast4x.rigallery.feature_node.presentation.trashed.TrashedGridScreen
import it.fast4x.rigallery.feature_node.presentation.util.Screen
import it.fast4x.rigallery.feature_node.presentation.util.rememberAppBottomSheetState
import it.fast4x.rigallery.feature_node.presentation.util.restartApplication
import it.fast4x.rigallery.feature_node.presentation.vault.VaultScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Stable
@NonRestartableComposable
@Composable
fun NavigationComp(
    navController: NavHostController,
    paddingValues: PaddingValues,
    bottomBarState: MutableState<Boolean>,
    systemBarFollowThemeState: MutableState<Boolean>,
    toggleRotate: () -> Unit,
    isScrolling: MutableState<Boolean>
) {
    val searchBarActive = rememberSaveable {
        mutableStateOf(false)
    }
    val bottomNavEntries = rememberNavigationItems()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navPipe = hiltViewModel<ChanneledViewModel>()
    navPipe
        .initWithNav(navController, bottomBarState)
        .collectAsStateWithLifecycle(
            LocalLifecycleOwner.current,
            context = Dispatchers.Main.immediate
        )
    //val groupTimelineByMonth by rememberTimelineGroupByMonth()

    val context = LocalContext.current
    var permissionState = rememberSaveable { context.permissionGranted(Constants.PERMISSIONS) }
    var lastStartScreen by rememberLastScreen()
    val startDest by rememberSaveable(permissionState, lastStartScreen) {
        mutableStateOf(
            if (permissionState) {
                lastStartScreen
            } else Screen.SetupScreen()
        )
    }
    val currentDest = remember(navController.currentDestination) {
        navController.currentDestination?.route ?: lastStartScreen
    }
    OnLifecycleEvent { _, event ->
        if (event == Lifecycle.Event.ON_STOP) {
            if (currentDest == Screen.TimelineScreen() || currentDest == Screen.AlbumsScreen()) {
                lastStartScreen = currentDest
            }
        }
    }

    var lastShouldDisplay by rememberSaveable {
        mutableStateOf(bottomNavEntries.find { item -> item.route == currentDest } != null)
    }
    val shouldSkipAuth = rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(navBackStackEntry) {
        navBackStackEntry?.destination?.route?.let {
            val shouldDisplayBottomBar =
                bottomNavEntries.find { item -> item.route == it } != null && !searchBarActive.value
            if (lastShouldDisplay != shouldDisplayBottomBar) {
                bottomBarState.value = shouldDisplayBottomBar
                lastShouldDisplay = shouldDisplayBottomBar
            }
            if (it != Screen.VaultScreen()) {
                shouldSkipAuth.value = false
            }
            systemBarFollowThemeState.value =
                !(it.contains(Screen.MediaViewScreen.route) || it.contains(Screen.VaultScreen()))
        }
    }

    // Preloaded viewModels
    val albumsViewModel = hiltViewModel<AlbumsViewModel>()
    val albumsState =
        albumsViewModel.albumsFlow.collectAsStateWithLifecycle(context = Dispatchers.IO)

    val timelineViewModel = hiltViewModel<MediaViewModel>()

    timelineViewModel.CollectDatabaseUpdates()

    val timelineState =
        timelineViewModel.mediaFlow.collectAsStateWithLifecycle(context = Dispatchers.IO)

    val vaultState = timelineViewModel.vaultsFlow.collectAsStateWithLifecycle(context = Dispatchers.IO)

    LaunchedEffect(permissionState) {
        timelineViewModel.updatePermissionState(permissionState)
    }

//    LaunchedEffect(Unit, groupTimelineByMonth) {
//        timelineViewModel.groupByMonth.value = groupTimelineByMonth
//    }
    // not necessary
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = startDest,
            enterTransition = { navigateInAnimation },
            exitTransition = { navigateUpAnimation },
            popEnterTransition = { navigateInAnimation },
            popExitTransition = { navigateUpAnimation }
        ) {
            composable(
                route = Screen.SetupScreen(),
            ) {
                LaunchedEffect(Unit) {
                    navPipe.toggleNavbar(false)
                }
                SetupScreen {
                    permissionState = true
                    context.restartApplication()
                    navPipe.navigate(Screen.TimelineScreen())
                }
            }
            composable(
                route = Screen.TimelineScreen()
            ) {
                TimelineScreen(
                    paddingValues = paddingValues,
                    handler = timelineViewModel.handler,
                    mediaState = timelineState,
                    albumsState = albumsState,
                    selectionState = timelineViewModel.multiSelectState,
                    selectedMedia = timelineViewModel.selectedPhotoState,
                    toggleSelection = timelineViewModel::toggleSelection,
                    navigate = navPipe::navigate,
                    navigateUp = navPipe::navigateUp,
                    toggleNavbar = navPipe::toggleNavbar,
                    isScrolling = isScrolling,
                    searchBarActive = searchBarActive,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }
            composable(
                route = Screen.MusicScreen()
            ) {
                MusicScreen(
                    paddingValues = paddingValues,
                    handler = timelineViewModel.handler,
                    mediaState = timelineState,
                    albumsState = albumsState,
                    selectionState = timelineViewModel.multiSelectState,
                    selectedMedia = timelineViewModel.selectedPhotoState,
                    toggleSelection = timelineViewModel::toggleSelection,
                    navigate = navPipe::navigate,
                    navigateUp = navPipe::navigateUp,
                    toggleNavbar = navPipe::toggleNavbar,
                    isScrolling = isScrolling,
                    searchBarActive = searchBarActive,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )

            }
            composable(
                route = Screen.TrashedScreen()
            ) {
                val vm = hiltViewModel<MediaViewModel>().apply {
                    target = TARGET_TRASH
                }
                val trashedMediaState =
                    vm.mediaFlow.collectAsStateWithLifecycle(context = Dispatchers.IO)
                TrashedGridScreen(
                    paddingValues = paddingValues,
                    handler = vm.handler,
                    mediaState = trashedMediaState,
                    albumsState = albumsState,
                    selectionState = vm.multiSelectState,
                    selectedMedia = vm.selectedPhotoState,
                    toggleSelection = vm::toggleSelection,
                    navigate = navPipe::navigate,
                    navigateUp = navPipe::navigateUp,
                    toggleNavbar = navPipe::toggleNavbar,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }
            composable(
                route = Screen.FavoriteScreen()
            ) {
                val vm = hiltViewModel<MediaViewModel>().apply {
                    target = TARGET_FAVORITES
                }
                val favoritesMediaState =
                    vm.mediaFlow.collectAsStateWithLifecycle(context = Dispatchers.IO)
                FavoriteScreen(
                    paddingValues = paddingValues,
                    handler = vm.handler,
                    mediaState = favoritesMediaState,
                    albumsState = albumsState,
                    selectionState = vm.multiSelectState,
                    selectedMedia = vm.selectedPhotoState,
                    toggleFavorite = vm::toggleFavorite,
                    toggleSelection = vm::toggleSelection,
                    navigate = navPipe::navigate,
                    navigateUp = navPipe::navigateUp,
                    toggleNavbar = navPipe::toggleNavbar,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }
            composable(
                route = Screen.AlbumsScreen()
            ) {
                AlbumsScreen(
                    navigate = navPipe::navigate,
                    toggleNavbar = navPipe::toggleNavbar,
                    mediaState = timelineState,
                    albumsState = albumsState,
                    paddingValues = paddingValues,
                    isScrolling = isScrolling,
                    searchBarActive = searchBarActive,
                    onAlbumClick = albumsViewModel.onAlbumClick(navPipe::navigate),
                    onAlbumLongClick = albumsViewModel.onAlbumLongClick,
                    filterOptions = albumsViewModel.rememberFilters(),
                    onMoveAlbumToTrash = albumsViewModel::moveAlbumToTrash,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }
            composable(
                route = Screen.AlbumViewScreen.albumAndName(),
                arguments = listOf(
                    navArgument(name = "albumId") {
                        type = NavType.LongType
                        defaultValue = -1
                    },
                    navArgument(name = "albumName") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val appName = _root_ide_package_.it.fast4x.rigallery.BuildConfig.APPLICATION_NAME
                val argumentAlbumName = remember(backStackEntry) {
                    backStackEntry.arguments?.getString("albumName") ?: appName
                }
                val argumentAlbumId = remember(backStackEntry) {
                    backStackEntry.arguments?.getLong("albumId") ?: -1
                }
                val vm = hiltViewModel<MediaViewModel>().apply {
                    albumId = argumentAlbumId
                }
                val hideTimeline by rememberHideTimelineOnAlbum()
                val mediaState = vm.mediaFlow.collectAsStateWithLifecycle(context = Dispatchers.IO)
                TimelineScreen(
                    paddingValues = paddingValues,
                    albumId = argumentAlbumId,
                    albumName = argumentAlbumName,
                    handler = vm.handler,
                    mediaState = mediaState,
                    albumsState = albumsState,
                    selectionState = vm.multiSelectState,
                    selectedMedia = vm.selectedPhotoState,
                    allowNavBar = false,
                    allowHeaders = !hideTimeline,
                    enableStickyHeaders = !hideTimeline,
                    toggleSelection = vm::toggleSelection,
                    navigate = navPipe::navigate,
                    navigateUp = navPipe::navigateUp,
                    toggleNavbar = navPipe::toggleNavbar,
                    isScrolling = isScrolling,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }
            composable(
                route = Screen.MediaViewScreen.idAndAlbum(),
                arguments = listOf(
                    navArgument(name = "mediaId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                    navArgument(name = "albumId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val mediaId: Long = remember(backStackEntry) {
                    backStackEntry.arguments?.getLong("mediaId") ?: -1L
                }
                val albumId: Long = remember(backStackEntry) {
                    backStackEntry.arguments?.getLong("albumId") ?: -1L
                }
                val entryName = remember(backStackEntry) {
                    if (albumId == -1L) {
                        Screen.TimelineScreen.route
                    } else {
                        Screen.AlbumViewScreen.route
                    }
                }

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(entryName)
                }
                val vm = hiltViewModel<MediaViewModel>(parentEntry).apply {
                    this.albumId = albumId
                }
                val mediaState = if (entryName == Screen.AlbumViewScreen()) {
                    vm.mediaFlow.collectAsStateWithLifecycle(context = Dispatchers.IO)
                } else timelineState

                MediaViewScreen(
                    navigateUp = navPipe::navigateUp,
                    toggleRotate = toggleRotate,
                    paddingValues = paddingValues,
                    mediaId = mediaId,
                    mediaState = mediaState,
                    albumsState = albumsState,
                    handler = vm.handler,
                    addMedia = vm::addMedia,
                    vaultState = vaultState,
                    navigate = navPipe::navigate,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }
            composable(
                route = Screen.MediaViewScreen.idAndTarget(),
                arguments = listOf(
                    navArgument(name = "mediaId") {
                        type = NavType.LongType
                        defaultValue = -1
                    },
                    navArgument(name = "target") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val mediaId: Long = remember(backStackEntry) {
                    backStackEntry.arguments?.getLong("mediaId") ?: -1
                }
                val target: String? = remember(backStackEntry) {
                    backStackEntry.arguments?.getString("target")
                }
                val entryName = remember(target) {
                    when (target) {
                        TARGET_FAVORITES -> Screen.FavoriteScreen.route
                        TARGET_TRASH -> Screen.TrashedScreen.route
                        else -> Screen.TimelineScreen.route
                    }
                }
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(entryName)
                }
                val viewModel = hiltViewModel<MediaViewModel>(parentEntry).apply {
                    this.target = target
                }
                val mediaState =
                    viewModel.mediaFlow.collectAsStateWithLifecycle(context = Dispatchers.IO)

                MediaViewScreen(
                    navigateUp = navPipe::navigateUp,
                    toggleRotate = toggleRotate,
                    paddingValues = paddingValues,
                    mediaId = mediaId,
                    target = target,
                    mediaState = mediaState,
                    albumsState = albumsState,
                    handler = viewModel.handler,
                    addMedia = viewModel::addMedia,
                    vaultState = vaultState,
                    navigate = navPipe::navigate,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }
            composable(
                route = Screen.MediaViewScreen.idAndQuery(),
                arguments = listOf(
                    navArgument(name = "mediaId") {
                        type = NavType.LongType
                        defaultValue = -1
                    },
                    navArgument(name = "query") {
                        type = NavType.BoolType
                        defaultValue = true
                    }
                )
            ) { backStackEntry ->
                val mediaId: Long = remember(backStackEntry) {
                    backStackEntry.arguments?.getLong("mediaId") ?: -1
                }
                val query: Boolean = remember(backStackEntry) {
                    backStackEntry.arguments?.getBoolean("query") ?: true
                }
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.TimelineScreen.route)
                }
                val viewModel = hiltViewModel<MediaViewModel>(parentEntry)
                val mediaState = remember(query) {
                    if (query) viewModel.searchMediaState else viewModel.mediaFlow
                }.collectAsStateWithLifecycle(context = Dispatchers.IO)


                MediaViewScreen(
                    navigateUp = navPipe::navigateUp,
                    toggleRotate = toggleRotate,
                    paddingValues = paddingValues,
                    mediaId = mediaId,
                    mediaState = mediaState,
                    albumsState = albumsState,
                    handler = viewModel.handler,
                    addMedia = viewModel::addMedia,
                    vaultState = vaultState,
                    navigate = navPipe::navigate,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }
            composable(
                route = Screen.SettingsScreen()
            ) {
                SettingsScreen(
                    navigateUp = navPipe::navigateUp,
                    navigate = navPipe::navigate
                )
            }
            composable(
                route = Screen.IgnoredScreen()
            ) {
                IgnoredScreen(
                    navigateUp = navPipe::navigateUp,
                    startSetup = { navPipe.navigate(Screen.IgnoredSetupScreen()) },
                    albumsState = albumsState
                )
            }

            composable(
                route = Screen.IgnoredSetupScreen()
            ) {
                IgnoredSetup(
                    onCancel = navPipe::navigateUp,
                    albumState = albumsState
                )
            }

            composable(
                route = Screen.VaultScreen()
            ) {
                VaultScreen(
                    paddingValues = paddingValues,
                    toggleRotate = toggleRotate,
                    shouldSkipAuth = shouldSkipAuth,
                    navigateUp = navPipe::navigateUp,
                    navigate = navPipe::navigate
                )
            }

            composable(
                route = Screen.LibraryScreen()
            ) {
                LibraryScreen(
                    navigate = navPipe::navigate,
                    toggleNavbar = navPipe::toggleNavbar,
                    paddingValues = paddingValues,
                    isScrolling = isScrolling,
                    searchBarActive = searchBarActive,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }

            composable(
                route = Screen.CategoriesScreen()
            ) {
                CategoriesScreen(
                    navigateUp = navPipe::navigateUp,
                    navigate = navPipe::navigate
                )
            }

            composable(
                route = Screen.CategoryViewScreen.category(),
                arguments = listOf(
                    navArgument(name = "category") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val category: String = remember(backStackEntry) {
                    backStackEntry.arguments?.getString("category") ?: ""
                }
                CategoryViewScreen(
                    navigateUp = navPipe::navigateUp,
                    navigate = navPipe::navigate,
                    toggleNavbar = navPipe::toggleNavbar,
                    category = category,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }

            composable(
                route = Screen.MediaViewScreen.idAndCategory(),
                arguments = listOf(
                    navArgument(name = "mediaId") {
                        type = NavType.LongType
                        defaultValue = -1
                    },
                    navArgument(name = "category") {
                        type = NavType.StringType
                        defaultValue = "null"
                    }
                )
            ) { backStackEntry ->
                val mediaId: Long = remember(backStackEntry) {
                    backStackEntry.arguments?.getLong("mediaId") ?: -1
                }
                val category: String = remember(backStackEntry) {
                    backStackEntry.arguments?.getString("category", "null").toString()
                }

                val viewModel = hiltViewModel<CategoryViewModel>().apply {
                    this.category = category
                }
                val mediaState = viewModel.mediaByCategory
                    .collectAsStateWithLifecycle(MediaState())

                MediaViewScreen(
                    navigateUp = navPipe::navigateUp,
                    toggleRotate = toggleRotate,
                    paddingValues = paddingValues,
                    mediaId = mediaId,
                    target = "category_$category",
                    mediaState = mediaState,
                    albumsState = albumsState,
                    handler = viewModel.handler,
                    addMedia = viewModel::addMedia,
                    vaultState = vaultState,
                    navigate = navPipe::navigate,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }

            composable(
                route = Screen.DateFormatScreen()
            ) {
                DateFormatScreen(navigateUp = navPipe::navigateUp)
            }



        }
    }
}