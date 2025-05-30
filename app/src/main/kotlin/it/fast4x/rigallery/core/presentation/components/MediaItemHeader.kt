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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.crop
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.core.enums.TransitionEffect
import it.fast4x.rigallery.core.util.titlecaseFirstCharIfItIsLowercase
import java.util.Locale

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

    val stringToday = stringResource(id = R.string.header_today)
    val stringYesterday = stringResource(id = R.string.header_yesterday)
    val stringJanuary = stringResource(id = R.string.tag_january)
    val stringFebruary = stringResource(id = R.string.tag_february)
    val stringMarch = stringResource(id = R.string.tag_march)
    val stringApril = stringResource(id = R.string.tag_april)
    val stringMay = stringResource(id = R.string.tag_may)
    val stringJune = stringResource(id = R.string.tag_june)
    val stringJuly = stringResource(id = R.string.tag_july)
    val stringAugust = stringResource(id = R.string.tag_august)
    val stringSeptember = stringResource(id = R.string.tag_september)
    val stringOctober = stringResource(id = R.string.tag_october)
    val stringNovember = stringResource(id = R.string.tag_november)
    val stringDecember = stringResource(id = R.string.tag_december)

    val translatedDate = remember {
        date.replace("Today", stringToday)
            .replace("Today", stringToday)
            .replace("Yesterday", stringYesterday)
            .replace("January", stringJanuary)
            .replace("February", stringFebruary)
            .replace("March", stringMarch)
            .replace("April", stringApril)
            .replace("May", stringMay)
            .replace("June", stringJune)
            .replace("July", stringJuly)
            .replace("August", stringAugust)
            .replace("September", stringSeptember)
            .replace("October", stringOctober)
            .replace("November", stringNovember)
            .replace("December", stringDecember)
            .titlecaseFirstCharIfItIsLowercase()
    }

    val transitionEffect by Settings.Misc.rememberTransitionEffect()

    Row(
        modifier = (if (showAsBig) bigModifier else smallModifier)
//            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
//                MaterialTheme.shapes.medium)
            .padding(start = 10.dp)
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = remember { translatedDate },
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
                enter =  TransitionEffect.enter(
                    TransitionEffect.entries[transitionEffect]
                ),
                exit =  TransitionEffect.exit(
                    TransitionEffect.entries[transitionEffect]
                )
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