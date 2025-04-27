/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.domain.use_case

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import it.fast4x.rigallery.core.Settings.Misc.getTrashEnabled
import it.fast4x.rigallery.feature_node.domain.model.ExifAttributes
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.Media.UriMedia
import it.fast4x.rigallery.feature_node.domain.repository.MediaRepository
import it.fast4x.rigallery.feature_node.presentation.util.mediaPair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class MediaHandleUseCase(
    private val repository: MediaRepository,
    private val context: Context
) {

    suspend fun <T: Media> toggleFavorite(
        result: ActivityResultLauncher<IntentSenderRequest>,
        mediaList: List<T>,
        favorite: Boolean
    ) = repository.toggleFavorite(result, mediaList, favorite)

    suspend fun <T: Media> toggleFavorite(
        result: ActivityResultLauncher<IntentSenderRequest>,
        mediaList: List<T>
    ) {
        val turnToFavorite = mediaList.filter { it.favorite == 0 }
        val turnToNotFavorite = mediaList.filter { it.favorite == 1 }
        if (turnToFavorite.isNotEmpty()) {
            repository.toggleFavorite(result, turnToFavorite, true)
        }
        if (turnToNotFavorite.isNotEmpty()) {
            repository.toggleFavorite(result, turnToNotFavorite, false)
        }
    }

    suspend fun <T: Media> toggleIgnored(
        mediaList: List<T>
    ) {
        val turnToIgnored = mediaList.filter { it.ignored == 0 }
        val turnToNotIgnored = mediaList.filter { it.ignored == 1 }
        if (turnToIgnored.isNotEmpty()) {
            turnToIgnored.forEach {
                repository.setMediaIgnored(
                    it.apply {
                        ignored = 1
                    } as UriMedia
                )
                println("MediaHandleUseCase: toggleIgnored Loop: turnToIgnored: $it")
            }
            println("MediaHandleUseCase: toggleIgnored: turnToIgnored: $turnToIgnored")
        }
        if (turnToNotIgnored.isNotEmpty()) {
            turnToNotIgnored.forEach {
                repository.setMediaIgnored(
                    it.apply {
                        ignored = 0
                    } as UriMedia
                )
                println("MediaHandleUseCase: turnToNotIgnored Loop: turnToNotIgnored: $it")
            }
            println("MediaHandleUseCase: toggleIgnored: turnToNotIgnored: $turnToNotIgnored")
        }
    }

    suspend fun <T: Media> trashMedia(
        result: ActivityResultLauncher<IntentSenderRequest>,
        mediaList: List<T>,
        trash: Boolean = true
    ) = withContext(Dispatchers.Default) {
        val isTrashEnabled = getTrashEnabled(context).firstOrNull() ?: true
        /**
         * Trash media only if user enabled the Trash Can
         * Or if user wants to remove existing items from the trash
         * */
        if ((isTrashEnabled || !trash)) {
            val pair = mediaList.mediaPair()
            if (pair.first.isNotEmpty()) {
                repository.trashMedia(result, mediaList, trash)
            }
            if (pair.second.isNotEmpty()) {
                repository.deleteMedia(result, mediaList)
            }
        } else {
            repository.deleteMedia(result, mediaList)
        }
    }

    suspend fun <T: Media> copyMedia(
        from: T,
        path: String
    ): Boolean = repository.copyMedia(from, path)

    suspend fun <T: Media> deleteMedia(
        result: ActivityResultLauncher<IntentSenderRequest>,
        mediaList: List<T>
    ) = repository.deleteMedia(result, mediaList)

    suspend fun <T: Media> renameMedia(
        media: T,
        newName: String
    ): Boolean = repository.renameMedia(media, newName)

    suspend fun <T: Media> moveMedia(
        media: T,
        newPath: String
    ): Boolean = repository.moveMedia(media, newPath)

    suspend fun <T: Media> updateMediaExif(
        media: T,
        exifAttributes: ExifAttributes
    ): Boolean = repository.updateMediaExif(media, exifAttributes)

    fun saveImage(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        mimeType: String,
        relativePath: String,
        displayName: String
    ) = repository.saveImage(bitmap, format, mimeType, relativePath, displayName)

    fun overrideImage(
        uri: Uri,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        mimeType: String,
        relativePath: String,
        displayName: String
    ) = repository.overrideImage(uri, bitmap, format, mimeType, relativePath, displayName)

    suspend fun getCategoryForMediaId(mediaId: Long) = repository.getCategoryForMediaId(mediaId)

    fun getClassifiedMediaCountAtCategory(category: String) = repository.getClassifiedMediaCountAtCategory(category)

    fun getClassifiedMediaThumbnailByCategory(category: String) = repository.getClassifiedMediaThumbnailByCategory(category)

}