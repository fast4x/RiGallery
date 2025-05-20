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
import it.fast4x.rigallery.core.util.isAtLeastAndroid14
import it.fast4x.rigallery.core.util.isAtLeastAndroid15
import it.fast4x.rigallery.feature_node.domain.model.getLocationData
import it.fast4x.rigallery.feature_node.presentation.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltWorker
class LocationWorker @AssistedInject constructor(
    private val database: InternalDatabase,
    //private val repository: MediaRepository,
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    val WORKERNAME = "locationWorker"
    val NOTIFICATION_CHANNEL = WORKERNAME
    val NOTIFICATION_ID = 1

    var title = ""
    var message = ""

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            //printWarning("LocationWorker retrieving media")

//            var media = database.getMediaDao().getMedia()
//
//            if (media.isEmpty()) {
//                printWarning("LocationWorker media is empty, let's try and update the database")
//                val mediaVersion = appContext.mediaStoreVersion
//                //printWarning("LocationWorker Force-updating database to version $mediaVersion")
//                database.getMediaDao().setMediaVersion(MediaVersion(mediaVersion))
//                val fetchedMedia =
//                    repository.getMedia().map { it.data ?: emptyList() }.firstOrNull()
//                fetchedMedia?.let {
//                    database.getMediaDao().updateMedia(it)
//                }
//            }

            val mediaForLocation = database.getMediaDao().getMediaToAnalyzeLocation()
            printWarning("LocationWorker not analyzed media for location size: ${mediaForLocation.size}")


//            media = database.getMediaDao().getMedia()
//            val mediaForLocation = media.filter { it.analyzed == 0 && (it.location == null || it.location == "") }
//            val mediaForDominantColor = media.filter { it.analyzed == 0 && (it.dominantColor == null || it.dominantColor == 0) }

            if (mediaForLocation.isEmpty() == true) {
                printWarning("LocationWorker media is empty, nothing to analyze fro location")
                //setProgress(workDataOf("progress" to 100))
                //return@withContext Result.success()
            } else {
                setProgress(workDataOf("progress" to 0))
                printWarning("LocationWorker Starting analysis for ${mediaForLocation.size} items with location")
                mediaForLocation.fastForEachIndexed { index, item ->
                    setProgress(workDataOf("progress" to (index / (mediaForLocation.size - 1).toFloat()) * 100f))
                    title =
                        (if (isAtLeastAndroid15)
                            //applicationContext.getText(R.string.analyzing_media)
                            "Analyzing location"
                        else "${
                            "Analyzing location"
                        } ${index + 1}/${mediaForLocation.size}").toString()
                    message = if (isAtLeastAndroid15) "Analyzing location" else "File: ${item.label}"
                    //println("LocationWorker index $index media.size ${media.size}")
                    setForeground(
                        createForegroundInfo(
                            title,
                            message,
                            index,
                            mediaForLocation.size
                        )
                    )

                    /* Process location */
                    try {
                        //var localMedia = item
                        getLocationData(
                            appContext, item,
                            onLocationFound = {
                                launch(Dispatchers.IO) {
                                    val loc = it?.location ?: "-"
                                    printWarning("LocationWorker Updating with location: ${loc} at item $index")
                                    database.getMediaDao().setLocation(item.id, loc)

//                                database.getMediaDao().updateMedia(
//                                    item.copy(
//                                        location = loc
//                                    )
////                                    if (it?.location == null || it.location.isEmpty())
////                                        item.copy(
////                                            analyzed = 1
////                                        )
////                                    else
////                                        item.copy(
////                                            analyzed = 1,
////                                            location = it.location
////                                        )
//                                )
                                }
                            }
                        )

                    } catch (e: Exception) {
                        println("LocationWorker Error ${e.stackTraceToString()} at item $index")
                        return@fastForEachIndexed
                    }

                }
            }
            setProgress(workDataOf("progress" to 100f))
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL, NotificationManagerCompat.IMPORTANCE_HIGH)
            //.setName(applicationContext.getText(R.string.analysis_channel_name))
            .setName("Analyzer Location channel name")
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



//fun WorkManager.startAnalysis(indexStart: Int = 0, size: Int = 50) {
//    val inputData = Data.Builder()
//        .putInt("chunkIndexStart", indexStart)
//        .putInt("chunkSize", size)
//        .build()
//
//    val uniqueWork = OneTimeWorkRequestBuilder<AnalyzerWorker>()
//        .addTag(WORKERNAME)
//        .setConstraints(
//            Constraints.Builder()
//                .setRequiresStorageNotLow(true)
//                .build()
//        )
//        .setInputData(inputData)
//        .build()
//
//    enqueueUniqueWork(
//        WORKERNAME,
//        ExistingWorkPolicy.REPLACE,
//        uniqueWork
//    )
//}
//
//fun WorkManager.stopAnalysis() {
//    cancelAllWorkByTag(WORKERNAME)
//    //cancelAllWork()
//}