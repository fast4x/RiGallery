package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.enums.DayTagsType
import it.fast4x.rigallery.core.enums.MetadataTagsType
import it.fast4x.rigallery.core.enums.MonthTagsType
import it.fast4x.rigallery.core.enums.TagsType
import it.fast4x.rigallery.feature_node.domain.model.Album

@Composable
fun TagsGrid(
    searchTag: (String) -> Unit,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit
){
    val rows = if (expanded) 3 else 1
    val baseHeight = 60.dp
    val height = if (expanded) baseHeight*rows else baseHeight

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(height)
    ) {
        items(TagsType.entries.size) { index ->
            val tag = TagsType.entries[index].tag

            var tagInAction = remember { mutableStateOf("") }
            var showTagAction = remember { mutableStateOf(false) }

            if (showTagAction.value) {
                TagAction(
                    tag = tag,
                    onSearch = { searchTag(tagInAction.value) },
                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
                    onCombine = { addSearchTag(tagInAction.value, false) },
                    onDismiss = { showTagAction.value = false }
                )
            }

            FilterChip(
                selected = true,
                onClick = {
                    tagInAction.value = tag
                    showTagAction.value = !showTagAction.value
                },
                label = { Text(text = tag) },
                leadingIcon = {
                    Icon(
                        imageVector = TagsType.entries[index].icon,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = Color.Transparent,
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(2.dp, TagsType.entries[index].color, MaterialTheme.shapes.large)
                    .background(Color.Transparent, MaterialTheme.shapes.large)
                    .animateItem()
            )
        }
    }
}


@Composable
fun RecentTagsGrid(
    searchTag: (String) -> Unit,
    addSearchTag: (String, Boolean) -> Unit,
    historyTagsItems: SnapshotStateList<Pair<String, String>>,
    expanded: Boolean = false
){
    val rows = if (expanded && historyTagsItems.size > 3) 3 else 1
    val baseHeight = 60.dp
    val height = if (expanded && historyTagsItems.size > 3) baseHeight*rows else baseHeight

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(height)
    ) {
        items(historyTagsItems.size) { index ->
            val tag = historyTagsItems[index].second
            val tagType = TagsType.entries.find { it.tag == tag }

            var tagInAction = remember { mutableStateOf("") }
            var showTagAction = remember { mutableStateOf(false) }

            if (showTagAction.value) {
                TagAction(
                    tag = tag,
                    onSearch = { searchTag(tagInAction.value) },
                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
                    onCombine = { addSearchTag(tagInAction.value, false) },
                    onDismiss = { showTagAction.value = false }
                )
            }

            FilterChip(
                selected = true,
                onClick = {
//                    tagInAction.value = tag
//                    showTagAction.value = !showTagAction.value
                    searchTag(tag)
                },
                label = { Text(text = tag) },
                leadingIcon = {
                    Icon(
                        imageVector = tagType?.icon ?: Icons.Default.Tag,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = Color.Transparent,
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        tagType?.color ?: MaterialTheme.colorScheme.primary,
                        MaterialTheme.shapes.large
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.large)
                    .animateItem()
            )
        }
    }
}

@Composable
fun MetadataTagsGrid(
    searchTag: (String) -> Unit,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit
){
    val rows = 1 //if (expanded) 3 else 1
    val baseHeight = 60.dp
    val height =  baseHeight //if (expanded) baseHeight*rows else baseHeight

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(height)
    ) {
        items(MetadataTagsType.entries.size) { index ->
            val tag = MetadataTagsType.entries[index].tag

            var tagInAction = remember { mutableStateOf("") }
            var showTagAction = remember { mutableStateOf(false) }

            if (showTagAction.value) {
                TagAction(
                    tag = tag,
                    onSearch = { searchTag(tagInAction.value) },
                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
                    onCombine = { addSearchTag(tagInAction.value, false) },
                    onDismiss = { showTagAction.value = false }
                )
            }

            FilterChip(
                selected = true,
                onClick = {
                    tagInAction.value = tag
                    showTagAction.value = !showTagAction.value
                },
                label = { Text(text = tag) },
                leadingIcon = {
                    Icon(
                        imageVector = MetadataTagsType.entries[index].icon,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = Color.Transparent,
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MetadataTagsType.entries[index].color,
                        MaterialTheme.shapes.extraLarge
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.extraLarge)
                    .animateItem()
            )
        }
    }
}

@Composable
fun LocationTagsGrid(
    searchTag: (String) -> Unit,
    tagsItems: List<String?>,
    locationIsCountry: Boolean = true,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit
){
    val rows = if (expanded && tagsItems.size > 3) 3 else 1
    val baseHeight = 60.dp
    val height = if (expanded && tagsItems.size > 3) baseHeight*rows else baseHeight
    val tagName = stringResource(if (locationIsCountry) R.string.tag_country else R.string.tag_locality)

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(height)
    ) {
        items(tagsItems.size) { index ->
            val tag = tagsItems[index]

            var tagInAction = remember { mutableStateOf("") }
            var showTagAction = remember { mutableStateOf(false) }

            if (showTagAction.value) {
                TagAction(
                    tag = tag.toString(),
                    onSearch = { searchTag(tagInAction.value) },
                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
                    onCombine = { addSearchTag(tagInAction.value, false) },
                    onDismiss = { showTagAction.value = false }
                )
            }

            FilterChip(
                selected = true,
                onClick = {
                    tagInAction.value = "#$tagName:$tag"
                    showTagAction.value = !showTagAction.value
                },
                label = { Text(text = tag.toString()) },
                leadingIcon = {
                    Icon(
                        imageVector = if (locationIsCountry) Icons.Outlined.LocationOn else Icons.Outlined.LocationCity,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = Color.Transparent,
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        if (locationIsCountry) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        MaterialTheme.shapes.extraSmall
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.extraSmall)
                    .animateItem()
            )
        }
    }
}

@Composable
fun AlbumsTagsGrid(
    searchTag: (String) -> Unit,
    tagsItems: List<Album>,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit
){
    val rows = if (expanded && tagsItems.size > 3) 3 else 1
    val baseHeight = 60.dp
    val height = if (expanded && tagsItems.size > 3) baseHeight*rows else baseHeight
    val tagName = stringResource(R.string.tag_album)

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(height)
    ) {
        items(tagsItems.size) { index ->
            val tag = tagsItems[index].label
            //val tagLabel = tagsItems[index].label
            var tagInAction = remember { mutableStateOf("") }
            var showTagAction = remember { mutableStateOf(false) }

            if (showTagAction.value) {
                TagAction(
                    tag = tag,
                    onSearch = { searchTag(tagInAction.value) },
                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
                    onCombine = { addSearchTag(tagInAction.value, false) },
                    onDismiss = { showTagAction.value = false }
                )
            }

            FilterChip(
                selected = true,
                onClick = {
                    tagInAction.value = "#$tagName:$tag"
                    showTagAction.value = !showTagAction.value
                },
                label = { Text(text = tag) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Album,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = Color.Transparent,
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.onPrimary,
                        MaterialTheme.shapes.small
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.small)
                    .animateItem()
            )
        }
    }
}

@Composable
fun MonthTagsGrid(
    searchTag: (String) -> Unit,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit
){
    val rows = if (expanded) 3 else 1
    val baseHeight = 60.dp
    val height =  if (expanded) baseHeight*rows else baseHeight

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(height)
    ) {
        items(MonthTagsType.entries.size) { index ->
            val tag = MonthTagsType.entries[index].tag

            var tagInAction = remember { mutableStateOf("") }
            var showTagAction = remember { mutableStateOf(false) }

            if (showTagAction.value) {
                TagAction(
                    tag = tag,
                    onSearch = { searchTag(tagInAction.value) },
                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
                    onCombine = { addSearchTag(tagInAction.value, false) },
                    onDismiss = { showTagAction.value = false }
                )
            }

            FilterChip(
                selected = true,
                onClick = {
                    tagInAction.value = tag
                    showTagAction.value = !showTagAction.value
                },
                label = { Text(text = tag) },
                leadingIcon = {
                    Icon(
                        imageVector = MonthTagsType.entries[index].icon,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = Color.Transparent,
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MonthTagsType.entries[index].color,
                        MaterialTheme.shapes.medium
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.medium)
                    .animateItem()
            )
        }
    }
}

@Composable
fun DayTagsGrid(
    searchTag: (String) -> Unit,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit
){
    val rows = if (expanded) 3 else 1
    val baseHeight = 60.dp
    val height =  if (expanded) baseHeight*rows else baseHeight

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(height)
    ) {
        items(DayTagsType.entries.size) { index ->
            val tag = DayTagsType.entries[index].tag

            var tagInAction = remember { mutableStateOf("") }
            var showTagAction = remember { mutableStateOf(false) }

            if (showTagAction.value) {
                TagAction(
                    tag = tag,
                    onSearch = { searchTag(tagInAction.value) },
                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
                    onCombine = { addSearchTag(tagInAction.value, false) },
                    onDismiss = { showTagAction.value = false }
                )
            }

            FilterChip(
                selected = true,
                onClick = {
                    tagInAction.value = tag
                    showTagAction.value = !showTagAction.value
                },
                label = { Text(text = tag) },
                leadingIcon = {
                    Icon(
                        imageVector = DayTagsType.entries[index].icon,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = Color.Transparent,
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        DayTagsType.entries[index].color,
                        MaterialTheme.shapes.small
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.small)
                    .animateItem()
            )
        }
    }
}

@Composable
fun YearTagsGrid(
    searchTag: (String) -> Unit,
    tagsItems: List<Int>,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit
){
    val rows = if (expanded && tagsItems.size > 3) 3 else 1
    val baseHeight = 60.dp
    val height = if (expanded && tagsItems.size > 3) baseHeight*rows else baseHeight
    val tagName = stringResource(R.string.tag_year)

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(height)
    ) {
        items(tagsItems.size) { index ->
            val tag = tagsItems[index].toString()
            //val tagLabel = tagsItems[index].label

            var tagInAction = remember { mutableStateOf("") }
            var showTagAction = remember { mutableStateOf(false) }

            if (showTagAction.value) {
                TagAction(
                    tag = tag,
                    onSearch = { searchTag(tagInAction.value) },
                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
                    onCombine = { addSearchTag(tagInAction.value, false) },
                    onDismiss = { showTagAction.value = false }
                )
            }

            FilterChip(
                selected = true,
                onClick = {
                    tagInAction.value = "#$tagName:$tag"
                    showTagAction.value = !showTagAction.value
                },
                label = { Text(text = tag) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = Color.Transparent,
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.shapes.medium
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.medium)
                    .animateItem()
            )
        }
    }
}