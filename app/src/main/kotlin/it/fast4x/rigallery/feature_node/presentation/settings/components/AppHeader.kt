/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.feature_node.presentation.support.SupportSheet
import it.fast4x.rigallery.feature_node.presentation.util.rememberAppBottomSheetState
import it.fast4x.rigallery.ui.theme.GalleryTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsAppHeader() {

    val appVersion = remember { "v${_root_ide_package_.it.fast4x.rigallery.BuildConfig.VERSION_NAME} (${_root_ide_package_.it.fast4x.rigallery.BuildConfig.VERSION_CODE})" }

    val donateImage = painterResource(id = R.drawable.donation)
    val donateTitle = stringResource(R.string.donate)
    val donateContentDesc = stringResource(R.string.donate_button_cd)

    val githubImage = painterResource(id = R.drawable.ic_github)
    val githubTitle = stringResource(R.string.github)
    val githubContentDesc = stringResource(R.string.github_button_cd)
    val githubUrl = "https://github.com/fast4x/RiGallery"

    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val supportState = rememberAppBottomSheetState()

    SupportSheet(state = supportState)

    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(all = 24.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "RiGallery",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = appVersion,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.graphicsLayer {
                    translationX = 6.0f
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "by Fast4x",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        supportState.show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = .12f),
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = .12f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .weight(1f)
                    .semantics {
                        contentDescription = donateContentDesc
                    }
            ) {
                Icon(painter = donateImage, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = donateTitle)
            }
            Button(
                onClick = { uriHandler.openUri(githubUrl) },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.inverseSurface,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = .12f),
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = .2f),
                    disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = .12f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(52.dp)
                    .semantics {
                        contentDescription = githubContentDesc
                    }
            ) {
                Icon(painter = githubImage, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = githubTitle)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { uriHandler.openUri("https://github.com/fast4x/RiGallery/issues/new?assignees=&labels=bug&template=bug_report.yaml") },
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.inverseSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = .12f),
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = .3f),
                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = .12f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(text = stringResource(R.string.report_an_issue))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { uriHandler.openUri("https://github.com/fast4x/RiGallery/issues/new?assignees=&labels=feature_request&template=feature_request.yaml") },
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.inverseSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = .12f),
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = .3f),
                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = .12f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                //.weight(1f)
                .height(52.dp)
        ) {
            Text(text = stringResource(R.string.request_a_feature_or_suggest_an_idea))
        }

    }
}

@Preview
@Composable
fun Preview() {
    GalleryTheme {
        SettingsAppHeader()
    }
}