package it.fast4x.rigallery.feature_node.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import it.fast4x.rigallery.feature_node.domain.repository.MediaRepository
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository: MediaRepository,
    private val workManager: WorkManager,
    val handler: MediaHandleUseCase,
) : ViewModel() {

    val notAnalyzedMedia = repository.getMedia()
        .map { it.data?.filter { m -> m.analyzed == 0 } ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val mediaInDatabase = repository.getMedia()
        .map { it.data }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val analyzedMediaCount = repository.getAnalyzedMediaCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val notAnalyzedMediaCount = repository.getNotAnalyzedMediaCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val isRunning = workManager.getWorkInfosByTagFlow("MediaAnalyzer")
        .map { it.lastOrNull()?.state == WorkInfo.State.RUNNING }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val progress = workManager.getWorkInfosByTagFlow("MediaAnalyzer")
        .map {
            it.lastOrNull()?.progress?.getFloat("progress", 0f) ?: 0f
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)

    fun startAnalysis() {
        workManager.startAnalysis()
    }

    fun stopAnalysis() {
        workManager.stopAnalysis()
    }

    fun resetAnalysis() {
        viewModelScope.launch {
            repository.resetAnalizedMedia()
        }
    }

}