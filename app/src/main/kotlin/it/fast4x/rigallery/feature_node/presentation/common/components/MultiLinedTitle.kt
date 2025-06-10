package it.fast4x.rigallery.feature_node.presentation.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun MultiLinedTitle(
    modifier: Modifier = Modifier,
    title: String,
    subtitle1: String = "",
    subtitle2: String = "",
    subtitle3: String = "",
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        if (subtitle1.isNotEmpty()) {
            Text(
                modifier = Modifier,
                text = subtitle1,
                style = MaterialTheme.typography.labelSmall,
                //fontFamily = FontFamily.Monospace,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        if (subtitle2.isNotEmpty()) {
            Text(
                modifier = Modifier,
                text = subtitle2,
                style = MaterialTheme.typography.labelSmall,
                //fontFamily = FontFamily.Monospace,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        if (subtitle3.isNotEmpty()) {
            Text(
                modifier = Modifier,
                text = subtitle3,
                style = MaterialTheme.typography.labelSmall,
                //fontFamily = FontFamily.Monospace,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

