package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings.Search.rememberSearchHistory

@Composable
fun SearchHistoryGrid(
    historyItems: SnapshotStateList<Pair<String, String>>,
    suggestionSet: List<Pair<String, String>>,
    search: (String) -> Unit,
    historyTagsItems: SnapshotStateList<Pair<String, String>>,
    countriesTagsItems: List<String?>,
    localitiesTagsItems: List<String?>
) {

    var historySet by rememberSearchHistory()

    LazyVerticalGrid(
        state = rememberLazyGridState(),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(horizontal = 5.dp).padding(bottom = 50.dp)
    ) {

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
                    searchTag = search
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
                expanded = true
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
                searchTag = search
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
                    searchTag = search
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
                    locationIsCountry = false
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