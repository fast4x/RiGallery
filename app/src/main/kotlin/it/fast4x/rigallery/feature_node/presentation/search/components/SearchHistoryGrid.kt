package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings.Search.rememberSearchHistory
import it.fast4x.rigallery.feature_node.domain.model.Album

@Composable
fun SearchHistoryGrid(
    historyItems: SnapshotStateList<Pair<String, String>>,
    suggestionSet: List<Pair<String, String>>,
    search: (String) -> Unit,
    historyTagsItems: SnapshotStateList<Pair<String, String>>,
    countriesTagsItems: List<String?>,
    localitiesTagsItems: List<String?>,
    albumsTagsItems: List<Album>
) {

    var historySet by rememberSearchHistory()
    var expanded by remember { mutableStateOf(false) }
    val textExpand = if (expanded) "Collapse" else "Expand" // stringResource(R.string.collapse) else stringResource(R.string.expand)

    LazyVerticalGrid(
        state = rememberLazyGridState(),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(horizontal = 5.dp).padding(bottom = 50.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = textExpand,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .clickable{
                            expanded = !expanded
                        }
                )
                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                        modifier = Modifier.fillMaxHeight(),
                        contentDescription = null
                    )
                }
            }
        }

        if (historyTagsItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Recent tags",//stringResource(R.string.history_recent_tags),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(top = 16.dp)
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                RecentTagsGrid(
                    historyTagsItems = historyTagsItems,
                    searchTag = search,
                    expanded = expanded
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Tags", //stringResource(R.string.tags),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(top = 16.dp)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            TagsGrid(
                searchTag = search,
                expanded = expanded
            )
        }

        if (albumsTagsItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Albums", //stringResource(R.string.metadata),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(top = 16.dp)
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                AlbumsTagsGrid(
                    tagsItems = albumsTagsItems,
                    searchTag = search,
                    expanded = expanded
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Month", //stringResource(R.string.month),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(top = 16.dp)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            MonthTagsGrid (
                searchTag = search,
                expanded = expanded
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(R.string.metadata),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(top = 16.dp)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            MetadataTagsGrid (
                searchTag = search,
                expanded = expanded
            )
        }

        if (countriesTagsItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Countries", //stringResource(R.string.metadata),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(top = 16.dp)
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                LocationTagsGrid(
                    tagsItems = countriesTagsItems,
                    searchTag = search,
                    expanded = expanded
                )
            }
        }

        if (localitiesTagsItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Localities", //stringResource(R.string.metadata),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(top = 16.dp)
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                LocationTagsGrid(
                    tagsItems = localitiesTagsItems,
                    searchTag = search,
                    locationIsCountry = false,
                    expanded = expanded
                )
            }
        }

        if (historyItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.history_recent_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(top = 16.dp)
                )
            }
            items(historyItems.size) { index ->
                HistoryItem(
                    historyQuery = historyItems[index],
                    search = search,
                ) {
                    historySet = historySet.toMutableSet().apply { remove(it) }
                }
            }
        }
        if (suggestionSet.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.history_suggestions_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(top = 16.dp)
                )
            }
            items(suggestionSet.size) { index ->
                HistoryItem(
                    historyQuery = suggestionSet[index],
                    search = search,
                )
            }
        }
    }
}