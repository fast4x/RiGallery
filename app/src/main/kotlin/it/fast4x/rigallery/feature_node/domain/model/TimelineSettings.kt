package it.fast4x.rigallery.feature_node.domain.model

import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.fast4x.rigallery.feature_node.domain.util.MediaOrder
import it.fast4x.rigallery.feature_node.domain.util.OrderType

/**
 * Timeline settings
 *
 * This entity contains all settings of the app that
 * affects the media display.
 */
@Stable
@Entity(tableName = "timeline_settings")
data class TimelineSettings(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val groupTimelineByMonth: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val groupTimelineInAlbums: Boolean = false,
    @ColumnInfo(defaultValue = "{\"orderType\":{\"type\":\"it.fast4x.rigallery.feature_node.domain.util.OrderType.Descending\"},\"orderType_date\":{\"type\":\"it.fast4x.rigallery.feature_node.domain.util.OrderType.Descending\"}}")
    val timelineMediaOrder: MediaOrder = MediaOrder.Date(OrderType.Descending),
    @ColumnInfo(defaultValue = "{\"orderType\":{\"type\":\"it.fast4x.rigallery.feature_node.domain.util.OrderType.Descending\"},\"orderType_date\":{\"type\":\"it.fast4x.rigallery.feature_node.domain.util.OrderType.Descending\"}}")
    val albumMediaOrder: MediaOrder = MediaOrder.Date(OrderType.Descending),
)
