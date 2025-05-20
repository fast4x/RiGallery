package it.fast4x.rigallery.feature_node.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import it.fast4x.rigallery.feature_node.data.data_source.InternalDatabase
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.repository.MediaRepository
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.work.Constraints
import androidx.work.impl.WorkManagerImpl
import it.fast4x.rigallery.feature_node.presentation.mediaview.components.LocationItem

const val WORKERS = "WORKERS"

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository: MediaRepository,
    private val workManager: WorkManager,
    val handler: MediaHandleUseCase,
    private val database: InternalDatabase,
) : ViewModel() {


    val WORKERNAME_LOCATION = "locationWorker"
    val WORKERNAME_DOMINANT_COLOR = "dominantColorWorker"


    val mediaInDb = database.getMediaDao().getMediaCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val isRunningLoc = workManager.getWorkInfosByTagFlow(WORKERNAME_LOCATION)
        .map { it.lastOrNull()?.state == WorkInfo.State.RUNNING }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val isRunningDomCol = workManager.getWorkInfosByTagFlow(WORKERNAME_DOMINANT_COLOR)
        .map { it.lastOrNull()?.state == WorkInfo.State.RUNNING }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val progressLoc = workManager.getWorkInfosByTagFlow(WORKERNAME_LOCATION)
        .map {
            it.lastOrNull()?.progress?.getFloat("progress", 0f) ?: 0f
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)

    val progressDomCol = workManager.getWorkInfosByTagFlow(WORKERNAME_DOMINANT_COLOR)
        .map {
            it.lastOrNull()?.progress?.getFloat("progress", 0f) ?: 0f
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)

    fun startAnalysis() {
        if (isRunningLoc.value && isRunningDomCol.value) return
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

    fun resetAnalysisForLocation() {
        viewModelScope.launch {
            repository.resetAnalizedMediaForLocation()
        }
    }

    fun resetAnalysisForDominantColor() {
        viewModelScope.launch {
            repository.resetAnalizedMediaForDominantColor()
        }
    }

    fun WorkManager.startAnalysis(){
        // Cancel previous work
        cancelAllWork()

        val locationUniqueWork = OneTimeWorkRequestBuilder<LocationWorker>()
        .addTag(WORKERNAME_LOCATION)
        .setConstraints(
            Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()
        )
        //.setInputData(inputData)
        .build()

        val dominantColorUniqueWork = OneTimeWorkRequestBuilder<DominantColorWorker>()
            .addTag(WORKERNAME_DOMINANT_COLOR)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            //.setInputData(inputData)
            .build()

        enqueueUniqueWork(
            WORKERNAME_LOCATION,
            ExistingWorkPolicy.REPLACE,
            locationUniqueWork
        )

        enqueueUniqueWork(
            WORKERNAME_DOMINANT_COLOR,
            ExistingWorkPolicy.REPLACE,
            dominantColorUniqueWork
        )

//        beginUniqueWork(
//            WORKERS,
//            ExistingWorkPolicy.REPLACE,
//            OneTimeWorkRequest.from(LocationWorker::class.java)
//        )
//        .then(OneTimeWorkRequest.from(DominantColorWorker::class.java))
//        .enqueue()
    }

    fun WorkManager.stopAnalysis(){
        //cancelAllWorkByTag(WORKERS)
        cancelAllWork()
    }


}