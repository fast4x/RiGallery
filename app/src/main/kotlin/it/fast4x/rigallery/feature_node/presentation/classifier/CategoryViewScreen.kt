package it.fast4x.rigallery.feature_node.presentation.classifier

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.fast4x.rigallery.R
import it.fast4x.rigallery.feature_node.presentation.common.MediaScreen
import it.fast4x.rigallery.feature_node.presentation.util.Screen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CategoryViewScreen(
    navigateUp: () -> Unit,
    navigate: (String) -> Unit,
    toggleNavbar: (Boolean) -> Unit,
    category: String,
    showSearch: MutableState<Boolean>
) {
    val viewModel = hiltViewModel<CategoryViewModel>().apply {
        this.category = category
    }

    LaunchedEffect(category) {
        if (category.isEmpty()) {
            navigateUp()
        }
    }

    val mediaState = viewModel.mediaByCategory.collectAsStateWithLifecycle()

    MediaScreen(
        target = "category_$category",
        albumName = category,
        handler = viewModel.handler,
        mediaState = mediaState,
        selectionState = viewModel.selectionState,
        selectedMedia = viewModel.selectedMedia,
        toggleSelection = {
            viewModel.toggleSelection(mediaState.value, it)
        },
        customDateHeader = stringResource(R.string.s_items,  mediaState.value.media.size),
        customViewingNavigation = { media ->
            navigate(Screen.MediaViewScreen.idAndCategory(media.id, category))
        },
        navActionsContent = { expandedDropDown, result ->
        },
        navigate = navigate,
        navigateUp = navigateUp,
        toggleNavbar = toggleNavbar,
        onActivityResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.selectedMedia.clear()
                viewModel.selectionState.value = false
            }
        },
        showSearch = showSearch,
    )
}