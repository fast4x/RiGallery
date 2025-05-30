package it.fast4x.rigallery.feature_node.data.data_source.mediastore

import android.net.Uri
import android.provider.MediaStore
import it.fast4x.rigallery.core.util.eq
import it.fast4x.rigallery.core.util.or

object MediaQuery {
    val MediaStoreFileUri: Uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
    val MediaProjection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.RELATIVE_PATH,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.BUCKET_ID,
        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Files.FileColumns.DATE_TAKEN,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.DURATION,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.IS_FAVORITE,
        MediaStore.Files.FileColumns.IS_TRASHED,
        MediaStore.Files.FileColumns.DATE_EXPIRES,
        MediaStore.Files.FileColumns.HEIGHT,
        MediaStore.Files.FileColumns.WIDTH,
        MediaStore.Files.FileColumns.ORIENTATION,
    )
    val AlbumsProjection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.RELATIVE_PATH,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.BUCKET_ID,
        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Files.FileColumns.DATE_TAKEN,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MIME_TYPE,
    )

    object Selection {
        val image =
            MediaStore.Files.FileColumns.MEDIA_TYPE eq MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
        val video =
            MediaStore.Files.FileColumns.MEDIA_TYPE eq MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
        val imageOrVideo = image or video
        val audio =
            MediaStore.Files.FileColumns.MEDIA_TYPE eq MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO
    }
}