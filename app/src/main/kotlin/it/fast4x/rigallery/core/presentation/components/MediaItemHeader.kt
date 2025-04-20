/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.crop
import it.fast4x.rigallery.core.Constants.Animation.enterAnimation
import it.fast4x.rigallery.core.Constants.Animation.exitAnimation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaItemHeader(
    modifier: Modifier = Modifier,
    date: String,
    showAsBig: Boolean = false,
    isCheckVisible: MutableState<Boolean>,
    isChecked: MutableState<Boolean>,
    onChecked: (() -> Unit)? = null
) {
    val smallModifier = modifier
        .padding(
            horizontal = 0.dp,
            vertical = 5.dp
        )
        .fillMaxWidth()
    val bigModifier = modifier
        .padding(horizontal = 0.dp)
        .padding(top = 80.dp)
//    val bigTextStyle = MaterialTheme.typography.headlineLarge.copy(
//        fontWeight = FontWeight.Bold
//    )
    val bigTextStyle = MaterialTheme.typography.headlineLarge
    //val smallTextStyle = MaterialTheme.typography.titleMedium
    val smallTextStyle = MaterialTheme.typography.titleLarge
//    val smallTextStyle = MaterialTheme.typography.titleLarge.copy(
//        fontWeight = FontWeight.Bold
//    )
    Row(
        modifier = (if (showAsBig) bigModifier else smallModifier)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                MaterialTheme.shapes.medium)
            .padding(start = 10.dp)
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = remember { date },
            style = if (showAsBig) bigTextStyle else smallTextStyle,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.then(
                if (!showAsBig) Modifier.combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onLongClick = {
                        onChecked?.invoke()
                    },
                    onClick = {
                        if (isCheckVisible.value) onChecked?.invoke()
                    }
                ) else Modifier
            )
        )
        if (!showAsBig && onChecked != null) {
            AnimatedVisibility(
                visible = isCheckVisible.value,
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                CheckBox(
                    isChecked = isChecked.value,
                    onCheck = onChecked,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}