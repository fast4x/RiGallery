/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: 2025 Fast4x
 * SPDX-License-Identifier: GPL-3.0 license
 */

package it.fast4x.rigallery.feature_node.presentation.common

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.fast4x.rigallery.core.Constants
import it.fast4x.rigallery.core.Resource
import it.fast4x.rigallery.core.Settings
import it.fast4x.rigallery.feature_node.domain.model.IgnoredAlbum
import it.fast4x.rigallery.feature_node.domain.model.Media
import it.fast4x.rigallery.feature_node.domain.model.Media.UriMedia
import it.fast4x.rigallery.feature_node.domain.model.MediaState
import it.fast4x.rigallery.feature_node.domain.model.Vault
import it.fast4x.rigallery.feature_node.domain.model.VaultState
import it.fast4x.rigallery.feature_node.domain.repository.MediaRepository
import it.fast4x.rigallery.feature_node.domain.use_case.MediaHandleUseCase
import it.fast4x.rigallery.feature_node.presentation.util.collectMedia
import it.fast4x.rigallery.feature_node.presentation.util.mapMediaToItem
import it.fast4x.rigallery.feature_node.presentation.util.mediaFlow
import it.fast4x.rigallery.feature_node.presentation.util.update
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Settings.Misc.TIMELINE_GROUP_BY_MONTH
import it.fast4x.rigallery.core.enums.Languages
import it.fast4x.rigallery.core.enums.MediaType
import it.fast4x.rigallery.feature_node.domain.util.isAudio
import it.fast4x.rigallery.feature_node.domain.util.isFavorite
import it.fast4x.rigallery.feature_node.domain.util.isImage
import it.fast4x.rigallery.feature_node.domain.util.isVideo
import it.fast4x.rigallery.feature_node.presentation.util.printWarning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
open class MediaViewModel @Inject constructor(
    private val repository: MediaRepository,
    val handler: MediaHandleUseCase,
    @ApplicationContext val context: Context
) : ViewModel() {

    var lastQuery = mutableStateOf("")
    val multiSelectState = mutableStateOf(false)
    private val _searchMediaState = MutableStateFlow(MediaState<Media>())
    val searchMediaState = _searchMediaState.asStateFlow()
    val selectedPhotoState = mutableStateListOf<UriMedia>()

    var albumId: Long = -1L
    var target: String? = null
    var category: String? = null

//    var groupByMonth: Boolean
//        get() = settingsFlow.value?.groupTimelineByMonth ?: false
//        set(value) {
//            viewModelScope.launch(Dispatchers.IO) {
//                settingsFlow.value?.copy(groupTimelineByMonth = value)?.let {
//                    repository.updateTimelineSettings(it)
//                }
//            }
//        }

//    private val settingsFlow = repository.getTimelineSettings()
//        .stateIn(
//            viewModelScope,
//            started = SharingStarted.Eagerly,
//            TimelineSettings()
//        )

    // Get groupByMonth from app settings, not necessary to get from db

    var groupByMonth =
        repository.getSetting(TIMELINE_GROUP_BY_MONTH, true)
            .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val blacklistedAlbums = repository.getBlacklistedAlbums()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val ignoredMediaList = repository.getMediaIgnored()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    internal val mediaWithLocation = repository.getMediaWithLocation()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    internal val dominantColors = repository.getDominantColors()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    internal val mediaWithDominantColor = repository.getMediaWithDominantColor()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val defaultDateFormat =
        repository.getSetting(Settings.Misc.DEFAULT_DATE_FORMAT, Constants.DEFAULT_DATE_FORMAT)
            .stateIn(viewModelScope, SharingStarted.Eagerly, Constants.DEFAULT_DATE_FORMAT)

    private val extendedDateFormat =
        repository.getSetting(Settings.Misc.EXTENDED_DATE_FORMAT, Constants.EXTENDED_DATE_FORMAT)
            .stateIn(viewModelScope, SharingStarted.Eagerly, Constants.EXTENDED_DATE_FORMAT)

    private val weeklyDateFormat =
        repository.getSetting(Settings.Misc.WEEKLY_DATE_FORMAT, Constants.WEEKLY_DATE_FORMAT)
            .stateIn(viewModelScope, SharingStarted.Eagerly, Constants.WEEKLY_DATE_FORMAT)

    val ignoreImages =
        repository.getSetting(Settings.Misc.IGNORE_IMAGES, false)
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val ignoreVideos =
        repository.getSetting(Settings.Misc.IGNORE_VIDEOS, false)
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val mediaType =
        repository.getSetting(Settings.Misc.MEDIATYPE, MediaType.All.ordinal)
            .stateIn(viewModelScope, SharingStarted.Eagerly, MediaType.All.ordinal)

    val languageApp =
        repository.getSetting(Settings.Misc.LANGUAGE_APP, Languages.System.code)
            .stateIn(viewModelScope, SharingStarted.Eagerly, Languages.System.code)

    val checkUpdate =
        repository.getSetting(Settings.Misc.CHECK_UPDATE, false)
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val permissionState = MutableStateFlow(false)

    val mediaFlow by lazy {
        combine(
            repository.mediaFlow(albumId, target),
            groupByMonth,
            blacklistedAlbums,
            combine(
                defaultDateFormat,
                extendedDateFormat,
                weeklyDateFormat
            ) { defaultDateFormat, extendedDateFormat, weeklyDateFormat ->
                Triple(defaultDateFormat, extendedDateFormat, weeklyDateFormat)
            },
            combine (mediaType, ignoredMediaList) { mediaType, ignoredMediaList ->
                Pair(mediaType, ignoredMediaList)
            }

        ) { result, groupedByMonth, blacklistedAlbums, (defaultDateFormat, extendedDateFormat, weeklyDateFormat),
            (mediaType, ignoredMediaList) ->

            if (result is Resource.Error) return@combine MediaState(
                error = result.message ?: "",
                isLoading = false
            )

            val data = (result.data ?: emptyList()).toMutableList().apply {
                removeAll { media -> blacklistedAlbums.any { it.shouldIgnore(media) } }
                removeAll { media -> media.id == ignoredMediaList.find { it.id == media.id }?.id }
                if (mediaType == MediaType.Video.ordinal) removeAll { media -> media.isImage || media.isAudio }
                if (mediaType == MediaType.Images.ordinal) removeAll { media -> media.isVideo || media.isAudio }
                //if (mediaType == MediaType.Audios.ordinal) removeAll { media -> media.isVideo || media.isImage }
            }

            updateDatabase()
            mapMediaToItem(
                data = data,
                error = result.message ?: "",
                albumId = albumId,
                groupByMonth = groupedByMonth,
                defaultDateFormat = defaultDateFormat,
                extendedDateFormat = extendedDateFormat,
                weeklyDateFormat = weeklyDateFormat
            )
        }.stateIn(viewModelScope, started = SharingStarted.Eagerly, MediaState())
    }

    val ignoredMediaFlow by lazy {
        combine(
            groupByMonth,
            blacklistedAlbums,
            combine(
                defaultDateFormat,
                extendedDateFormat,
                weeklyDateFormat
            ) { defaultDateFormat, extendedDateFormat, weeklyDateFormat ->
                Triple(defaultDateFormat, extendedDateFormat, weeklyDateFormat)
            },
           ignoredMediaList,mediaType
        ) {
          groupedByMonth, blacklistedAlbums, (defaultDateFormat, extendedDateFormat, weeklyDateFormat),
            ignoredMediaList, mediaType ->

            println("ignoredMediaFlow: $ignoredMediaList")
            val data = ignoredMediaList.toMutableList().apply {
                if (mediaType == MediaType.Video.ordinal) removeAll { media -> media.isImage }
                if (mediaType == MediaType.Images.ordinal) removeAll { media -> media.isVideo }
            }

            updateDatabase()
            mapMediaToItem(
                data = data,
                error = "",
                albumId = albumId,
                groupByMonth = groupedByMonth,
                defaultDateFormat = defaultDateFormat,
                extendedDateFormat = extendedDateFormat,
                weeklyDateFormat = weeklyDateFormat
            )
        }.stateIn(viewModelScope, started = SharingStarted.Eagerly, MediaState())
    }

    val vaultsFlow = repository.getVaults()
        .map { it.data ?: emptyList() }
        .map { VaultState(it, isLoading = false) }
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, VaultState())

    private sealed class Event {
        data object UpdateDatabase : Event()
    }

    private val updater = Channel<Event>()

    @Composable
    fun CollectDatabaseUpdates() {
        LaunchedEffect(Unit) {
            viewModelScope.launch(Dispatchers.IO) {
                updater.receiveAsFlow().collectLatest {
                    when (it) {
                        is Event.UpdateDatabase -> {
                            repository.updateInternalDatabase()
                        }
                    }
                }
            }
        }
    }

    fun updatePermissionState(hasPermission: Boolean) {
        permissionState.tryEmit(hasPermission)
    }

    private fun updateDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            updater.send(Event.UpdateDatabase)
        }
    }

    fun <T : Media> addMedia(vault: Vault, media: T) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMedia(vault, media)
        }
    }

    fun clearQuery() {
        queryMedia("")
    }


    fun toggleFavorite(
        result: ActivityResultLauncher<IntentSenderRequest>,
        mediaList: List<UriMedia>,
        favorite: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            handler.toggleFavorite(result, mediaList, favorite)
        }
    }

    fun toggleSelection(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = mediaFlow.value.media[index]
            val selectedPhoto = selectedPhotoState.find { it.id == item.id }
            if (selectedPhoto != null) {
                selectedPhotoState.remove(selectedPhoto)
            } else {
                selectedPhotoState.add(item)
            }
            multiSelectState.update(selectedPhotoState.isNotEmpty())
        }
    }

    fun toggleIgnoredSelection(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = ignoredMediaFlow.value.media[index]
            val selectedPhoto = selectedPhotoState.find { it.id == item.id }
            if (selectedPhoto != null) {
                selectedPhotoState.remove(selectedPhoto)
            } else {
                selectedPhotoState.add(item)
            }
            multiSelectState.update(selectedPhotoState.isNotEmpty())
        }
    }

    fun queryMedia(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                lastQuery.value = query
            }
            if (query.isEmpty()) {
                _searchMediaState.tryEmit(MediaState(isLoading = false))
                return@launch
            } else {
                _searchMediaState.tryEmit(MediaState(isLoading = true))
                val tag = if (query.startsWith("#")) query.substringAfter("#").lowercase() else ""
                val tagFiltersNot = query.lowercase().split("!#")
                    .dropWhile { it.isEmpty() || it.startsWith("!") || it.startsWith("#") }
                val tagFilters = query.lowercase().split("#").filterNot {
                    it in tagFiltersNot
                }.dropWhile { it.isEmpty() || it.startsWith("!") || it.startsWith("#") }
                _searchMediaState.collectMedia(
                    data = if (tag.isEmpty()) mediaFlow.value.media.parseQuery(query)
                    else mediaFlow.value.media.filterMedia(tagFilters),
                    error = mediaFlow.value.error,
                    albumId = albumId,
                    groupByMonth = groupByMonth.value,
                    defaultDateFormat = defaultDateFormat.value,
                    extendedDateFormat = extendedDateFormat.value,
                    weeklyDateFormat = weeklyDateFormat.value
                )
            }
        }
    }

    private suspend fun <T : Media> List<T>.parseQuery(query: String): List<T> {
        return withContext(Dispatchers.IO) {
            if (query.isEmpty())
                return@withContext emptyList()
            // TODO remove fuzzy searc
//            val matches =
//                FuzzySearch.extractSorted(query, this@parseQuery, { it.toString() }, 80)
//            return@withContext matches.map { it.referent }.ifEmpty { emptyList() }

            return@withContext this@parseQuery.filter {
                it.toString().contains(query, ignoreCase = true)
            }
        }
    }

    private suspend fun <T: Media>parseTags(t: T, tags: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            ((t.isImage && context.getString(R.string.tag_image).toString() in tags) ||
                    (t.isVideo && context.getString(R.string.tag_video).toString() in tags) ||
                    (t.isFavorite && context.getString(R.string.tag_favorite).toString() in tags))
        }

    }

    private suspend fun <T : Media> List<T>.filterMedia(
        tags: List<String>
    ): List<T> {
        println("MediaViewModel filterMedia tags: $tags")
//        println("MediaViewModel filterMedia color Red ${Color.Red.toArgb()}")
//        println("MediaViewModel filterMedia color Black ${Color.Black.toArgb()}")
//        println("MediaViewModel filterMedia color Magenta ${Color.Magenta.toArgb()}")
//        println("MediaViewModel filterMedia color Blue ${Color.Blue.toArgb()}")
//        println("MediaViewModel filterMedia color Green ${Color.Green.toArgb()}")
//        println("MediaViewModel filterMedia color Yellow ${Color.Yellow.toArgb()}")
//        println("MediaViewModel filterMedia color Cyan ${Color.Cyan.toArgb()}")
//        println("MediaViewModel filterMedia color White ${Color.White.toArgb()}")
//        println("MediaViewModel filterMedia color Gray ${Color.Gray.toArgb()}")
//        println("MediaViewModel filterMedia color DarkGray ${Color.DarkGray.toArgb()}")
//        println("MediaViewModel filterMedia color LightGray ${Color.LightGray.toArgb()}")

        return withContext(Dispatchers.IO) {

            return@withContext this@filterMedia.filter { it ->
                val dt = Instant.ofEpochSecond(it.definedTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                val d = Instant.ofEpochSecond(it.definedTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val today = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()

                val isColorTag = tags.fastMap {
                    it.lowercase().startsWith(context.getString(R.string.tag_color))
                }.firstOrNull()

                var colorTagValue = tags.firstOrNull() {
                        it.lowercase().startsWith(context.getString(R.string.tag_color))
                    }.toString().substringAfter(":").trim().take(Constants.colorAccurance)

                val dominantColor = if (isColorTag == true) {
                    mediaWithDominantColor.value.find {media -> media.id == it.id }?.dominantColor.toString()
                } else {
                    null
                }

                //printWarning("MediaViewModel: isColorTag $isColorTag colorTagValue $colorTagValue dominantColor ${dominantColor.toString()} match ${dominantColor.toString().startsWith(colorTagValue)}")
                printWarning("MediaViewModel: mimeType ${it.mimeType}")


                (it.isImage && context.getString(R.string.tag_image).toString() in tags) ||
                (it.isVideo && context.getString(R.string.tag_video).toString() in tags) ||
                (it.isFavorite && context.getString(R.string.tag_favorite).toString() in tags) ||

                (it.orientation == 90 && context.getString(R.string.tag_rotated90).toString() in tags) ||
                (it.orientation == 180 && context.getString(R.string.tag_rotated180).toString() in tags) ||
                (it.orientation == 270 && context.getString(R.string.tag_rotated270).toString() in tags) ||

                ((it.width ?: 0) > (it.height ?: 0) && context.getString(R.string.tag_horizontal).toString() in tags) ||
                ((it.width ?: 0) < (it.height ?: 0) && context.getString(R.string.tag_vertical).toString() in tags) ||

                (it.id in mediaWithLocation.value.fastMap { it.id } && context.getString(R.string.tag_withlocation).toString() in tags) ||
                (it.id !in mediaWithLocation.value.fastMap { it.id } && context.getString(R.string.tag_withoutlocation).toString() in tags) ||
                (it.id in mediaWithLocation.value.fastMap { it.id } && "${context.getString(R.string.tag_country).lowercase()}:${mediaWithLocation.value.find { m -> m.id == it.id }?.location?.substringAfter(",")?.trim()?.lowercase() }".toString() in tags) ||
                (it.id in mediaWithLocation.value.fastMap { it.id } && "${context.getString(R.string.tag_locality).lowercase()}:${mediaWithLocation.value.find { m -> m.id == it.id }?.location?.substringBefore(",")?.trim()?.lowercase() }".toString() in tags) ||

                ("${context.getString(R.string.tag_album).lowercase()}:${it.albumLabel.lowercase()}".toString() in tags) ||

                (dt.monthValue == 1 && context.getString(R.string.tag_january).toString() in tags) ||
                (dt.monthValue == 2 && context.getString(R.string.tag_february).toString() in tags) ||
                (dt.monthValue == 3 && context.getString(R.string.tag_march).toString() in tags) ||
                (dt.monthValue == 4 && context.getString(R.string.tag_april).toString() in tags) ||
                (dt.monthValue == 5 && context.getString(R.string.tag_may).toString() in tags) ||
                (dt.monthValue == 6 && context.getString(R.string.tag_june).toString() in tags) ||
                (dt.monthValue == 7 && context.getString(R.string.tag_july).toString() in tags) ||
                (dt.monthValue == 8 && context.getString(R.string.tag_august).toString() in tags) ||
                (dt.monthValue == 9 && context.getString(R.string.tag_september).toString() in tags) ||
                (dt.monthValue == 10 && context.getString(R.string.tag_october).toString() in tags) ||
                (dt.monthValue == 11 && context.getString(R.string.tag_november).toString() in tags) ||
                (dt.monthValue == 12 && context.getString(R.string.tag_december).toString() in tags) ||

                (dt.dayOfWeek == DayOfWeek.MONDAY && context.getString(R.string.tag_monday).toString() in tags) ||
                (dt.dayOfWeek == DayOfWeek.TUESDAY && context.getString(R.string.tag_tuesday).toString() in tags) ||
                (dt.dayOfWeek == DayOfWeek.WEDNESDAY && context.getString(R.string.tag_wednesday).toString() in tags) ||
                (dt.dayOfWeek == DayOfWeek.THURSDAY && context.getString(R.string.tag_thursday).toString() in tags) ||
                (dt.dayOfWeek == DayOfWeek.FRIDAY && context.getString(R.string.tag_friday).toString() in tags) ||
                (dt.dayOfWeek == DayOfWeek.SATURDAY && context.getString(R.string.tag_saturday).toString() in tags) ||
                (dt.dayOfWeek == DayOfWeek.SUNDAY && context.getString(R.string.tag_sunday).toString() in tags)  ||
                (d == today && context.getString(R.string.tag_today).toString() in tags) ||
                (d == today.minusDays(1) && context.getString(R.string.tag_yesterday).toString() in tags) ||

                ("${context.getString(R.string.tag_year).lowercase()}:${dt.year}".toString() in tags) ||

                (it.id in mediaWithDominantColor.value.fastMap { it.id }  && isColorTag == true && dominantColor.toString().startsWith(colorTagValue))


            }
        }

    }

    private fun IgnoredAlbum.shouldIgnore(media: Media) =
        matchesMedia(media) && (hiddenInTimeline && albumId == -1L || hiddenInAlbums && albumId != -1L)


}

