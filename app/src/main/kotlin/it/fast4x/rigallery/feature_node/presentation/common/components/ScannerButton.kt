package it.fast4x.rigallery.feature_node.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Scanner
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Constants.Animation.enterAnimation
import it.fast4x.rigallery.core.Constants.Animation.exitAnimation
import java.util.Locale

@Composable
fun ScannerButton(
    image: ImageVector = Icons.Outlined.Scanner,
    scanningMediaText: String = stringResource(R.string.scanning_media),
    scanForNewText: String = stringResource(R.string.scan_for_new_categories),
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    indicatorCounter: Float = 0f,
    isRunning: Boolean = false
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = contentColor.copy(alpha = 0.1f),
            headlineColor = contentColor
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .then(modifier),
        headlineContent = {
            val text = remember(isRunning) {
                if (isRunning) scanningMediaText else scanForNewText
            }
            Text(
                modifier = Modifier
                    .then(if (isRunning) Modifier.padding(top = 8.dp) else Modifier),
                text = text,
                style = MaterialTheme.typography.labelLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        },
        leadingContent = {
            Icon(
                imageVector = image,
                tint = contentColor,
                contentDescription = "" //stringResource(R.string.scan_for_new_categories)
            )
        },
        trailingContent = {
            AnimatedVisibility(
                visible = isRunning,
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                Text(
                    text = remember(indicatorCounter) {
                        String.format(
                            Locale.getDefault(),
                            "%.1f",
                            indicatorCounter.coerceIn(0f..100f)
                        ) + "%"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        },
        supportingContent = if (isRunning) {
            {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    AnimatedVisibility(
                        visible = indicatorCounter < 100f
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            progress = { (indicatorCounter / 100f).coerceAtLeast(0f) },
                            color = contentColor,
                        )
                    }

                    AnimatedVisibility(
                        visible = indicatorCounter == 100f
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = contentColor,
                        )
                    }
                }
            }
        } else null
    )
}