package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import it.fast4x.rigallery.core.Settings.Search.rememberSearchHistory
import it.fast4x.rigallery.core.Settings.Search.rememberSearchTagsHistory
import it.fast4x.rigallery.feature_node.domain.model.Media

@Composable
fun SearchHistory(
    mediaWithLocation: State<List<Media.UriMedia>>,
    search: (query: String) -> Unit
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
        mediaWithLocation.value.map {
            it.location?.substringAfterLast(delimiter = ",", missingDelimiterValue = "")?.trim()
        }.distinct().sortedByDescending { it }
    }

    val localitiesTagsItems = remember {
        mediaWithLocation.value.map {
            it.location?.substringBefore(delimiter = ",", missingDelimiterValue = "")?.trim()
        }.distinct().sortedByDescending { it }
    }

    println("SearchHistory: countries: $countriesTagsItems and localities: $localitiesTagsItems")

    val suggestionSet = listOf(
        "0" to "Screenshots",
        "1" to "Camera",
        //"2" to "May 2022",
        //"3" to "Thursday"
    )

    SearchHistoryGrid(
        historyItems = historyItems,
        historyTagsItems = historyTagsItems,
        countriesTagsItems = countriesTagsItems,
        localitiesTagsItems = localitiesTagsItems,
        suggestionSet = suggestionSet,
        search = search
    )

}