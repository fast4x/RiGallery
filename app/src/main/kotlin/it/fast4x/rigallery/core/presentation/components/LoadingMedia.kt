/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0 license
 */

package it.fast4x.rigallery.core.presentation.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.google.android.material.progressindicator.CircularProgressIndicator
import it.fast4x.rigallery.core.Constants.cellsList
import it.fast4x.rigallery.core.Settings.Misc.rememberGridSize
import it.fast4x.rigallery.ui.theme.Dimens
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingMedia(
    modifier: Modifier = Modifier,
    //shouldShimmer: Boolean = true,
    topContent: @Composable (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    hideProgressIndicator: Boolean = false,
) {
//    val gridSize by rememberGridSize()
//    val grid = remember(gridSize) { cellsList.size - gridSize }
//    val canShimmer = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU }
    Column(
        modifier = modifier
            .fillMaxSize(),
            //.then(if (shouldShimmer && canShimmer) Modifier.shimmer() else Modifier),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        if (topContent != null) {
            topContent()
        }

        if (!hideProgressIndicator)
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .fillMaxSize(),
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(Dimens.Photo()),
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }

//        Box(
//            modifier = Modifier
//                .padding(horizontal = 24.dp, vertical = 24.dp),
//        ) {
//            Spacer(
//                modifier = Modifier
//                    .height(24.dp)
//                    .fillMaxWidth(0.45f)
//                    .background(
//                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
//                        shape = RoundedCornerShape(100)
//                    )
//            )
//        }
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(1.dp),
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            repeat(grid) {
//                Spacer(
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f)
//                        .size(Dimens.Photo())
//                        .background(
//                            color = MaterialTheme.colorScheme.surfaceVariant
//                        )
//                )
//            }
//        }
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(1.dp)
//        ) {
//            repeat(grid / 2) {
//                Spacer(
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f)
//                        .size(Dimens.Photo())
//                        .background(
//                            color = MaterialTheme.colorScheme.surfaceVariant
//                        )
//                )
//            }
//            repeat(grid / 2) {
//                Spacer(
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f)
//                        .size(Dimens.Photo())
//                )
//            }
//        }
//        if (shouldShimmer) {
//            Box(
//                modifier = Modifier
//                    .padding(horizontal = 24.dp, vertical = 24.dp)
//            ) {
//                Spacer(
//                    modifier = Modifier
//                        .height(24.dp)
//                        .fillMaxWidth(0.45f)
//                        .background(
//                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
//                            shape = RoundedCornerShape(100)
//                        )
//                )
//            }
//            repeat(10) {
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(1.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    repeat(grid) {
//                        Spacer(
//                            modifier = Modifier
//                                .weight(1f)
//                                .aspectRatio(1f)
//                                .size(Dimens.Photo())
//                                .background(
//                                    color = MaterialTheme.colorScheme.surfaceVariant
//                                )
//                        )
//                    }
//                }
//            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(1.dp)
//            ) {
//                repeat(grid / 2) {
//                    Spacer(
//                        modifier = Modifier
//                            .weight(1f)
//                            .aspectRatio(1f)
//                            .size(Dimens.Photo())
//                            .background(
//                                color = MaterialTheme.colorScheme.surfaceVariant
//                            )
//                    )
//                }
//                repeat(grid / 2) {
//                    Spacer(
//                        modifier = Modifier
//                            .weight(1f)
//                            .aspectRatio(1f)
//                            .size(Dimens.Photo())
//                    )
//                }
//            }
//        }
        if (bottomContent != null) {
            bottomContent()
        }
    }
}