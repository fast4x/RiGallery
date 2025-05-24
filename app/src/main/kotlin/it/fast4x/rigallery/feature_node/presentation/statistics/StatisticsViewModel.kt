package it.fast4x.rigallery.feature_node.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.fast4x.rigallery.feature_node.data.data_source.InternalDatabase
import it.fast4x.rigallery.feature_node.presentation.statistics.data.MediaType
import it.fast4x.rigallery.feature_node.presentation.statistics.data.MediaTypes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val database: InternalDatabase
) : ViewModel() {

    val favoriteCount = database.getMediaDao().getFavoriteCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val trashedCount = database.getMediaDao().getTrashedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val ignoredCount = database.getMediaDao().getIgnoredCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val withLocationCount = database.getMediaDao().getWithLocationCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val mediaTypeCountByYears = database.getMediaDao().getMediaTypeCountByYears()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val mediaTypeCount = database.getMediaDao().getMediaTypeCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MediaType())
    val mediaTypesCount = database.getMediaDao().getMediaTypesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

}