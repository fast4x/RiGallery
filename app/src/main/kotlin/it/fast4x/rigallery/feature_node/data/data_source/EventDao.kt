/*
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.feature_node.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import it.fast4x.rigallery.feature_node.domain.model.Event
import it.fast4x.rigallery.feature_node.domain.model.Media
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT COUNT(id) FROM events_table")
    suspend fun getEventsCount(): Long

    @Query("SELECT * FROM events_table WHERE mediaId = :mediaId")
    suspend fun getEvent(mediaId: Long): Event?

    @Query("SELECT * FROM events_table ORDER BY timestamp DESC LIMIT :limit")
    fun getTopEvents(limit: Int): Flow<List<Event>>

    @Query("SELECT * FROM media WHERE id IN (SELECT DISTINCT mediaId FROM events_table) ORDER BY timestamp DESC LIMIT :limit")
    fun getTopMedia(limit: Int): Flow<List<Media.UriMedia>>

    @Query("SELECT EXISTS(SELECT * FROM events_table WHERE mediaId = :mediaId AND (timestamp / 86400000) = (:todayTimestamp / 86400000) LIMIT 1)")
    suspend fun isEventRegistered(todayTimestamp: Long, mediaId: Long): Boolean

    @Query("DELETE FROM events_table")
    suspend fun deleteAllEvents()

    @Insert
    suspend fun insertEvent(event: Event)

    @Delete
    suspend fun removeEvent(event: Event)


}