package it.fast4x.rigallery.feature_node.presentation.analysis

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.fast4x.rigallery.feature_node.presentation.common.components.ScannerButton
import it.fast4x.rigallery.feature_node.presentation.common.components.TwoLinedDateToolbarTitle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    navigateUp: () -> Unit,
    navigate: (String) -> Unit,
) {
    val viewModel = hiltViewModel<AnalysisViewModel>()
    val mediaNotAnalyzedCount by viewModel.notAnalyzedMediaCount.collectAsStateWithLifecycle()
    val analyzedMediaCount by viewModel.analyzedMediaCount.collectAsStateWithLifecycle()

    var canScroll by rememberSaveable { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { canScroll }
    )

    val isRunning by viewModel.isRunning.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()

    Box {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        TwoLinedDateToolbarTitle(
                            albumName = "Analysis",//stringResource(R.string.categories),
                            dateHeader = "Analyzed media: $analyzedMediaCount" //stringResource(R.string.classified_media, classifiedCount)
                        )
                    },
                    navigationIcon = {
//                        IconButton(onClick = navigateUp) {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                contentDescription = stringResource(R.string.back_cd)
//                            )
//                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddings ->

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(paddings),
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


                if (analyzedMediaCount > 0 && !isRunning) {
                    ScannerButton(
                        image = Icons.Outlined.Close,
                        scanForNewText = "Reset Analyzed media",
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
                }

            }

        }
    }
}
