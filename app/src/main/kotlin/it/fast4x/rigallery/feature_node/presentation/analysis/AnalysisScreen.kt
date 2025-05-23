package it.fast4x.rigallery.feature_node.presentation.analysis

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.fast4x.rigallery.R
import it.fast4x.rigallery.feature_node.data.data_source.InternalDatabase
import it.fast4x.rigallery.feature_node.presentation.common.MediaViewModel
import it.fast4x.rigallery.feature_node.presentation.common.components.ScannerButton
import it.fast4x.rigallery.feature_node.presentation.common.components.TwoLinedDateToolbarTitle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    navigateUp: () -> Unit,
    navigate: (String) -> Unit,
) {




    val viewModelMedia = hiltViewModel<MediaViewModel>()
    val mediaWithLocation by viewModelMedia.mediaWithLocation.collectAsStateWithLifecycle()
    val mediaWithDominantColor by viewModelMedia.mediaWithDominantColor.collectAsStateWithLifecycle()

    val viewModel = hiltViewModel<AnalysisViewModel>()
    val mediaInDb by viewModel.mediaInDb.collectAsStateWithLifecycle()
    val isRunningLoc by viewModel.isRunningLoc.collectAsStateWithLifecycle()
    val progressLoc by viewModel.progressLoc.collectAsStateWithLifecycle()
    val isRunningDomCol by viewModel.isRunningDomCol.collectAsStateWithLifecycle()
    val progressDomCol by viewModel.progressDomCol.collectAsStateWithLifecycle()
    val isRunning = isRunningLoc || isRunningDomCol
    val progress = if (isRunningLoc) progressLoc else progressDomCol


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
                            albumName = stringResource(R.string.analysis_of_media),
                            dateHeader = stringResource(R.string.media_in_database, mediaInDb)
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
        ) { paddings ->

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(paddings)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ScannerButton(
                    scanForNewText = "Analyze media",
                    isRunning = isRunning,
                    indicatorCounter = progress,
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = {
                                if (isRunning) viewModel.stopAnalysis()
                            },
                            onClick = {
                                if (!isRunning) viewModel.startAnalysis()
                                else viewModel.stopAnalysis()
                            }
                        )
                )


                if (!isRunning)
                    ScannerButton(
                        image = Icons.Outlined.Close,
                        scanForNewText = "Reset analyzed media",
//                        scanForNewText = "Reset analyzed media (${mediaWithLocation.size?.plus(
//                            mediaWithDominantColor?.size ?: 0
//                        )})",
                        isRunning = isRunning,
                        indicatorCounter = progress,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.resetAnalysis()
                                }
                            )
                    )


                if (!isRunning)
                    ScannerButton(
                        image = Icons.Outlined.Close,
                        scanForNewText = "Reset Location Analysis",
                        //scanForNewText = "Reset Location Analysis (${mediaWithLocation?.size})",
                        isRunning = isRunning,
                        indicatorCounter = progress,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.resetAnalysisForLocation()
                                }
                            )
                    )

                if (!isRunning)
                    ScannerButton(
                        image = Icons.Outlined.Close,
                        scanForNewText = "Reset Dominant Color Analysis",
                        //scanForNewText = "Reset Dominant Color Analysis (${mediaWithDominantColor?.size})",
                        isRunning = isRunning,
                        indicatorCounter = progress,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.resetAnalysisForDominantColor()
                                }
                            )
                    )

            }

        }
    }
}
