/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.core.presentation.components

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings.Misc.rememberAutoHideNavBar
import it.fast4x.rigallery.core.Settings.Misc.rememberNoClassification

import it.fast4x.rigallery.feature_node.presentation.common.components.OptionItem
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionSheet
import it.fast4x.rigallery.feature_node.presentation.util.NavigationItem
import it.fast4x.rigallery.feature_node.presentation.util.Screen
import it.fast4x.rigallery.feature_node.presentation.util.rememberAppBottomSheetState
import it.fast4x.rigallery.ui.core.icons.Encrypted
import kotlinx.coroutines.launch

@Composable
fun rememberNavigationItems(): List<NavigationItem> {
    var noClassification by rememberNoClassification()
    val timelineTitle = stringResource(R.string.nav_timeline)
    val albumsTitle = stringResource(R.string.nav_albums)
    //val libraryTitle = stringResource(R.string.library)
    val smartCategoriesTitle = stringResource(R.string.smart_categories)
    val favoritesTitle = stringResource(R.string.favorites)
    val navigationItems = remember { mutableListOf<NavigationItem>() }
    navigationItems
        .also { it.clear() }
        .apply {
            add(
                NavigationItem(
                    name = timelineTitle,
                    route = Screen.TimelineScreen.route,
                    icon = Icons.Outlined.Photo,
                )
            )
            add(
                NavigationItem(
                    name = albumsTitle,
                    route = Screen.AlbumsScreen.route,
                    icon = Icons.Outlined.Album,
                )
            )
            if (noClassification == false) {
                add(
                    NavigationItem(
                        name = smartCategoriesTitle,
                        route = Screen.CategoriesScreen(),
                        icon = Icons.Outlined.Category
                    )
                )
            }
            add(
                NavigationItem(
                    name = favoritesTitle,
                    route = Screen.FavoriteScreen(),
                    icon = Icons.Outlined.Favorite
                )
            )
        }

    return remember {navigationItems}
//    return remember {
//        mutableListOf(
//            NavigationItem(
//                name = timelineTitle,
//                route = Screen.TimelineScreen.route,
//                icon = Icons.Outlined.Photo,
//            ),
//            NavigationItem(
//                name = albumsTitle,
//                route = Screen.AlbumsScreen.route,
//                icon = Icons.Outlined.Album,
//            ),
////            NavigationItem(
////                name = libraryTitle,
////                route = Screen.LibraryScreen(),
////                icon = Icons.Outlined.PhotoLibrary
////            ),
//            NavigationItem(
//                name = smartCategoriesTitle,
//                route = Screen.CategoriesScreen(),
//                icon = Icons.Outlined.Category
//            ),
//            NavigationItem(
//                name = favoritesTitle,
//                route = Screen.FavoriteScreen(),
//                icon = Icons.Outlined.Favorite
//            )
//        )
//    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Stable
@Composable
fun AppBarContainer(
    navController: NavController,
    bottomBarState: Boolean,
    paddingValues: PaddingValues,
    isScrolling: Boolean,
    onShowSearchBar: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val bottomNavItems = rememberNavigationItems()
    val useNavRail by remember(windowSizeClass) {
        mutableStateOf(windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact)
    }
    //val useOldNavbar by rememberOldNavbar()

//
    val scope = rememberCoroutineScope()
    val expandedDropDown = remember { mutableStateOf(false) }
    val toggleSearchBar = remember { mutableStateOf(false) }
    val appBottomSheetState = rememberAppBottomSheetState()
    LaunchedEffect(appBottomSheetState.isVisible, expandedDropDown.value) {
        scope.launch {
            if (expandedDropDown.value) appBottomSheetState.show()
            else appBottomSheetState.hide()
        }
    }

    val optionList = remember {
        mutableListOf(
//            OptionItem(
//                text = context.getString(R.string.favorites),
//                icon = Icons.Filled.Favorite,
//                onClick = {
//                    navController.navigate(Screen.FavoriteScreen.route)
//                    expandedDropDown.value = false
//                }
//            ),
            OptionItem(
                text = "Analysis media" ,//context.getString(R.string.ignored_albums),
                icon = Icons.Filled.QueryStats,
                onClick = {
                    navController.navigate(Screen.AnalysisScreen.route)
                    expandedDropDown.value = false
                }
            ),
            OptionItem(
                text = context.getString(R.string.ignored_albums),
                icon = Icons.Filled.VisibilityOff,
                onClick = {
                    navController.navigate(Screen.IgnoredScreen.route)
                    expandedDropDown.value = false
                }
            ),
            OptionItem(
                text = context.getString(R.string.ignored_media),
                icon = Icons.Filled.VisibilityOff,
                onClick = {
                    navController.navigate(Screen.IgnoredMediaScr.route)
                    expandedDropDown.value = false
                }
            ),
            OptionItem(
                text = context.getString(R.string.vault),
                icon = it.fast4x.rigallery.ui.core.Icons.Encrypted,
                onClick = {
                    navController.navigate(Screen.VaultScreen.route)
                    expandedDropDown.value = false
                }
            ),
            OptionItem(
                text = context.getString(R.string.trash),
                icon = Icons.Filled.Delete,
                onClick = {
                    navController.navigate(Screen.TrashedScreen.route)
                    expandedDropDown.value = false
                }
            ),
            OptionItem(
                text = "Statistics",
                icon = Icons.Filled.QueryStats,
                onClick = {
                    navController.navigate(Screen.StatisticsScreen.route)
                    expandedDropDown.value = false
                }
            )

        )
    }
    val tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer
    val onTertiaryContainer = MaterialTheme.colorScheme.onTertiaryContainer
    val settingsOption = remember {
        mutableStateListOf(
            OptionItem(
                text = context.getString(R.string.settings_title),
                icon = Icons.Filled.Settings,
                containerColor = tertiaryContainer,
                contentColor = onTertiaryContainer,
                onClick = {
                    navController.navigate(Screen.SettingsScreen.route)
                    expandedDropDown.value = false
                }
            )
        )
    }

    OptionSheet(
        headerContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = stringResource(id = R.string.navigation),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

        },
        state = appBottomSheetState,
        onDismiss = {
            expandedDropDown.value = false
        },
        optionList = arrayOf(optionList, settingsOption)
    )

//

    // NOT REQUIRED
//    AnimatedVisibility(
//        visible = useOldNavbar,
//        enter = enterAnimation,
//        exit = exitAnimation
//    ) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            val anySelectedRoute = remember(backStackEntry) {
//                bottomNavItems.any { it.route == navController.currentDestination?.route }
//            }
//            val showNavRail by remember(useNavRail, bottomBarState, anySelectedRoute) {
//                derivedStateOf {
//                    useNavRail && bottomBarState && anySelectedRoute
//                }
//            }
//            AnimatedVisibility(
//                visible = showNavRail,
//                enter = slideInHorizontally { it * -2 },
//                exit = slideOutHorizontally { it * -2 }
//            ) {
//                ClassicNavigationRail(
//                    backStackEntry = backStackEntry,
//                    navigationItems = bottomNavItems,
//                    onClick = { navigate(navController, it) }
//                )
//            }
//            val animatedPadding by animateDpAsState(
//                targetValue = remember(showNavRail) {
//                    if (showNavRail) 80.dp else 0.dp
//                },
//                label = "animatedPadding"
//            )
//            Box(
//                modifier = Modifier.padding(start = animatedPadding)
//            ) {
//                content()
//            }
//            val hideNavBarSetting by rememberAutoHideNavBar()
//            val showClassicNavbar by remember(useNavRail, bottomBarState, isScrolling, hideNavBarSetting, anySelectedRoute) {
//                derivedStateOf {
//                    !useNavRail && bottomBarState && (!isScrolling || !hideNavBarSetting) && anySelectedRoute
//                }
//            }
//            AnimatedVisibility(
//                modifier = Modifier.align(Alignment.BottomCenter),
//                visible = showClassicNavbar,
//                enter = slideInVertically { it * 2 },
//                exit = slideOutVertically { it * 2 },
//                content = {
//                    ClassicNavBar(
//                        backStackEntry = backStackEntry,
//                        navigationItems = bottomNavItems,
//                        onClick = { navigate(navController, it) },
//                    )
//                }
//            )
//        }
//    }
//    AnimatedVisibility(
//        visible = !useOldNavbar,
//        enter = enterAnimation,
//        exit = exitAnimation
//    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            content()
            val hideNavBarSetting by rememberAutoHideNavBar()
            val anySelectedRoute = remember(backStackEntry) {
                bottomNavItems.any { it.route == navController.currentDestination?.route }
            }
            val showNavbar by remember(bottomBarState, isScrolling, hideNavBarSetting, anySelectedRoute) {
                derivedStateOf {
                    bottomBarState && (!isScrolling || !hideNavBarSetting) && anySelectedRoute
                }
            }
            //var showSearchBar by remember(showSearchBar) { mutableStateOf(showSearchBar) }

            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                visible = showNavbar,
                enter = fadeIn(),
                exit = fadeOut(),
//                enter = slideInVertically { it * 2 },
//                exit = slideOutVertically { it * 2 },
                content = {
                    val modifier = remember(useNavRail) {
                        if (useNavRail) Modifier.requiredWidth((110 * bottomNavItems.size).dp)
                        else Modifier.fillMaxWidth()
                    }
                    GalleryNavBar(
                        modifier = modifier,
                        backStackEntry = backStackEntry,
                        navigationItems = bottomNavItems,
                        onClick = { navigate(navController, it) },
                        onCustomItemClick = {
                            expandedDropDown.value = true
                        },
                        onShowSearchBar = {
                            toggleSearchBar.value = !toggleSearchBar.value
                            onShowSearchBar(toggleSearchBar.value)
                        }
                    )
                }
            )
        }
    //}
}

private fun navigate(navController: NavController, route: String) {
    navController.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

@Composable
fun GalleryNavBar(
    modifier: Modifier,
    backStackEntry: NavBackStackEntry?,
    navigationItems: List<NavigationItem>,
    onClick: (route: String) -> Unit,
    onCustomItemClick: (msg: String) -> Unit,
    onShowSearchBar: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(all = 5.dp)
            .then(modifier)
            .height(64.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                shape = RoundedCornerShape(percent = 12)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        navigationItems.forEach { item ->
            val selected = remember(item, backStackEntry) {
                item.route == backStackEntry?.destination?.route
            }
            GalleryNavBarItem(
                navItem = item,
                isSelected = selected,
                onClick = onClick
            )
        }

        GalleryNavBarItem(
            navItem = NavigationItem(
                name = stringResource(R.string.app_name),
                route = "", // no route needed,
                icon = Icons.Outlined.Search
            ),
            isSelected = false,
            onClick = onShowSearchBar
        )
        GalleryNavBarItem(
            navItem = NavigationItem(
                name = stringResource(R.string.app_name),
                route = "", // no route needed,
                icon = Icons.Outlined.MoreVert
            ),
            isSelected = false,
            onClick = onCustomItemClick
        )

    }
}

@Stable
@Composable
private fun Label(item: NavigationItem) = Text(
    text = item.name,
    fontWeight = FontWeight.Medium,
    style = MaterialTheme.typography.bodyMedium,
)

@Stable
@Composable
private fun Icon(item: NavigationItem) = Icon(
    imageVector = item.icon,
    contentDescription = item.name,
)

@Composable
fun ClassicNavBar(
    backStackEntry: NavBackStackEntry?,
    navigationItems: List<NavigationItem>,
    onClick: (route: String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    ) {
        navigationItems.forEach { item ->
            val selected = item.route == backStackEntry?.destination?.route
            NavigationBarItem(
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                onClick = {
                    if (!selected) {
                        onClick(item.route)
                    }
                },
                label = { Label(item) },
                icon = { Icon(item) }
            )
        }
    }
}

@Composable
private fun ClassicNavigationRail(
    backStackEntry: NavBackStackEntry?,
    navigationItems: List<NavigationItem>,
    onClick: (route: String) -> Unit
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Spacer(Modifier.weight(1f))
        navigationItems.forEach { item ->
            val selected = item.route == backStackEntry?.destination?.route
            NavigationRailItem(
                selected = selected,
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                onClick = {
                    if (!selected) {
                        onClick(item.route)
                    }
                },
                label = { Label(item) },
                icon = { Icon(item) }
            )
        }
        Spacer(Modifier.weight(1f))
    }

}

@Composable
fun RowScope.GalleryNavBarItem(
    navItem: NavigationItem,
    isSelected: Boolean,
    onClick: (route: String) -> Unit,
) {
    val mutableInteraction = remember { MutableInteractionSource() }
    val selectedColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        label = "selectedColor"
    )
    val selectedIconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "selectedIconColor"
    )
    Box(
        modifier = Modifier
            .height(64.dp)
            .weight(1f)
            // Dummy clickable to intercept clicks from passing under the container
            .clickable(
                indication = null,
                interactionSource = mutableInteraction,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(32.dp)
                .width(46.dp)
                .background(
                    color = selectedColor,
                    shape = RoundedCornerShape(percent = 100)
                )
                .clip(RoundedCornerShape(100))
                .clickable { if (!isSelected) onClick(navItem.route) },
        )
        Icon(
            modifier = Modifier
                .size(22.dp),
            imageVector = navItem.icon,
            contentDescription = navItem.name,
            tint = selectedIconColor
        )
    }
}