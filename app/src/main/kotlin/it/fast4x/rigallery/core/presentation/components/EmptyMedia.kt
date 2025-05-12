/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R

@Composable
fun EmptyMedia(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.no_media_title),
    hideProgressIndicator: Boolean = true
) = LoadingMedia(
    modifier = modifier,
    //shouldShimmer = false,
    bottomContent = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 72.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.ImageSearch,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(64.dp)
            )
            Text(
                text = stringResource(R.string.no_media_found),
                style = MaterialTheme.typography.titleMedium
            )
        }
//        Text(
//            modifier = Modifier.fillMaxWidth().padding(32.dp),
//            text = title,
//            style = MaterialTheme.typography.titleLarge,
//            textAlign = TextAlign.Center
//        )
    },
    hideProgressIndicator = hideProgressIndicator
)