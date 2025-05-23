package it.fast4x.rigallery.feature_node.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.fast4x.rigallery.feature_node.data.data_source.InternalDatabase
import it.fast4x.rigallery.feature_node.presentation.statistics.data.MediaType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val database: InternalDatabase
) : ViewModel() {

    var year = 2023
    var mediaType = "image"

    val favoriteCount = database.getMediaDao().getFavoriteCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val trashedCount = database.getMediaDao().getTrashedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val ignoredCount = database.getMediaDao().getIgnoredCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val withLocationCount = database.getMediaDao().getWithLocationCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val videoCount = database.getMediaDao().getVideosCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val imageCount = database.getMediaDao().getImagesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val audioCount = database.getMediaDao().getAudiosCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val mediaCount = database.getMediaDao().getMediaCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val mediaTypes = database.getMediaDao().getMediaTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val years = database.getMediaDao().getYears()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val mediaCountByYears = database.getMediaDao().getMediaCountByYears()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val mediaTypeCountByYears = database.getMediaDao().getMediaTypeCountByYears()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val mediaTypeCount = database.getMediaDao().getMediaTypeCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MediaType())
    val mediaCountByYear = database.getMediaDao().getMediaCountByYear(year)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val mediaCountByType = database.getMediaDao().getMediaCountByType(mediaType)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)




}