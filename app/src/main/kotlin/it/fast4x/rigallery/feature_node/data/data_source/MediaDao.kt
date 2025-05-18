package it.fast4x.rigallery.feature_node.data.data_source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import it.fast4x.rigallery.feature_node.domain.model.Media.UriMedia
import it.fast4x.rigallery.feature_node.domain.model.MediaVersion
import it.fast4x.rigallery.feature_node.domain.model.TimelineSettings
import it.fast4x.rigallery.feature_node.presentation.picker.AllowedMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    /** UriMedia */
    @Query("SELECT * FROM media ORDER BY timestamp DESC")
    suspend fun getMedia(): List<UriMedia>

    @Query("SELECT * FROM media WHERE mimeType LIKE :allowedMedia ORDER BY timestamp DESC")
    suspend fun getMediaByType(allowedMedia: AllowedMedia): List<UriMedia>

    @Query("SELECT * FROM media WHERE favorite = 1 ORDER BY timestamp DESC")
    suspend fun getFavorites(): List<UriMedia>

    @Query("SELECT * FROM media WHERE ignored = 1 ORDER BY timestamp DESC")
    fun getMediaIgnored(): Flow<List<UriMedia>>

    @Query("SELECT * FROM media WHERE location != '' ORDER BY timestamp DESC")
    fun getMediaWithLocation(): Flow<List<UriMedia>>

    @Query("SELECT * FROM media WHERE dominantColor != 0 ORDER BY timestamp DESC")
    fun getMediaWithDominantColor(): Flow<List<UriMedia>>

    @Query("SELECT * FROM media WHERE dominantColor = -65536 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorRed(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -16711936 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorGreen(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -16776961 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorBlue(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -256 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorYellow(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -16711681 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorCyan(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -1 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorWhite(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -7829368 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorGray(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -12303292 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorDarkGray(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -3355444 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorLightGray(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -16777216 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorBlack(): Flow<List<UriMedia>>
    @Query("SELECT * FROM media WHERE dominantColor = -65281 ORDER BY timestamp DESC")
    fun getMediaWithDominantColorMagenta(): Flow<List<UriMedia>>










    @Query("SELECT * FROM media WHERE id = :id LIMIT 1")
    suspend fun getMediaById(id: Long): UriMedia

    @Query("SELECT * FROM media WHERE albumID = :albumId ORDER BY timestamp DESC")
    suspend fun getMediaByAlbumId(albumId: Long): List<UriMedia>

    @Query("SELECT * FROM media WHERE albumID = :albumId AND mimeType LIKE :allowedMedia ORDER BY timestamp DESC")
    suspend fun getMediaByAlbumIdAndType(albumId: Long, allowedMedia: AllowedMedia): List<UriMedia>

    @Upsert(entity = UriMedia::class)
    suspend fun addMediaList(mediaList: List<UriMedia>)

    @Transaction
    suspend fun updateMedia(mediaList: List<UriMedia>) {
        // Upsert the items in mediaList
        addMediaList(mediaList)

        // Get the IDs of the media items in mediaList
        val mediaIds = mediaList.map { it.id }

        // Delete items from the database that are not in mediaList
        deleteMediaNotInList(mediaIds)
    }

    @Query("DELETE FROM media WHERE id NOT IN (:mediaIds)")
    suspend fun deleteMediaNotInList(mediaIds: List<Long>)

    /** MediaVersion */
    @Upsert(entity = MediaVersion::class)
    suspend fun setMediaVersion(version: MediaVersion)

    @Query("SELECT EXISTS(SELECT * FROM media_version WHERE version = :version) LIMIT 1")
    suspend fun isMediaVersionUpToDate(version: String): Boolean

    /** Timeline Settings */
    @Query("SELECT * FROM timeline_settings LIMIT 1")
    fun getTimelineSettings(): Flow<TimelineSettings?>

    @Upsert(entity = TimelineSettings::class)
    suspend fun setTimelineSettings(settings: TimelineSettings)

    @Query("UPDATE media SET ignored = :ignored WHERE id = :id")
    suspend fun setMediaIgnored(id: Long, ignored: Int)

    @Upsert
    suspend fun updateMedia(media: UriMedia)

    @Query("UPDATE media SET analyzed = 1, dominantColor = :dominantColor WHERE id = :id")
    suspend fun setDominantColor(id: Long, dominantColor: Int)

    @Query("UPDATE media SET analyzed = 1, location = :location WHERE id = :id")
    suspend fun setLocation(id: Long, location: String)

    @Query("UPDATE media SET analyzed = 0, location = '', dominantColor = 0 WHERE analyzed = 1")
    suspend fun resetAnalizedMedia()

    @Query("SELECT COUNT(*) FROM media WHERE analyzed = 1")
    fun getAnalyzedMediaCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM media WHERE analyzed = 0")
    fun getNotAnalyzedMediaCount(): Flow<Int>

}