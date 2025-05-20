/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.feature_node.presentation.main

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import it.fast4x.rigallery.core.Settings.Misc.getSecureMode
import it.fast4x.rigallery.core.Settings.Misc.rememberForceTheme
import it.fast4x.rigallery.core.Settings.Misc.rememberIsDarkMode
import it.fast4x.rigallery.core.presentation.components.AppBarContainer
import it.fast4x.rigallery.core.presentation.components.NavigationComp
import it.fast4x.rigallery.feature_node.domain.repository.MediaRepository
import it.fast4x.rigallery.feature_node.presentation.util.toggleOrientation
import it.fast4x.rigallery.ui.theme.GalleryTheme
import dagger.hilt.android.AndroidEntryPoint
import it.fast4x.rigallery.core.extensions.checkupdate.CheckAvailableNewVersion
import it.fast4x.rigallery.core.util.ext.OkHttpRequest
import it.fast4x.rigallery.feature_node.data.data_source.InternalDatabase
import it.fast4x.rigallery.feature_node.presentation.analysis.AnalysisViewModel
import it.fast4x.rigallery.feature_node.presentation.common.MediaViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

//    @Inject
//    lateinit var repository: MediaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enforceSecureFlag()
        enableEdgeToEdge()
        setContent {

            val analyzerViewModel = hiltViewModel<AnalysisViewModel>()
            analyzerViewModel.startAnalysis()

            val mediaViewModel = hiltViewModel<MediaViewModel>()
            val langCode = mediaViewModel.languageApp.collectAsState()

            val systemLangCode =
                AppCompatDelegate.getApplicationLocales().get(0).toString()

            val sysLocale: LocaleListCompat =
                LocaleListCompat.forLanguageTags(systemLangCode)
            val appLocale: LocaleListCompat =
                LocaleListCompat.forLanguageTags(langCode.value)
            AppCompatDelegate.setApplicationLocales(if (langCode.value == "") sysLocale else appLocale)


            var showNewversionDialog by rememberSaveable {
                mutableStateOf(true)
            }
            val checkUpdate = mediaViewModel.checkUpdate.collectAsState()
            if (checkUpdate.value) {
                var request = OkHttpRequest(OkHttpClient())
                val urlVersionCode =
                    "https://raw.githubusercontent.com/fast4x/RiGallery/main/updatedVersion/updatedVersionCode.ver"
                request.GET(urlVersionCode, object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val responseData = response.body?.string()
                        runOnUiThread {
                            try {
                                if (responseData != null) {
                                    val file = File(filesDir, "UpdatedVersionCode.ver")
                                    file.writeText(responseData.toString())
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }

                    @OptIn(UnstableApi::class)
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("UpdatedVersionCode", "Check failure")
                    }
                })


                if (showNewversionDialog)
                    CheckAvailableNewVersion(
                        onDismiss = { showNewversionDialog = false },
                        updateAvailable = {}
                    )
            }

            GalleryTheme {
                val navController = rememberNavController()
                val isScrolling = remember { mutableStateOf(false) }
                val bottomBarState = rememberSaveable { mutableStateOf(true) }
                val systemBarFollowThemeState = rememberSaveable { mutableStateOf(true) }
                val forcedTheme by rememberForceTheme()
                val localDarkTheme by rememberIsDarkMode()
                val systemDarkTheme = isSystemInDarkTheme()
                val darkTheme by remember(forcedTheme, localDarkTheme, systemDarkTheme) {
                    mutableStateOf(if (forcedTheme) localDarkTheme else systemDarkTheme)
                }
                LaunchedEffect(darkTheme, systemBarFollowThemeState.value) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                        ) { darkTheme || !systemBarFollowThemeState.value },
                        navigationBarStyle = SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                        ) { darkTheme || !systemBarFollowThemeState.value }
                    )
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        AppBarContainer(
                            navController = navController,
                            paddingValues = paddingValues,
                            bottomBarState = bottomBarState.value,
                            isScrolling = isScrolling.value
                        ) {
                            NavigationComp(
                                navController = navController,
                                paddingValues = paddingValues,
                                bottomBarState = bottomBarState,
                                systemBarFollowThemeState = systemBarFollowThemeState,
                                toggleRotate = ::toggleOrientation,
                                isScrolling = isScrolling
                            )
                        }
                    }
                )
            }
        }
    }

    private fun enforceSecureFlag() {
        lifecycleScope.launch {
            getSecureMode(this@MainActivity).collectLatest { enabled ->
                if (enabled) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }
        }
    }

    companion object {
        const val OPEN_FROM_ANALYZER = 2
    }

}