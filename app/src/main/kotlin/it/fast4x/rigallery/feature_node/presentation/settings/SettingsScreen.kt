/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.feature_node.presentation.settings

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Position
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.Settings.Misc.rememberAudioFocus
import it.fast4x.rigallery.core.Settings.Misc.rememberAutoHideNavBar
import it.fast4x.rigallery.core.Settings.Misc.rememberAutoHideOnVideoPlay
import it.fast4x.rigallery.core.Settings.Misc.rememberAutoHideSearchBar
import it.fast4x.rigallery.core.Settings.Misc.rememberForcedLastScreen
import it.fast4x.rigallery.core.Settings.Misc.rememberFullBrightnessView
import it.fast4x.rigallery.core.Settings.Misc.rememberLastScreen
import it.fast4x.rigallery.core.Settings.Misc.rememberVideoAutoplay
import it.fast4x.rigallery.core.SettingsEntity
import it.fast4x.rigallery.core.enums.Languages
import it.fast4x.rigallery.core.enums.MediaType
import it.fast4x.rigallery.core.enums.Option
import it.fast4x.rigallery.core.enums.TransitionEffect
import it.fast4x.rigallery.core.presentation.components.OptionSheetMenu
import it.fast4x.rigallery.feature_node.presentation.settings.components.SettingsAppHeader
import it.fast4x.rigallery.feature_node.presentation.settings.components.SettingsItem
import it.fast4x.rigallery.feature_node.presentation.util.Screen
import it.fast4x.rigallery.feature_node.presentation.util.restartApplication
import it.fast4x.rigallery.ui.theme.Shapes
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateUp: () -> Unit,
    navigate: (String) -> Unit
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val showLaunchScreenDialog = rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val settingsList = rememberSettingsList(navigate, showLaunchScreenDialog)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_cd)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = padding
        ) {
            item { SettingsAppHeader() }
            items(
                items = settingsList,
                key = { it.title + it.type.toString() }
            ) { SettingsItem(it) }
        }

        if (showLaunchScreenDialog.value) {
            ModalBottomSheet(
                onDismissRequest = { showLaunchScreenDialog.value = false },
                contentWindowInsets = {
                    WindowInsets(bottom = WindowInsets.systemBars.getBottom(LocalDensity.current))
                }
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = Shapes.extraLarge
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompositionLocalProvider(
                        value = LocalTextStyle.provides(
                            TextStyle.Default.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    ) {
                        val scope = rememberCoroutineScope()
                        Text(
                            text = stringResource(R.string.set_default_launch_screen),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )

                        var lastScreen by rememberLastScreen()
                        var forcedLastScreen by rememberForcedLastScreen()
                        val lastOpenScreenString = stringResource(R.string.use_last_opened_screen)
                        val timelineOpenScreenString = stringResource(R.string.launch_on_timeline)
                        val albumsOpenScreenString = stringResource(R.string.launch_on_albums)
                        val libraryOpenScreenString = stringResource(R.string.launch_on_library)

                        val openItems = remember(lastScreen, forcedLastScreen) {
                            listOf(
                                Triple(lastOpenScreenString, !forcedLastScreen) {
                                    forcedLastScreen = false
                                    lastScreen = Screen.TimelineScreen()
                                },
                                Triple(
                                    timelineOpenScreenString,
                                    forcedLastScreen && lastScreen == Screen.TimelineScreen()
                                ) {
                                    forcedLastScreen = true
                                    lastScreen = Screen.TimelineScreen()
                                },
                                Triple(
                                    albumsOpenScreenString,
                                    forcedLastScreen && lastScreen == Screen.AlbumsScreen()
                                ) {
                                    forcedLastScreen = true
                                    lastScreen = Screen.AlbumsScreen()
                                },
                                Triple(
                                    libraryOpenScreenString,
                                    forcedLastScreen && lastScreen == Screen.LibraryScreen()
                                ) {
                                    forcedLastScreen = true
                                    lastScreen = Screen.LibraryScreen()
                                }
                            )
                        }

                        LazyColumn {
                            items(
                                items = openItems,
                                key = { it.first }
                            ) { (title, enabled, onClick) ->
                                ListItem(
                                    modifier = Modifier
                                        .clip(Shapes.large)
                                        .clickable(onClick = onClick),
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                    headlineContent = {
                                        Text(text = title)
                                    },
                                    trailingContent = {
                                        RadioButton(
                                            selected = enabled,
                                            onClick = onClick
                                        )
                                    }
                                )
                            }
                        }
                        Button(onClick = {
                            scope.launch {
                                sheetState.hide()
                                showLaunchScreenDialog.value = false
                            }
                        }) {
                            Text(
                                text = stringResource(R.string.done),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSettingsList(
    navigate: (String) -> Unit,
    showLaunchScreenDialog: MutableState<Boolean>
): SnapshotStateList<SettingsEntity> {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showLaunguageAppMenu by remember { mutableStateOf(false) }
    var launguageApp by Settings.Misc.rememberLanguageApp()
    val languageAppPref = remember(launguageApp) {
        SettingsEntity.Preference(
            title = "Language", //context.getString(R.string.show_media_type),
            summary = Languages.languageFromcode(launguageApp).toString(),
            onClick = { showLaunguageAppMenu = true },
            screenPosition = Position.Alone
        )
    }
    OptionSheetMenu(
        title = "Language",
        options = Languages.entries.map{ option ->
            Option(
                ordinal = option.ordinal,
                name = option.name,
                title = option.name,
                icon = Icons.Outlined.Language
            )
        },
        visible = showLaunguageAppMenu,
        onSelected = { launguageApp = Languages.entries[it].code },
        onDismiss = { showLaunguageAppMenu = false }
    )


    var checkUpdate by Settings.Misc.rememberCheckUpdate()
    val checkUpdateValuePref = remember(checkUpdate) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.settings_check_update_title),
            isChecked = checkUpdate,
            onCheck = { checkUpdate = it },
            screenPosition = Position.Bottom
        )
    }


    var forceTheme by Settings.Misc.rememberForceTheme()
    val forceThemeValuePref = remember(forceTheme) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.settings_follow_system_theme_title),
            isChecked = !forceTheme,
            onCheck = { forceTheme = !it },
            screenPosition = Position.Top
        )
    }

    // TODO Valuate if necessary media type
//    var showMediaTypeMenu by remember { mutableStateOf(false) }
//    var showMediaType by Settings.Misc.rememberShowMediaType()
//    val showMediaTypePref = remember(showMediaType) {
//        SettingsEntity.Preference(
//            title = "Media type", //context.getString(R.string.show_media_type),
//            summary = MediaType.entries[showMediaType].title,
//            onClick = { showMediaTypeMenu = true },
//            screenPosition = Position.Top
//        )
//    }
//    OptionSheetMenu(
//        title = "Media type",
//        options = MediaType.entries.map{ option ->
//            Option(
//                ordinal = option.ordinal,
//                name = option.name,
//                title = option.title,
//                icon = option.icon
//            )
//        },
//        visible = showMediaTypeMenu,
//        onSelected = { showMediaType = it },
//        onDismiss = { showMediaTypeMenu = false }
//    )

    var darkModeValue by Settings.Misc.rememberIsDarkMode()
    val darkThemePref = remember(darkModeValue, forceTheme) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.settings_dark_mode_title),
            enabled = forceTheme,
            isChecked = darkModeValue,
            onCheck = { darkModeValue = it },
            screenPosition = Position.Middle
        )
    }
    var amoledModeValue by Settings.Misc.rememberIsAmoledMode()
    val amoledModePref = remember(amoledModeValue) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.amoled_mode_title),
            summary = context.getString(R.string.amoled_mode_summary),
            isChecked = amoledModeValue,
            onCheck = { amoledModeValue = it },
            screenPosition = Position.Bottom
        )
    }
    var trashCanEnabled by Settings.Misc.rememberTrashEnabled()
    val trashCanEnabledPref = remember(trashCanEnabled) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.settings_trash_title),
            summary = context.getString(R.string.settings_trash_summary),
            isChecked = trashCanEnabled,
            onCheck = { trashCanEnabled = it },
            screenPosition = Position.Top
        )
    }
    var secureMode by Settings.Misc.rememberSecureMode()
    val secureModePref = remember(secureMode) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.secure_mode_title),
            summary = context.getString(R.string.secure_mode_summary),
            isChecked = secureMode,
            onCheck = { secureMode = it },
            screenPosition = Position.Middle
        )
    }

    var allowVibrations by Settings.Misc.rememberAllowVibrations()
    val allowVibrationsPref = remember(allowVibrations) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.allow_vibrations),
            summary = context.getString(R.string.allow_vibrations_summary),
            isChecked = allowVibrations,
            onCheck = { allowVibrations = it },
            screenPosition = Position.Middle
        )
    }

    var groupByMonth by Settings.Misc.rememberTimelineGroupByMonth()
    val groupByMonthPref = remember(groupByMonth) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.monthly_timeline_title),
            summary = context.getString(R.string.monthly_timeline_summary),
            isChecked = groupByMonth,
            onCheck = {
                scope.launch {
                    scope.async { groupByMonth = it }.await()
                    delay(50)
                    context.restartApplication()
                }
            },
            screenPosition = Position.Middle
        )
    }

    val shouldAllowBlur = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }
    var allowBlur by Settings.Misc.rememberAllowBlur()
    val allowBlurPref = remember(allowBlur) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.fancy_blur),
            summary = context.getString(R.string.fancy_blur_summary),
            isChecked = allowBlur,
            onCheck = { allowBlur = it },
            enabled = shouldAllowBlur,
            screenPosition = Position.Middle
        )
    }

    var useStaggeredGrid by Settings.Misc.rememberStaggeredGrid()
    val useStaggeredGridPref = remember(useStaggeredGrid) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.mosaic_grid),
            summary = context.getString(R.string.mosaic_grid_info),
            isChecked = useStaggeredGrid,
            onCheck = { useStaggeredGrid = it },
            screenPosition = Position.Middle
        )
    }

    // NOT REQUIRED
//    var showOldNavbar by Settings.Misc.rememberOldNavbar()
//    val showOldNavbarPref = remember(showOldNavbar) {
//        SettingsEntity.SwitchPreference(
//            title = context.getString(R.string.old_navbar),
//            summary = context.getString(R.string.old_navbar_summary),
//            isChecked = showOldNavbar,
//            onCheck = { showOldNavbar = it },
//            screenPosition = Position.Top
//        )
//    }

    var hideTimelineOnAlbum by Settings.Album.rememberHideTimelineOnAlbum()
    val hideTimelineOnAlbumPref = remember(hideTimelineOnAlbum) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.hide_timeline_for_albums),
            summary = context.getString(R.string.hide_timeline_for_album_summary),
            isChecked = hideTimelineOnAlbum,
            onCheck = { hideTimelineOnAlbum = it },
            screenPosition = Position.Middle
        )
    }

    val lastScreen by rememberLastScreen()
    val forcedLastScreen by rememberForcedLastScreen()
    val summary = remember(lastScreen, forcedLastScreen) {
        if (forcedLastScreen) {
            when (lastScreen) {
                Screen.TimelineScreen() -> context.getString(R.string.launch_on_timeline)
                Screen.AlbumsScreen() -> context.getString(R.string.launch_on_albums)
                else -> context.getString(R.string.launch_on_library)
            }
        } else {
            context.getString(R.string.launch_auto)
        }
    }
    val forcedLastScreenPref = remember(forcedLastScreen, lastScreen) {
        SettingsEntity.Preference(
            title = context.getString(R.string.set_default_screen),
            summary = summary,
            onClick = { showLaunchScreenDialog.value = true },
            screenPosition = Position.Middle
        )
    }

    var autoHideSearchSetting by rememberAutoHideSearchBar()

    val autoHideSearch = remember(autoHideSearchSetting) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.auto_hide_searchbar),
            summary = context.getString(R.string.auto_hide_searchbar_summary),
            isChecked = autoHideSearchSetting,
            onCheck = { autoHideSearchSetting = it },
            screenPosition = Position.Middle
        )
    }

    var autoHideNavigationSetting by rememberAutoHideNavBar()

    val autoHideNavigation = remember(autoHideNavigationSetting) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.auto_hide_navigationbar),
            summary = context.getString(R.string.auto_hide_navigationbar_summary),
            isChecked = autoHideNavigationSetting,
            onCheck = { autoHideNavigationSetting = it },
            screenPosition = Position.Bottom
        )
    }

    var audioFocus by rememberAudioFocus()
    val audioFocusPref = remember(audioFocus) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.take_audio_focus_title),
            summary = context.getString(R.string.take_audio_focus_summary),
            isChecked = audioFocus,
            onCheck = {
                scope.launch {
                    audioFocus = it
                    delay(50)
                    context.restartApplication()
                }
            },
            screenPosition = Position.Middle
        )
    }

    var fullBrightnessView by rememberFullBrightnessView()
    val fullBrightnessViewPref = remember(fullBrightnessView) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.full_brightness_view_title),
            summary = context.getString(R.string.full_brightness_view_summary),
            isChecked = fullBrightnessView,
            onCheck = { fullBrightnessView = it },
            screenPosition = Position.Middle
        )
    }

    var autoHideOnVideoPlay by rememberAutoHideOnVideoPlay()
    val autoHideOnVideoPlayPref = remember(autoHideOnVideoPlay) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.auto_hide_on_video_play),
            summary = context.getString(R.string.auto_hide_on_video_play_summary),
            isChecked = autoHideOnVideoPlay,
            onCheck = { autoHideOnVideoPlay = it },
            screenPosition = Position.Middle
        )
    }

    var autoPlayVideo by rememberVideoAutoplay()
    val autoPlayVideoPref = remember(autoPlayVideo) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.auto_play_video),
            summary = context.getString(R.string.auto_play_video_summary),
            isChecked = autoPlayVideo,
            onCheck = { autoPlayVideo = it },
            screenPosition = Position.Middle
        )
    }

    var noClassification by Settings.Misc.rememberNoClassification()
    val noClassificationPref = remember(noClassification) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.no_classification),
            summary = context.getString(R.string.no_classification_summary),
            isChecked = noClassification,
            onCheck = { noClassification = it },
            screenPosition = Position.Alone
        )
    }

    val dateHeaderPref = remember {
        SettingsEntity.Preference(
            title = context.getString(R.string.date_header),
            summary = context.getString(R.string.date_header_summary),
            onClick = { navigate(Screen.DateFormatScreen()) },
            screenPosition = Position.Middle
        )
    }

    var sharedElements by Settings.Misc.rememberSharedElements()

    val sharedElementsPref = remember(sharedElements) {
        SettingsEntity.SwitchPreference(
            title = context.getString(R.string.shared_elements),
            summary = context.getString(R.string.shared_elements_summary),
            isChecked = sharedElements,
            onCheck = { sharedElements = it },
            screenPosition = Position.Middle
        )
    }

    var showTransitionEffectMenu by remember { mutableStateOf(false) }
    var showTransitionEffect by Settings.Misc.rememberTransitionEffect()
    val showTransitionEffectPref = remember(showTransitionEffect) {
        SettingsEntity.Preference(
            title =  context.getString(R.string.transition_effect),
            summary = TransitionEffect.entries[showTransitionEffect].name,
            onClick = { showTransitionEffectMenu = true },
            screenPosition = Position.Bottom
        )
    }
    OptionSheetMenu(
        title = context.getString(R.string.transition_effect),
        options = TransitionEffect.entries.map{ option ->
            Option(
                ordinal = option.ordinal,
                name = option.name,
                title = option.name,
                icon = Icons.Outlined.Map //option.icon
            )
        },
        visible = showTransitionEffectMenu,
        onSelected = { showTransitionEffect = it },
        onDismiss = { showTransitionEffectMenu = false }
    )


    return remember(
        arrayOf(
            forceTheme,
            darkModeValue,
            trashCanEnabled,
            groupByMonth,
            amoledModeValue,
            secureMode
        )
    ) {
        mutableStateListOf<SettingsEntity>().apply {
            add(SettingsEntity.Header(title = "Language"))
            add(languageAppPref)
            add(SettingsEntity.Header(title = context.getString(R.string.settings_theme_header)))
            add(forceThemeValuePref)
            add(darkThemePref)
            add(amoledModePref)
            add(SettingsEntity.Header(title = context.getString(R.string.settings_general)))
            add(trashCanEnabledPref)
            add(secureModePref)
            add(allowVibrationsPref)
            add(checkUpdateValuePref)
            add(SettingsEntity.Header(title = context.getString(R.string.customization)))
            //add(showMediaTypePref)
            add(dateHeaderPref)
            add(groupByMonthPref)
            add(allowBlurPref)
            add(useStaggeredGridPref)
            add(hideTimelineOnAlbumPref)
            add(forcedLastScreenPref)
            add(audioFocusPref)
            add(fullBrightnessViewPref)
            add(autoHideOnVideoPlayPref)
            add(autoPlayVideoPref)
            add(sharedElementsPref)
            add(showTransitionEffectPref)
            add(SettingsEntity.Header(title = context.getString(R.string.navigation)))
            //add(showOldNavbarPref) // NOT REQUIRED
            add(autoHideSearch)
            add(autoHideNavigation)
            add(SettingsEntity.Header(title = context.getString(R.string.ai_category)))
            add(noClassificationPref)

        }
    }
}