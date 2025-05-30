package it.fast4x.rigallery.feature_node.presentation.ignored

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.fast4x.rigallery.feature_node.domain.model.IgnoredAlbum
import it.fast4x.rigallery.feature_node.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IgnoredViewModel @Inject constructor(
    private val repository: MediaRepository
): ViewModel() {

    val blacklistState = repository.getBlacklistedAlbums()
        .map { IgnoredState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), IgnoredState())

    fun addToBlacklist(ignoredAlbum: IgnoredAlbum) {
        viewModelScope.launch {
            repository.addBlacklistedAlbum(ignoredAlbum)
        }
    }

    fun removeFromBlacklist(ignoredAlbum: IgnoredAlbum) {
        viewModelScope.launch {
            repository.removeBlacklistedAlbum(ignoredAlbum)
        }
    }
}