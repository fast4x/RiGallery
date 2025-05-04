package it.fast4x.rigallery.feature_node.presentation.analysis

import android.content.Context
import android.graphics.ColorSpace
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import androidx.exifinterface.media.ExifInterface
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import it.fast4x.rigallery.feature_node.data.data_source.InternalDatabase
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaVersion
import it.fast4x.rigallery.feature_node.domain.repository.MediaRepository
import it.fast4x.rigallery.feature_node.presentation.util.mediaStoreVersion
import it.fast4x.rigallery.feature_node.presentation.util.printWarning
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.request.ImageRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import it.fast4x.rigallery.feature_node.domain.model.LocationData
import it.fast4x.rigallery.feature_node.domain.model.getLocationData
import it.fast4x.rigallery.feature_node.presentation.classifier.ImageClassifierHelper
import it.fast4x.rigallery.feature_node.presentation.util.ExifMetadata
import it.fast4x.rigallery.feature_node.presentation.util.formattedAddress
import it.fast4x.rigallery.feature_node.presentation.util.getExifInterface
import it.fast4x.rigallery.feature_node.presentation.util.getGeocoder
import it.fast4x.rigallery.feature_node.presentation.util.getLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltWorker
class AnalyzerWorker @AssistedInject constructor(
    private val database: InternalDatabase,
    private val repository: MediaRepository,
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            printWarning("ClassifierWorker retrieving media")

            val media = database.getMediaDao().getMedia()
                .filter { it.analyzed == 0 }

            if (media.isEmpty()) {
                printWarning("MediaAnalyzer media is empty, we can abort")
                setProgress(workDataOf("progress" to 100))
                return@withContext Result.success()
            }
            printWarning("MediaAnalyzer not analyzed media size: ${media.size}")
            setProgress(workDataOf("progress" to 0))

            printWarning("MediaAnalyzer Starting analysis for ${media.size} items")
            media.fastForEachIndexed { index, item ->
                //printWarning("MediaAnalyzer Processing item $index")
                setProgress(workDataOf("progress" to (index / (media.size - 1).toFloat()) * 100f))
                try {
                    var media = item
                    getLocationData(appContext, media,
                        onLocationFound = {
                            printWarning("MediaAnalyzer Updating media $index with location: ${it?.location}")
                            launch(Dispatchers.IO) {
                                printWarning("MediaAnalyzer Updating item $media")
                                database.getMediaDao().updateMedia(
                                    if (it?.location == null || it.location.isEmpty())
                                        item.copy(
                                        analyzed = 1
                                        )
                                    else
                                        item.copy(
                                            analyzed = 1,
                                            location = it.location
                                        )
                                )
                            }
                        }
                    )

                } catch (e: Exception) {
                    println("MediaAnalyzer Error ${e.stackTraceToString()} at item $index")
                    return@fastForEachIndexed
                }
            }
            setProgress(workDataOf("progress" to 100f))
        }

        return Result.success()
    }

//    override fun onError(error: String) {
//        printWarning("ClassifierWorker ImageClassifierHelper Error: $error")
//    }

}

fun WorkManager.startAnalysis(indexStart: Int = 0, size: Int = 50) {
    val inputData = Data.Builder()
        .putInt("chunkIndexStart", indexStart)
        .putInt("chunkSize", size)
        .build()
    val uniqueWork = OneTimeWorkRequestBuilder<AnalyzerWorker>()
        .addTag("MediaAnalyzer")
        .setConstraints(
            Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()
        )
        .setInputData(inputData)
        .build()

    enqueueUniqueWork(
        "MediaAnalyzer_${indexStart}_$size",
        ExistingWorkPolicy.REPLACE,
        uniqueWork
    )
}

fun WorkManager.stopAnalysis() {
    cancelAllWorkByTag("MediaAnalyzer")
}