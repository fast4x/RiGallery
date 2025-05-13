package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateListOf
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
import it.fast4x.rigallery.core.extensions.components.Title
import it.fast4x.rigallery.feature_node.domain.model.Album

@Composable
fun SearchHistoryGrid(
    historyItems: SnapshotStateList<Pair<String, String>>,
    suggestionSet: List<Pair<String, String>>,
    search: (String, Boolean) -> Unit,
    historyTagsItems: SnapshotStateList<Pair<String, String>>,
    countriesTagsItems: List<String?>,
    localitiesTagsItems: List<String?>,
    albumsTagsItems: List<Album>,
    mediaYearsItems: List<Int>
) {

    var historySet by rememberSearchHistory()
    var expanded by remember { mutableStateOf(false) }
    val textExpand = if (expanded) "Collapse" else "Expand" // stringResource(R.string.collapse) else stringResource(R.string.expand)

    var expandedRecentTags by remember { mutableStateOf(false) }
    var expandedTags by remember { mutableStateOf(false) }
    var expandedAlbums by remember { mutableStateOf(false) }
    var expandedMonths by remember { mutableStateOf(false) }
    var expandedDays by remember { mutableStateOf(false) }
    var expandedYears by remember { mutableStateOf(false) }
    var expandedMetadatas by remember { mutableStateOf(false) }
    var expandedCountries by remember { mutableStateOf(false) }
    var expandedLocalities by remember { mutableStateOf(false) }


    var searchTags = remember { mutableStateListOf<String>() }

    LazyVerticalGrid(
        state = rememberLazyGridState(),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(horizontal = 5.dp).padding(bottom = 50.dp)
    ) {

        stickyHeader {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                if (searchTags.isNotEmpty()) {
                    Row {
                        //item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Click to search tags",//stringResource(R.string.recent_tag),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(top = 16.dp)
                        )
                        //}
                    }
                    Row {
                        //item(span = { GridItemSpan(maxLineSpan) }) {
                        SelectedTagsGrid(
                            tagsItems = searchTags,
                            onClick = { search(searchTags.distinct().joinToString(" "), true) },
                            expanded = expanded
                        )
                        //}
                    }
                }

//                Row(
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp)
//                ) {
//                    Text(
//                        text = textExpand,
//                        color = MaterialTheme.colorScheme.primary,
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier
//                            .clickable {
//                                expanded = !expanded
//                            }
//                    )
//                    IconButton(
//                        onClick = {
//                            expanded = !expanded
//                        }
//                    ) {
//                        Icon(
//                            imageVector = if (expanded) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
//                            modifier = Modifier.fillMaxHeight(),
//                            contentDescription = null
//                        )
//                    }
//                }
            }
        }

//        item(span = { GridItemSpan(maxLineSpan) }) {
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp)
//            ) {
//                Text(
//                    text = textExpand,
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.titleMedium,
//                    modifier = Modifier
//                        .clickable{
//                            expanded = !expanded
//                        }
//                )
//                IconButton(
//                    onClick = {
//                        expanded = !expanded
//                    }
//                ) {
//                    Icon(
//                        imageVector = if (expanded) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
//                        modifier = Modifier.fillMaxHeight(),
//                        contentDescription = null
//                    )
//                }
//            }
//        }

        if (historyTagsItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
//                Text(
//                    text = stringResource(R.string.recent_tag),
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.titleMedium,
//                    modifier = Modifier
//                        .padding(vertical = 8.dp)
//                        .padding(top = 16.dp)
//                )
                Title(
                    title = stringResource(R.string.recent_tag),
                    icon = if (expandedRecentTags) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    onClick = {
                        expandedRecentTags = !expandedRecentTags
                    },
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                RecentTagsGrid(
                    historyTagsItems = historyTagsItems,
                    searchTag = {  search(it, true) },
                    addSearchTag = { it, maybeCanQuery ->
                        if (searchTags.contains(it))
                            searchTags.remove(it)
                        else searchTags.add(it)
                        search(searchTags.distinct().joinToString(" "), maybeCanQuery)
                    },
                    expanded = expandedRecentTags
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
//            Text(
//                text = stringResource(R.string.tag),
//                color = MaterialTheme.colorScheme.primary,
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//                    .padding(top = 16.dp)
//            )
            Title(
                title = stringResource(R.string.tag),
                icon = if (expandedTags) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                onClick = {
                    expandedTags = !expandedTags
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            TagsGrid(
                searchTag = {  search(it, true) },
                addSearchTag = { it, maybeCanQuery ->
                    if (searchTags.contains(it))
                        searchTags.remove(it)
                    else searchTags.add(it)
                    search(searchTags.distinct().joinToString(" "), maybeCanQuery)
                },
                selectedTags = searchTags,
                expanded = expandedTags
            )
        }

        if (albumsTagsItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
//                Text(
//                    text = stringResource(R.string.album),
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.titleMedium,
//                    modifier = Modifier
//                        .padding(vertical = 8.dp)
//                        .padding(top = 16.dp)
//                )
                Title(
                    title = stringResource(R.string.album),
                    icon = if (expandedAlbums) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    onClick = {
                        expandedAlbums = !expandedAlbums
                    },
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                AlbumsTagsGrid(
                    tagsItems = albumsTagsItems,
                    searchTag = {  search(it, true) },
                    addSearchTag = { it, maybeCanQuery ->
                        if (searchTags.contains(it))
                            searchTags.remove(it)
                        else searchTags.add(it)
                        search(searchTags.distinct().joinToString(" "), maybeCanQuery)
                    },
                    selectedTags = searchTags,
                    expanded = expandedAlbums
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
//            Text(
//                text = stringResource(R.string.month),
//                color = MaterialTheme.colorScheme.primary,
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//                    .padding(top = 16.dp)
//            )
            Title(
                title = stringResource(R.string.month),
                icon = if (expandedMonths) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                onClick = {
                    expandedMonths = !expandedMonths
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            MonthTagsGrid (
                searchTag = {  search(it, true) },
                addSearchTag = { it, maybeCanQuery ->
                    if (searchTags.contains(it))
                        searchTags.remove(it)
                    else searchTags.add(it)
                    search(searchTags.distinct().joinToString(" "), maybeCanQuery)
                },
                selectedTags = searchTags,
                expanded = expandedMonths
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
//            Text(
//                text = stringResource(R.string.day),
//                color = MaterialTheme.colorScheme.primary,
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//                    .padding(top = 16.dp)
//            )
            Title(
                title = stringResource(R.string.day),
                icon = if (expandedDays) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                onClick = {
                    expandedDays = !expandedDays
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            DayTagsGrid (
                searchTag = {  search(it, true) },
                addSearchTag = { it, maybeCanQuery ->
                    if (searchTags.contains(it))
                        searchTags.remove(it)
                    else searchTags.add(it)
                    search(searchTags.distinct().joinToString(" "), maybeCanQuery)
                },
                selectedTags = searchTags,
                expanded = expandedDays
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
//            Text(
//                text = stringResource(R.string.year),
//                color = MaterialTheme.colorScheme.primary,
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//                    .padding(top = 16.dp)
//            )
            Title(
                title = stringResource(R.string.year),
                icon = if (expandedYears) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                onClick = {
                    expandedYears = !expandedYears
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            YearTagsGrid (
                searchTag = {  search(it, true) },
                addSearchTag = { it, maybeCanQuery ->
                    if (searchTags.contains(it))
                        searchTags.remove(it)
                    else searchTags.add(it)
                    search(searchTags.distinct().joinToString(" "), maybeCanQuery)
                },
                selectedTags = searchTags,
                tagsItems = mediaYearsItems,
                expanded = expandedYears
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
//            Text(
//                text = stringResource(R.string.metadata),
//                color = MaterialTheme.colorScheme.primary,
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//                    .padding(top = 16.dp)
//            )
            Title(
                title = stringResource(R.string.metadata),
                icon = if (expandedMetadatas) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                onClick = {
                    expandedMetadatas = !expandedMetadatas
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            MetadataTagsGrid (
                searchTag = {  search(it, true) },
                addSearchTag = { it, maybeCanQuery ->
                    if (searchTags.contains(it))
                        searchTags.remove(it)
                    else searchTags.add(it)
                    search(searchTags.distinct().joinToString(" "), maybeCanQuery)
                },
                selectedTags = searchTags,
                expanded = expandedMetadatas
            )
        }

        if (countriesTagsItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
//                Text(
//                    text = stringResource(R.string.countries),
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.titleMedium,
//                    modifier = Modifier
//                        .padding(vertical = 8.dp)
//                        .padding(top = 16.dp)
//                )
                Title(
                    title = stringResource(R.string.countries),
                    icon = if (expandedCountries) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    onClick = {
                        expandedCountries = !expandedCountries
                    },
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                LocationTagsGrid(
                    searchTag = {  search(it, true) },
                    tagsItems = countriesTagsItems,
                    expanded = expandedCountries,
                    addSearchTag = { it, maybeCanQuery ->
                        if (searchTags.contains(it))
                            searchTags.remove(it)
                        else searchTags.add(it)
                        search(searchTags.distinct().joinToString(" "), maybeCanQuery)
                    },
                    selectedTags = searchTags
                )
            }
        }

        if (localitiesTagsItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
//                Text(
//                    text = stringResource(R.string.localities),
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.titleMedium,
//                    modifier = Modifier
//                        .padding(vertical = 8.dp)
//                        .padding(top = 16.dp)
//                )
                Title(
                    title = stringResource(R.string.localities),
                    icon = if (expandedLocalities) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    onClick = {
                        expandedLocalities = !expandedLocalities
                    },
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                LocationTagsGrid(
                    tagsItems = localitiesTagsItems,
                    searchTag = {  search(it, true) },
                    addSearchTag = { it, maybeCanQuery ->
                        if (searchTags.contains(it))
                            searchTags.remove(it)
                        else searchTags.add(it)
                        search(searchTags.distinct().joinToString(" "), maybeCanQuery)
                    },
                    locationIsCountry = false,
                    selectedTags = searchTags,
                    expanded = expandedLocalities
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
                    search = {  search(it, true) },
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
                    search = {  search(it, true) },
                )
            }
        }
    }
}