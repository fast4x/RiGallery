/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
  * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.feature_node.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.util.toRange
import it.fast4x.rigallery.core.Constants.Animation
import it.fast4x.rigallery.core.presentation.components.CheckBox
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.util.getUri
import it.fast4x.rigallery.feature_node.domain.util.isFavorite
import it.fast4x.rigallery.feature_node.domain.util.isVideo
import it.fast4x.rigallery.feature_node.presentation.mediaview.components.video.VideoDurationHeader
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.key
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Composable
fun <T: Media> MediaImage(
    modifier: Modifier = Modifier,
    media: T,
    selectionState: MutableState<Boolean>,
    selectedMedia: SnapshotStateList<T>,
    canClick: Boolean,
    staggered: Boolean = false,
    onItemClick: (T) -> Unit,
    onItemLongClick: (T) -> Unit,
) {
    var isSelected by remember { mutableStateOf(false) }
    LaunchedEffect(selectionState.value, selectedMedia.size) {
        withContext(Dispatchers.IO) {
            isSelected = if (!selectionState.value) false else {
                selectedMedia.find { it.id == media.id } != null
            }
        }
    }
    val selectedSize by animateDpAsState(
        if (isSelected) 12.dp else 0.dp, label = "selectedSize"
    )
    val scale by animateFloatAsState(
        if (isSelected) 0.5f else 1f, label = "scale"
    )
    val selectedShapeSize by animateDpAsState(
        if (isSelected) 16.dp else 0.dp, label = "selectedShapeSize"
    )
    val strokeSize by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.dp, label = "strokeSize"
    )
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val strokeColor by animateColorAsState(
        targetValue = if (isSelected) primaryContainerColor else Color.Transparent,
        label = "strokeColor"
    )

    val aspectRatio = rememberSaveable(media) {
        mutableFloatStateOf(
            if (staggered)
                Random.nextFloat() + 1f
            else 1f
         )
    }

    Box(
        modifier = Modifier
            .combinedClickable(
                enabled = canClick,
                onClick = {
                    onItemClick(media)
                    if (selectionState.value) {
                        isSelected = !isSelected
                    }
                },
                onLongClick = {
                    onItemLongClick(media)
                    if (selectionState.value) {
                        isSelected = !isSelected
                    }
                },
            )
            .aspectRatio(aspectRatio.floatValue)
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .aspectRatio(aspectRatio.floatValue)
                .padding(selectedSize)
                .clip(RoundedCornerShape(selectedShapeSize))
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(selectedShapeSize)
                )
                .border(
                    width = strokeSize,
                    shape = RoundedCornerShape(selectedShapeSize),
                    color = strokeColor
                )
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                request = ComposableImageRequest(media.getUri().toString()) {
                    key(media.id)
                    crossfade(100)
                    resizeOnDraw()
                    scale(Scale.FILL)
                    setExtra(
                        key = "mediaKey",
                        value = media.toString(),
                    )
                    setExtra(
                        key = "realMimeType",
                        value = media.mimeType,
                    )
                },
                contentDescription = media.label,
                contentScale = ContentScale.FillBounds,
            )
        }

        AnimatedVisibility(
            visible = remember(media) { media.isVideo },
            enter = Animation.enterAnimation,
            exit = Animation.exitAnimation,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            VideoDurationHeader(
                modifier = Modifier
                    .padding(selectedSize / 2)
                    .scale(scale),
                media = media
            )
        }

        AnimatedVisibility(
            visible = remember(media) {
                media.isFavorite
            },
            enter = Animation.enterAnimation,
            exit = Animation.exitAnimation,
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Image(
                modifier = Modifier
                    .padding(selectedSize / 2)
                    .scale(scale)
                    .padding(8.dp)
                    .size(16.dp),
                imageVector = Icons.Filled.Favorite,
                colorFilter = ColorFilter.tint(Color.Red),
                contentDescription = null
            )
        }

        AnimatedVisibility(
            visible = selectionState.value,
            enter = Animation.enterAnimation,
            exit = Animation.exitAnimation
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                CheckBox(isChecked = isSelected)
            }
        }
    }
}
