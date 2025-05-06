package it.fast4x.rigallery.feature_node.presentation.analysis

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.ColorSpace
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.exifinterface.media.ExifInterface
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
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
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.util.isAtLeastAndroid14
import it.fast4x.rigallery.core.util.isAtLeastAndroid15
import it.fast4x.rigallery.feature_node.domain.model.LocationData
import it.fast4x.rigallery.feature_node.domain.model.getLocationData
import it.fast4x.rigallery.feature_node.presentation.classifier.ImageClassifierHelper
import it.fast4x.rigallery.feature_node.presentation.main.MainActivity
import it.fast4x.rigallery.feature_node.presentation.util.ExifMetadata
import it.fast4x.rigallery.feature_node.presentation.util.formattedAddress
import it.fast4x.rigallery.feature_node.presentation.util.getExifInterface
import it.fast4x.rigallery.feature_node.presentation.util.getGeocoder
import it.fast4x.rigallery.feature_node.presentation.util.getLocation
import it.fast4x.rigallery.feature_node.presentation.util.mediaFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.reflect.KMutableProperty1

const val NOTIFICATION_CHANNEL = "analyzerWorker"
const val NOTIFICATION_ID = 1

@HiltWorker
class AnalyzerWorker @AssistedInject constructor(
    private val database: InternalDatabase,
    private val repository: MediaRepository,
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    var title = ""
    var message = ""

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            printWarning("MediaAnalyzer retrieving media")

            var media = database.getMediaDao().getMedia()

            if (media.isEmpty()) {
                printWarning("MediaAnalyzer media is empty, let's try and update the database")
                val mediaVersion = appContext.mediaStoreVersion
                printWarning("MediaAnalyzer Force-updating database to version $mediaVersion")
                database.getMediaDao().setMediaVersion(MediaVersion(mediaVersion))
                val fetchedMedia =
                    repository.getMedia().map { it.data ?: emptyList() }.firstOrNull()
                fetchedMedia?.let {
                    database.getMediaDao().updateMedia(it)
                }
            }

            media = database.getMediaDao().getMedia().filter { it.analyzed == 0 }

            printWarning("MediaAnalyzer mediaToAnalyze size: ${media.size}")

            if (media.isEmpty() == true) {
                printWarning("MediaAnalyzer media is empty, we can abort")
                setProgress(workDataOf("progress" to 100))
                return@withContext Result.success()
            }
            printWarning("MediaAnalyzer not analyzed media size: ${media.size}")
            setProgress(workDataOf("progress" to 0))

            printWarning("MediaAnalyzer Starting analysis for ${media.size} items")
            media.fastForEachIndexed { index, item ->
                setProgress(workDataOf("progress" to (index / (media.size - 1).toFloat()) * 100f))
                title = (if (isAtLeastAndroid15) applicationContext.getText(R.string.analyzing_media) else "${applicationContext.getText(R.string.analyzing_media)} ${index + 1}/${media.size}").toString()
                message = if (isAtLeastAndroid15) "" else "File: ${item.label}"
                println("MediaAnalyzer index $index media.size ${media.size}")
                setForeground(createForegroundInfo(title, message, index, media.size))
                try {
                    var media = item
                    getLocationData(appContext, media,
                        onLocationFound = {
                            //printWarning("MediaAnalyzer Updating media $index with location: ${it?.location}")
                            launch(Dispatchers.IO) {
                                //printWarning("MediaAnalyzer Updating item $media")
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

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL, NotificationManagerCompat.IMPORTANCE_HIGH)
            //.setName(applicationContext.getText(R.string.analysis_channel_name))
            .setName("Analyzer channel name")
            .setShowBadge(false)
            .build()

        NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel)
    }

    private fun createForegroundInfo(title: String? = null, message: String? = null, progress: Int = 0, max: Int = 100): ForegroundInfo {
        createNotificationChannel()

        val pendingIntentFlags =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val openAppIntent = Intent(applicationContext, MainActivity::class.java).let {
            PendingIntent.getActivity(applicationContext, MainActivity.OPEN_FROM_ANALYZER, it, pendingIntentFlags)
        }
        val stopAction = NotificationCompat.Action.Builder(
            R.drawable.ic_remove,
            applicationContext.getString(R.string.action_stop),
            WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        ).build()

        val contentTitle = title
        val notification = NotificationCompat.Builder(applicationContext,
            NOTIFICATION_CHANNEL
        )
            .setContentTitle(contentTitle)
            .setTicker(contentTitle)
            .setSilent(true)
            .setColorized(true)
            .setAutoCancel(true)
            .setContentText(message)
            .setProgress(max, progress, if (isAtLeastAndroid15) true else false) //Workaround to android 15 because notification freeze
            .setSmallIcon(R.drawable.ic_gallery_thumbnail)
            .setOngoing(true)
            .setContentIntent(openAppIntent)
            .addAction(stopAction)
            .build()

        // from Android 14 (API 34), foreground service type is mandatory for long-running workers:
        // https://developer.android.com/guide/background/persistent/how-to/long-running
        return when {
            isAtLeastAndroid15 -> ForegroundInfo(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROCESSING)
            isAtLeastAndroid14 -> ForegroundInfo(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            else -> ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

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