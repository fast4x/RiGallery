/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.presentation.trashed.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.enums.TransitionEffect
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import it.fast4x.rigallery.feature_node.presentation.util.rememberAppBottomSheetState
import it.fast4x.rigallery.feature_node.presentation.util.rememberIsMediaManager
import kotlinx.coroutines.launch

@Composable
fun <T: Media> TrashedNavActions(
    handler: MediaHandleUseCase,
    mediaState: State<MediaState<T>>,
    selectedMedia: SnapshotStateList<T>,
    selectionState: MutableState<Boolean>,
) {
    val scope = rememberCoroutineScope()
    val isMediaManager = rememberIsMediaManager()
    val deleteSheetState = rememberAppBottomSheetState()
    val restoreSheetState = rememberAppBottomSheetState()
    val result = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                selectedMedia.clear()
                selectionState.value = false
                if (isMediaManager) {
                    scope.launch {
                        deleteSheetState.hide()
                        restoreSheetState.hide()
                    }
                }
            }
        }
    )
    val transitionEffect by Settings.Misc.rememberTransitionEffect()
    AnimatedVisibility(
        visible = mediaState.value.media.isNotEmpty(),
        enter =  TransitionEffect.enter(
            TransitionEffect.entries[transitionEffect]
        ),
        exit =  TransitionEffect.exit(
            TransitionEffect.entries[transitionEffect]
        )
    ) {
        Row {
            TextButton(
                onClick = {
                    scope.launch {
                        if (isMediaManager) {
                            restoreSheetState.show()
                        } else {
                            handler.trashMedia(
                                result,
                                selectedMedia.ifEmpty { mediaState.value.media },
                                false
                            )
                        }
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.trash_restore),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (selectionState.value) {
                TextButton(
                    onClick = {
                        scope.launch {
                            if (isMediaManager) {
                                deleteSheetState.show()
                            } else {
                                handler.deleteMedia(result, selectedMedia)
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.trash_delete),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                TextButton(
                    onClick = {
                        scope.launch {
                            if (isMediaManager) {
                                deleteSheetState.show()
                            } else {
                                handler.deleteMedia(result, mediaState.value.media)
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.trash_empty),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
    TrashDialog(
        appBottomSheetState = deleteSheetState,
        data = selectedMedia.ifEmpty { mediaState.value.media },
        action = TrashDialogAction.DELETE
    ) {
        handler.deleteMedia(result, it)
    }
    TrashDialog(
        appBottomSheetState = restoreSheetState,
        data = selectedMedia.ifEmpty { mediaState.value.media },
        action = TrashDialogAction.RESTORE
    ) {
        handler.trashMedia(result, it, false)
    }
}

