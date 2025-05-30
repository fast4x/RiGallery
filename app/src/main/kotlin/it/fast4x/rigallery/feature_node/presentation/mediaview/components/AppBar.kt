/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.feature_node.presentation.mediaview.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.core.Constants
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.enums.TransitionEffect
import it.fast4x.rigallery.feature_node.domain.model.LocationData
import it.fast4x.rigallery.feature_node.domain.model.rememberLocationData
import it.fast4x.rigallery.feature_node.presentation.util.rememberExifInterface
import it.fast4x.rigallery.feature_node.presentation.util.rememberExifMetadata
import it.fast4x.rigallery.ui.theme.BlackScrim

@Composable
fun MediaViewAppBar(
    modifier: Modifier = Modifier,
    showUI: Boolean,
    showInfo: Boolean,
    showDate: Boolean,
    currentDate: String,
    paddingValues: PaddingValues,
    onGoBack: () -> Unit,
    onShowInfo: () -> Unit,
    locationData: LocationData? = null
) {
    val transitionEffect by Settings.Misc.rememberTransitionEffect()
    AnimatedVisibility(
        visible = showUI,
        enter =  TransitionEffect.enter(
            TransitionEffect.entries[transitionEffect]
        ),
        exit =  TransitionEffect.exit(
            TransitionEffect.entries[transitionEffect]
        )
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BlackScrim, Color.Transparent)
                    )
                )
                .padding(top = paddingValues.calculateTopPadding())
                .padding(start = 5.dp, end = if (showInfo) 8.dp else 16.dp)
                .padding(vertical = 8.dp)
                .then(modifier)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onGoBack) {
                Image(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = "Go back",
                    modifier = Modifier
                        .height(48.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = showDate,
                    enter =  TransitionEffect.enter(
                        TransitionEffect.entries[transitionEffect]
                    ),
                    exit =  TransitionEffect.exit(
                        TransitionEffect.entries[transitionEffect]
                    )
                ) {
                    Text(
                        text = currentDate.uppercase(),
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        textAlign = TextAlign.End
                    )
                }
                
                AnimatedVisibility(
                    visible = locationData?.location?.isEmpty() == false,
                    enter =  TransitionEffect.enter(
                        TransitionEffect.entries[transitionEffect]
                    ),
                    exit =  TransitionEffect.exit(
                        TransitionEffect.entries[transitionEffect]
                    )
                ) {
                    IconButton(
                        onClick = onShowInfo
                    ) {
                        Image(
                            imageVector = Icons.Outlined.LocationOn,
                            colorFilter = ColorFilter.tint(Color.White),
                            contentDescription = "info",
                            modifier = Modifier
                                .height(48.dp)
                        )
                    }
                }
            }
        }
    }
}