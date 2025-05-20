package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.fast4x.rigallery.core.Settings.Search.rememberSearchHistory
import it.fast4x.rigallery.core.Settings.Search.rememberSearchTagsHistory
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.presentation.albums.AlbumsViewModel
import it.fast4x.rigallery.feature_node.presentation.common.MediaViewModel
import kotlinx.coroutines.Dispatchers
import java.time.Instant
import java.time.ZoneId

@Composable
fun SearchHistory(
    mediaWithLocation: State<List<Media.UriMedia>>,
    mediaFlowState: State<MediaState<Media.UriMedia>>,
    search: (String, Boolean) -> Unit
) {
    var historySet by rememberSearchHistory()
    var historyTagsSet by rememberSearchTagsHistory()
    val historyItems = remember(historySet) {
        historySet.toList()
            .filterNot {
                it.substringAfter(
                    delimiter = "/",
                    missingDelimiterValue = it
                ).startsWith("#")
            }
            .mapIndexed { index, item ->
            Pair(
                item.substringBefore(
                    delimiter = "/",
                    missingDelimiterValue = index.toString()
                ),
                item.substringAfter(
                    delimiter = "/",
                    missingDelimiterValue = item
                )
            )
        }.sortedByDescending { it.first }.take(10).toMutableStateList()
    }

    val historyTagsItems = remember(historyTagsSet) {
        historyTagsSet.toList()
            .filter {
                it.substringAfter(
                    delimiter = "/",
                    missingDelimiterValue = it
                ).startsWith("#")
            }
            .mapIndexed { index, item ->
                Pair(
                    item.substringBefore(
                        delimiter = "/",
                        missingDelimiterValue = index.toString()
                    ),
                    item.substringAfter(
                        delimiter = "/",
                        missingDelimiterValue = item
                    )
                )
            }.sortedByDescending { it.first }.take(10).toMutableStateList()
    }



    val countriesTagsItems = remember {
        mediaWithLocation.value.mapNotNull {
            it.location?.substringAfterLast(delimiter = ",", missingDelimiterValue = "")?.trim()
        }.distinct().sortedByDescending { it }
    }

    val localitiesTagsItems = remember {
        mediaWithLocation.value.mapNotNull {
            it.location?.substringBefore(delimiter = ",", missingDelimiterValue = "")?.trim()
        }.distinct().sortedByDescending { it }
    }

   val mediaViewModel = hiltViewModel<MediaViewModel>()
    val colorsTagsItems = remember {
        mediaViewModel.dominantColors.value  //.shuffled()
    }

    val albumsViewModel = hiltViewModel<AlbumsViewModel>()
    val albumsState =
        albumsViewModel.albumsFlow.collectAsStateWithLifecycle(context = Dispatchers.IO)

    val mediaYearsItems = mediaFlowState.value.media
        .map {
            val dt = Instant.ofEpochSecond(it.definedTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            dt.year
        }
        .distinct()

    //TODO add suggestion set
    val suggestionSet = listOf(
        "0" to "Screenshots",
        "1" to "Camera",
    )



    SearchHistoryGrid(
        historyItems = historyItems,
        historyTagsItems = historyTagsItems,
        countriesTagsItems = countriesTagsItems,
        localitiesTagsItems = localitiesTagsItems,
        albumsTagsItems = albumsState.value.albums,
        mediaYearsItems = mediaYearsItems,
        colorsTagsItems = colorsTagsItems,
        suggestionSet = suggestionSet,
        search = search
    )

}