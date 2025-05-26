package it.fast4x.rigallery.feature_node.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.fast4x.rigallery.feature_node.data.data_source.InternalDatabase
import it.fast4x.rigallery.feature_node.domain.model.Event
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.presentation.statistics.data.MediaType
import it.fast4x.rigallery.feature_node.presentation.statistics.data.MediaTypes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
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
    val topMedia = database.getEventDao().getTopMedia(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun insertEvent(event: Event){
        val todayTimestamp = System.currentTimeMillis()
        println("StatisticsViewModel.insertEvent: todayTimestamp: $todayTimestamp event: $event")
        viewModelScope.launch {
            println("StatisticsViewModel.insertEvent: todayTimestamp: $todayTimestamp event in db: ${database.getEventDao().getEvent(event.mediaId)}")
            if (!database.getEventDao().isEventRegistered(todayTimestamp, event.mediaId)) {
                database.getEventDao().insertEvent(event)
                println("StatisticsViewModel.insertEvent: event registered")
            } else {
                println("StatisticsViewModel.insertEvent: event already registered")
                println("StatisticsViewModel.insertEvent: events ${database.getEventDao().getEventsCount()}")
            }

        }

    }

}