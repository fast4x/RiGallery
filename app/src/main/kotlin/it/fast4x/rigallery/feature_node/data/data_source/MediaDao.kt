package it.fast4x.rigallery.feature_node.data.data_source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import it.fast4x.rigallery.feature_node.domain.model.Media.UriMedia
import it.fast4x.rigallery.feature_node.domain.model.MediaVersion
import it.fast4x.rigallery.feature_node.domain.model.TimelineSettings
import it.fast4x.rigallery.feature_node.presentation.picker.AllowedMedia
import it.fast4x.rigallery.feature_node.presentation.statistics.data.MediaInYear
import it.fast4x.rigallery.feature_node.presentation.statistics.data.MediaType
import it.fast4x.rigallery.feature_node.presentation.statistics.data.MediaTypeInYear
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

    @Query("SELECT * FROM media WHERE location IS NOT NULL AND location <> '-' ORDER BY timestamp DESC")
    fun getMediaWithLocation(): Flow<List<UriMedia>>

    @Query("SELECT * FROM media WHERE dominantColor IS NOT NULL ORDER BY dominantColor")
    fun getMediaWithDominantColor(): Flow<List<UriMedia>>

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

    @Query("UPDATE media SET dominantColor = :dominantColor WHERE id = :id")
    suspend fun setDominantColor(id: Long, dominantColor: Int)

    @Query("UPDATE media SET location = :location WHERE id = :id")
    suspend fun setLocation(id: Long, location: String)

    @Query("UPDATE media SET location = NULL, dominantColor = NULL")
    suspend fun resetAnalizedMedia()

    @Query("UPDATE media SET location = NULL")
    suspend fun resetAnalizedMediaForLocation()

    @Query("UPDATE media SET dominantColor = NULL")
    suspend fun resetAnalizedMediaForDominantColor()

    @Query("SELECT COUNT(*) FROM media WHERE analyzed = 1")
    fun getAnalyzedMediaCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM media WHERE analyzed = 0")
    fun getNotAnalyzedMediaCount(): Flow<Int>

    @Query("SELECT * FROM media WHERE location IS NULL ORDER BY timestamp DESC")
    suspend fun getMediaToAnalyzeLocation(): List<UriMedia>

    @Query("SELECT * FROM media WHERE dominantColor IS NULL ORDER BY timestamp DESC")
    suspend fun getMediaToAnalyzeDominantColor(): List<UriMedia>

    @Query("SELECT DISTINCT dominantColor FROM media WHERE dominantColor IS NOT NULL ORDER BY dominantColor DESC")
    fun getDominantColors(): Flow<List<Int>>


    @Query("SELECT COUNT(id) FROM media")
    fun getMediaCount(): Flow<Int>

    @Query("SELECT COUNT(id) FROM media WHERE favorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("SELECT COUNT(id) FROM media WHERE trashed = 1")
    fun getTrashedCount(): Flow<Int>

    @Query("SELECT COUNT(id) FROM media WHERE ignored = 1")
    fun getIgnoredCount(): Flow<Int>

    @Query("SELECT COUNT(id) FROM media WHERE location IS NOT NULL AND location <> '-'")
    fun getWithLocationCount(): Flow<Int>

    @Query("SELECT DISTINCT CAST(strftime('%Y',takenTimestamp / 1000,'unixepoch') as INTEGER) FROM media WHERE timestamp IS NOT NULL")
    fun getYears(): Flow<List<Int>>

    @Query("SELECT COUNT(id) FROM media WHERE CAST(strftime('%Y',takenTimestamp / 1000,'unixepoch') as INTEGER) = :year")
    fun getMediaCountByYear(year: Int): Flow<Int>

    @Query("SELECT CAST(strftime('%Y',takenTimestamp / 1000,'unixepoch') as INTEGER) AS year, COUNT(id) as value  FROM media " +
            "WHERE takenTimestamp IS NOT NULL " +
            "GROUP BY year ORDER BY year DESC")
    fun getMediaCountByYears(): Flow<List<MediaInYear>>

    @Query("SELECT CAST(strftime('%Y',takenTimestamp / 1000,'unixepoch') as INTEGER) AS year, " +
            "COUNT(CASE WHEN mimeType LIKE 'video/' || '%' THEN 1 END) AS videos, " +
            "COUNT(CASE WHEN mimeType LIKE 'image/' || '%' THEN 1 END) AS images " +
            "FROM media " +
            "WHERE takenTimestamp IS NOT NULL " +
            "GROUP BY year " +
            "ORDER BY year DESC")
    fun getMediaTypeCountByYears(): Flow<List<MediaTypeInYear>>

    @Query("SELECT COUNT(CASE WHEN mimeType LIKE 'video/' || '%' THEN 1 END) AS videos, " +
            "COUNT(CASE WHEN mimeType LIKE 'image/' || '%' THEN 1 END) AS images " +
            "FROM media " +
            "WHERE takenTimestamp IS NOT NULL ")
    fun getMediaTypeCount(): Flow<MediaType>

    @Query("SELECT COUNT(id) from media WHERE mimeType LIKE 'video/' || '%'")
    fun getVideosCount(): Flow<Int>

    @Query("SELECT COUNT(id) from media WHERE mimeType LIKE 'image/' || '%'")
    fun getImagesCount(): Flow<Int>

    @Query("SELECT COUNT(id) from media WHERE mimeType LIKE 'audio/' || '%'")
    fun getAudiosCount(): Flow<Int>

    @Query("SELECT DISTINCT SUBSTR(mimeType, INSTR(mimeType, '/')+1, LENGTH(mimeType)-INSTR(mimeType, '/')+1) FROM media WHERE mimeType IS NOT NULL")
    fun getMediaTypes(): Flow<List<String>>

    @Query("SELECT COUNT(id) from media WHERE mimeType LIKE '%' || :type || '%'")
    fun getMediaCountByType(type: String): Flow<Int>

}