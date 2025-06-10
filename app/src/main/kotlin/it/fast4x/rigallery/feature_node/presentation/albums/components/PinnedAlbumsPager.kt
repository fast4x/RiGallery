package it.fast4x.rigallery.feature_node.presentation.albums.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AppsOutage
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.panpf.sketch.AsyncImage
import it.fast4x.rigallery.R
import it.fast4x.rigallery.feature_node.domain.model.Album
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionItem
import it.fast4x.rigallery.feature_node.presentation.common.components.OptionSheet
import it.fast4x.rigallery.feature_node.presentation.util.rememberAppBottomSheetState
import it.fast4x.rigallery.ui.theme.Shapes
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun PinnedAlbumsPager(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    onAlbumLongClick: (Album) -> Unit
) {

    val scope = rememberCoroutineScope()
    val appBottomSheetState = rememberAppBottomSheetState()
    var currentAlbum: Album? by remember { mutableStateOf(null) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { albums.size }
    )

    val txBase = 200.dp
    val currentPage = pagerState.currentPage

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

            HorizontalPager(
                contentPadding = PaddingValues(16.dp),
                state = pagerState,
                modifier = Modifier
                    //.border(BorderStroke(1.dp, Color.Red))
                    .height(250.dp),
            ) { index ->

                AlbumComponent(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .zIndex(
                            if (pagerState.currentPage == index) 1f else 0f
                        )
                        .graphicsLayer {

                            val fractionAbs = (pagerState.currentPageOffsetFraction * 2f)
                                .coerceIn(-1f, 1f)
                                .absoluteValue
                            val scale = if (currentPage == index) {
                                (1f - fractionAbs * 0.2f)
                            } else {
                                0.7f + fractionAbs * 0.1f
                            }.coerceIn(0f, 1f)
                            scaleX = scale
                            scaleY = scale
                            val txPx = (with(this) { txBase.toPx() }) * (1f - fractionAbs)
                            translationX = if (index > currentPage) {
                                -txPx
                            } else if (index < currentPage) {
                                txPx
                            } else 0f
                        },

                    album = albums[index],
                    onItemClick = onAlbumClick,
                    onItemLongClick = {
                        currentAlbum = it
                        scope.launch {
                            appBottomSheetState.show()
                        }
                    },
                    onMoveAlbumToTrash = {}
                )
            }

            Text(
                text = albums[currentPage].label,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )

    }

    val unpinTitle = stringResource(R.string.unpin)
    val tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer
    val onTertiaryContainer = MaterialTheme.colorScheme.onTertiaryContainer
    val optionList = remember {
        mutableListOf(
            OptionItem(
                text = unpinTitle,
                containerColor = tertiaryContainer,
                contentColor = onTertiaryContainer,
                icon = Icons.Outlined.AppsOutage,
                onClick = {
                    scope.launch {
                        appBottomSheetState.hide()
                        currentAlbum?.let {
                            onAlbumLongClick(it)
                            currentAlbum = null
                        }
                    }
                }
            )
        )
    }

    OptionSheet(
        state = appBottomSheetState,
        optionList = arrayOf(optionList),
        headerContent = {
            if (currentAlbum != null) {
                AsyncImage(
                    modifier = Modifier
                        .size(98.dp)
                        .clip(Shapes.large),
                    contentScale = ContentScale.Crop,
                    uri = currentAlbum!!.uri.toString(),
                    contentDescription = currentAlbum!!.label
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                letterSpacing = MaterialTheme.typography.titleLarge.letterSpacing
                            )
                        ) {
                            append(currentAlbum!!.label)
                        }
                        append("\n")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing
                            )
                        ) {
                            append(stringResource(R.string.s_items, currentAlbum!!.count))
                        }
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
        }
    )

}