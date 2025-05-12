/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0
 */

package it.fast4x.rigallery.feature_node.data.data_source

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.fast4x.rigallery.feature_node.domain.model.IgnoredAlbum
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.MediaVersion
import it.fast4x.rigallery.feature_node.domain.model.PinnedAlbum
import it.fast4x.rigallery.feature_node.domain.model.TimelineSettings
import it.fast4x.rigallery.feature_node.domain.model.Vault
import it.fast4x.rigallery.feature_node.domain.util.Converters

@Database(
    entities = [
        PinnedAlbum::class,
        IgnoredAlbum::class,
        Media.UriMedia::class,
        MediaVersion::class,
        TimelineSettings::class,
        Media.ClassifiedMedia::class,
        Media.EncryptedMedia2::class,
        Vault::class
    ],
    version = 10,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
    ]
)
@TypeConverters(Converters::class)
abstract class InternalDatabase : RoomDatabase() {

    abstract fun getPinnedDao(): PinnedDao

    abstract fun getBlacklistDao(): BlacklistDao

    abstract fun getMediaDao(): MediaDao

    abstract fun getClassifierDao(): ClassifierDao

    abstract fun getVaultDao(): VaultDao

    companion object {
        const val NAME = "internal_db"
    }
}