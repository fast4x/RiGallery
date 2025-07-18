package it.fast4x.rigallery.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ProcessLifecycleInitializer
import androidx.navigation.compose.rememberNavController
import androidx.startup.Initializer
import androidx.work.WorkManagerInitializer
import it.fast4x.rigallery.core.presentation.components.AppBarContainer
import it.fast4x.rigallery.core.presentation.components.NavigationComp
import it.fast4x.rigallery.ui.theme.GalleryTheme

class ComposeInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        ComposeView(context).setContent {
            GalleryTheme {
                val navController = rememberNavController()
                val isScrolling = remember { mutableStateOf(false) }
                val showSearchBar = remember { mutableStateOf(false) }
                val bottomBarState = rememberSaveable { mutableStateOf(true) }
                val systemBarFollowThemeState = rememberSaveable { mutableStateOf(true) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        AppBarContainer(
                            navController = navController,
                            paddingValues = paddingValues,
                            bottomBarState = bottomBarState.value,
                            onShowSearchBar = { showSearchBar.value = it },
                            isScrolling = isScrolling.value
                        ) {
                            NavigationComp(
                                navController = navController,
                                paddingValues = paddingValues,
                                bottomBarState = bottomBarState,
                                systemBarFollowThemeState = systemBarFollowThemeState,
                                toggleRotate = { },
                                isScrolling = isScrolling,
                                showSearchBar = showSearchBar
                            )
                        }
                    }
                )
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ProcessLifecycleInitializer::class.java, WorkManagerInitializer::class.java)
    }
}