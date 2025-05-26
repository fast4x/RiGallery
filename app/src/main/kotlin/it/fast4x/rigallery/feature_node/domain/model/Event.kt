package it.fast4x.rigallery.feature_node.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events_table")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val mediaId: Long,
    val timestamp: Long,
)