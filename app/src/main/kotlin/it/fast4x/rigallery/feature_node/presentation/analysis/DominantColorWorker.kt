package it.fast4x.rigallery.feature_node.presentation.analysis

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
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
import it.fast4x.rigallery.feature_node.presentation.util.printWarning
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.util.ext.dominantColorInImage
import it.fast4x.rigallery.core.util.isAtLeastAndroid14
import it.fast4x.rigallery.core.util.isAtLeastAndroid15
import it.fast4x.rigallery.feature_node.domain.model.getLocationData
import it.fast4x.rigallery.feature_node.presentation.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



@HiltWorker
class DominantColorWorker @AssistedInject constructor(
    private val database: InternalDatabase,
    //private val repository: MediaRepository,
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    val WORKERNAME = "dominantColorWorker"
    val NOTIFICATION_CHANNEL = WORKERNAME
    val NOTIFICATION_ID = 2 // Use differtent number for each worker

    var title = ""
    var message = ""

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            //printWarning("DominantColorWorker retrieving media")

//            var media = database.getMediaDao().getMedia()
//
//            if (media.isEmpty()) {
//                printWarning("DominantColorWorker media is empty, let's try and update the database")
//                val mediaVersion = appContext.mediaStoreVersion
//                //printWarning("DominantColorWorker Force-updating database to version $mediaVersion")
//                database.getMediaDao().setMediaVersion(MediaVersion(mediaVersion))
//                val fetchedMedia =
//                    repository.getMedia().map { it.data ?: emptyList() }.firstOrNull()
//                fetchedMedia?.let {
//                    database.getMediaDao().updateMedia(it)
//                }
//            }



            val mediaForDominantColor = database.getMediaDao().getMediaToAnalyzeDominantColor()
            printWarning("DominantColorWorker not analyzed media for dominant color size: ${mediaForDominantColor.size}")
            if (mediaForDominantColor.isEmpty() == true) {
                printWarning("DominantColorWorker media is empty, nothing to analyze for dominant color")
            } else {
                setProgress(workDataOf("progress" to 0))
                printWarning("DominantColorWorker Starting analysis for ${mediaForDominantColor.size} items with dominant color")
                mediaForDominantColor.fastForEachIndexed { index, item ->
                    setProgress(workDataOf("progress" to (index / (mediaForDominantColor.size - 1).toFloat()) * 100f))
                    title =
                        (if (isAtLeastAndroid15)
                            applicationContext.getString(R.string.analysis_for_dominant_color)
                        else "${
                            applicationContext.getString(R.string.analysis_for_dominant_color)
                        } ${index + 1}/${mediaForDominantColor.size}").toString()
                    message = if (isAtLeastAndroid15) applicationContext.getString(R.string.scanning_in_progress_please_wait) else "File: ${item.label}"
                    //println("DominantColorWorker index $index media.size ${mediaForDominantColor.size}")
                    setForeground(
                        createForegroundInfo(
                            title,
                            message,
                            index,
                            mediaForDominantColor.size
                        )
                    )
                    /* Process dominant color */
                    dominantColorInImage(appContext, item.uri.toString()).let {
                        launch(Dispatchers.IO) {
                            printWarning("DominantColorWorker Updating with dominantColor: $it at item $index")
                            database.getMediaDao().setDominantColor(item.id, it)
//                            database.getMediaDao().updateMedia(
//                                item.copy(
//                                    dominantColor = it
//                                )
//                            )
                        }
                    }


                }
            }

            setProgress(workDataOf("progress" to 100f))

        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setName(applicationContext.getText(R.string.analysis_for_dominant_color))
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
