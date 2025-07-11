/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.feature_node.presentation.mediaview.components.media

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.core.Constants.DEFAULT_TOP_BAR_ANIMATION_DURATION
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.decoder.EncryptedRegionDecoder
import it.fast4x.rigallery.core.presentation.components.util.LocalBatteryStatus
import it.fast4x.rigallery.core.presentation.components.util.ProvideBatteryStatus
import it.fast4x.rigallery.core.presentation.components.util.swipe
import it.fast4x.rigallery.feature_node.data.data_source.KeychainHolder
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.util.asSubsamplingImage
import it.fast4x.rigallery.feature_node.domain.util.getUri
import it.fast4x.rigallery.feature_node.domain.util.isEncrypted
import it.fast4x.rigallery.feature_node.presentation.util.rememberFeedbackManager
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.rememberAsyncImagePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.util.Size
import com.github.panpf.zoomimage.SketchZoomAsyncImage
import com.github.panpf.zoomimage.ZoomImage
import com.github.panpf.zoomimage.rememberSketchZoomState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Stable
@Composable
fun <T: Media> ZoomablePagerImage(
    modifier: Modifier = Modifier,
    media: T,
    uiEnabled: Boolean,
    onItemClick: () -> Unit,
    onSwipeDown: () -> Unit,
    onSwipeUp: () -> Unit = {},
    onLongPress: () -> Unit = {},
) {
    val feedbackManager = rememberFeedbackManager()
    var isRotating by rememberSaveable { mutableStateOf(false) }
    var currentRotation by rememberSaveable { mutableIntStateOf(0) }
    val rotationAnimation by animateFloatAsState(
        targetValue = if (isRotating) 90f else 0f,
        label = "rotationAnimation"
    )
    ProvideBatteryStatus {
        val allowBlur by Settings.Misc.rememberAllowBlur()
        val isPowerSavingMode = LocalBatteryStatus.current.isPowerSavingMode
        AnimatedVisibility(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && allowBlur && !isPowerSavingMode) {
            val blurAlpha by animateFloatAsState(
                animationSpec = tween(DEFAULT_TOP_BAR_ANIMATION_DURATION),
                targetValue = if (uiEnabled) 0.6f else 0f,
                label = "blurAlpha"
            )
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(blurAlpha)
                    .blur(90.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
                request = ComposableImageRequest(media.getUri().toString()) {
                    crossfade()
                    resizeOnDraw()
                    size(Size.parseSize("600x600"))
                    setExtra(
                        key = "mediaKey",
                        value = media.toString(),
                    )
                    setExtra("realMimeType", media.mimeType)
                },
                contentDescription = null,
                filterQuality = FilterQuality.None,
                contentScale = ContentScale.Crop
            )
        }
    }
    val zoomState = rememberSketchZoomState()
    val scope = rememberCoroutineScope()

    if (media.isEncrypted) {
        val painter = rememberAsyncImagePainter(
            request = ComposableImageRequest(media.getUri().toString()) {
                crossfade()
                resizeOnDraw()
                setExtra(
                    key = "mediaKey",
                    value = media.toString(),
                )
                setExtra("realMimeType", media.mimeType)
            },
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.None,
        )
        val context = LocalContext.current
        val keychainHolder = remember {
            KeychainHolder(context)
        }
        LaunchedEffect(zoomState.subsampling) {
            zoomState.subsampling.regionDecoders = listOf(EncryptedRegionDecoder.Factory(keychainHolder))
            zoomState.setSubsamplingImage(media.asSubsamplingImage(context))
        }
        ZoomImage(
            zoomState = zoomState,
            painter = painter,
            modifier = Modifier
                .fillMaxSize()
                .swipe(
                    onSwipeDown = onSwipeDown,
                    onSwipeUp = onSwipeUp
                )
                .graphicsLayer {
                    rotationZ = if (isRotating) rotationAnimation else 0f
                }.then(modifier),
            onTap = { onItemClick() },
            onLongPress = {
                scope.launch {
                    isRotating = true
                    feedbackManager.vibrate()
                    currentRotation += 90
                    delay(350)
                    zoomState.zoomable.rotate(currentRotation)
                    isRotating = false
                }
                onLongPress()
            },
            alignment = Alignment.Center,
            contentDescription = media.label
        )
    } else {
        val asyncState = rememberAsyncImageState(
            options = ImageOptions {
                setExtra(
                    key = "mediaKey",
                    value = media.toString(),
                )
                setExtra("realMimeType", media.mimeType)
            }
        )

        SketchZoomAsyncImage(
            zoomState = zoomState,
            state = asyncState,
            modifier = Modifier
                .fillMaxSize()
                .swipe(
                    onSwipeDown = onSwipeDown,
                    onSwipeUp = onSwipeUp
                )
                .graphicsLayer {
                    rotationZ = if (isRotating) rotationAnimation else 0f
                }
                .then(modifier),
            onTap = { onItemClick() },
            onLongPress = {
                scope.launch {
                    isRotating = true
                    feedbackManager.vibrate()
                    currentRotation += 90
                    delay(350)
                    zoomState.zoomable.rotate(currentRotation)
                    isRotating = false
                }
            },
            alignment = Alignment.Center,
            uri = media.getUri().toString(),
            contentDescription = media.label
        )
    }
}


