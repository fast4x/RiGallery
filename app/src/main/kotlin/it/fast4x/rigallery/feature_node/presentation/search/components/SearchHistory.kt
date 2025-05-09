package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings.Search.rememberSearchHistory

@Composable
fun SearchHistory(search: (query: String) -> Unit) {
    var historySet by rememberSearchHistory()
    val historyItems = remember(historySet) {
        historySet.toList().mapIndexed { index, item ->
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
        }.sortedByDescending { it.first }.take(5).toMutableStateList()
    }
    val suggestionSet = listOf(
        "0" to "Screenshots",
        "1" to "Camera",
        //"2" to "May 2022",
        //"3" to "Thursday"
    )

    SearchHistoryGrid(
        historyItems = historyItems,
        suggestionSet = suggestionSet,
        search = search
    )

//    Column {
//        if (historyItems.isNotEmpty()) {
//            Text(
//                text = stringResource(R.string.history_recent_title),
//                color = MaterialTheme.colorScheme.primary,
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier
//                    .padding(horizontal = 16.dp, vertical = 8.dp)
//                    .padding(top = 16.dp)
//            )
//
//
//            repeat(historyItems.size) {
//                HistoryItem(
//                    historyQuery = historyItems[it],
//                    search = search,
//                ) {
//                    historySet = historySet.toMutableSet().apply { remove(it) }
//                }
//            }
//        }
//        Text(
//            text = stringResource(R.string.history_suggestions_title),
//            color = MaterialTheme.colorScheme.primary,
//            style = MaterialTheme.typography.titleMedium,
//            modifier = Modifier
//                .padding(horizontal = 16.dp, vertical = 8.dp)
//                .padding(top = 16.dp)
//        )
//
//        repeat(suggestionSet.size) {
//            HistoryItem(
//                historyQuery = suggestionSet[it],
//                search = search,
//            )
//        }
//    }
}