package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastDistinctBy
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.Constants
import it.fast4x.rigallery.core.enums.BaseColors
import it.fast4x.rigallery.core.enums.DayTagsType
import it.fast4x.rigallery.core.enums.MetadataTagsType
import it.fast4x.rigallery.core.enums.MonthTagsType
import it.fast4x.rigallery.core.enums.TagsType
import it.fast4x.rigallery.feature_node.domain.model.Album

@Composable
fun TagsGrid(
    searchTag: (String) -> Unit,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit,
    selectedTags: MutableList<String>
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

//            var tagInAction = remember { mutableStateOf("") }
//            var showTagAction = remember { mutableStateOf(false) }
//
//            if (showTagAction.value) {
//                TagAction(
//                    onSearch = { searchTag(tagInAction.value) },
//                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
//                    onCombine = { addSearchTag(tagInAction.value, false) },
//                    onDismiss = { showTagAction.value = false },
//                    tag = tag,
//                    tags = selectedTags,
//                    show = showTagAction
//                )
//            }

            var selected by remember { mutableStateOf(false) }

            FilterChip(
                selected = selected,
                onClick = {
                    //tagInAction.value = tag
                    //showTagAction.value = !showTagAction.value
                    selected = !selected
                    addSearchTag(tag, false)
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
                    disabledSelectedContainerColor = Color.Transparent,
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(2.dp, TagsType.entries[index].color, MaterialTheme.shapes.small)
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

//            var tagInAction = remember { mutableStateOf("") }
//            var showTagAction = remember { mutableStateOf(false) }
//
//            if (showTagAction.value) {
//                TagAction(
//                    onSearch = { searchTag(tagInAction.value) },
//                    onSearchCombined = { addSearchTag(tagInAction.value, true) }, //selectedTags,
//                    onCombine = { addSearchTag(tagInAction.value, false) },
//                    onDismiss = { showTagAction.value = false },
//                    tag = tag,
//                    tags = emptyList<String>() as MutableList<String>,
//                    show = showTagAction
//                )
//            }

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
    addSearchTag: (String, Boolean) -> Unit,
    selectedTags: MutableList<String>
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

//            var tagInAction = remember { mutableStateOf("") }
//            var showTagAction = remember { mutableStateOf(false) }
//
//            if (showTagAction.value) {
//                TagAction(
//                    onSearch = { searchTag(tagInAction.value) },
//                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
//                    onCombine = { addSearchTag(tagInAction.value, false) },
//                    onDismiss = { showTagAction.value = false },
//                    tag = tag,
//                    tags = selectedTags,
//                    show = showTagAction
//                )
//            }

            var selected by remember { mutableStateOf(false) }

            FilterChip(
                selected = selected,
                onClick = {
//                    tagInAction.value = tag
//                    showTagAction.value = !showTagAction.value
                    selected = !selected
                    addSearchTag(tag, false)
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
                    disabledSelectedContainerColor = Color.Transparent,
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MetadataTagsType.entries[index].color,
                        MaterialTheme.shapes.small
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.large)
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
    addSearchTag: (String, Boolean) -> Unit,
    selectedTags: MutableList<String>
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

//            var tagInAction = remember { mutableStateOf("") }
//            var showTagAction = remember { mutableStateOf(false) }
//
//            if (showTagAction.value) {
//                TagAction(
//                    onSearch = { searchTag(tagInAction.value) },
//                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
//                    onCombine = { addSearchTag(tagInAction.value, false) },
//                    onDismiss = { showTagAction.value = false },
//                    tag = tag.toString(),
//                    tags = selectedTags,
//                    show = showTagAction
//                )
//            }

            var selected by remember { mutableStateOf(false) }

            FilterChip(
                selected = selected,
                onClick = {
//                    tagInAction.value = "#$tagName:$tag"
//                    showTagAction.value = !showTagAction.value
                    selected = !selected
                    addSearchTag("#$tagName:$tag", false)
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
                    disabledSelectedContainerColor = Color.Transparent,
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        if (locationIsCountry) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        MaterialTheme.shapes.small
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.large)
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
    addSearchTag: (String, Boolean) -> Unit,
    selectedTags: MutableList<String>
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
//            var tagInAction = remember { mutableStateOf("") }
//            var showTagAction = remember { mutableStateOf(false) }
//
//            if (showTagAction.value) {
//                TagAction(
//                    onSearch = { searchTag(tagInAction.value) },
//                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
//                    onCombine = { addSearchTag(tagInAction.value, false) },
//                    onDismiss = { showTagAction.value = false },
//                    tag = tag,
//                    tags = selectedTags,
//                    show = showTagAction
//                )
//            }

            var selected by remember { mutableStateOf(false) }

            FilterChip(
                selected = selected,
                onClick = {
//                    tagInAction.value = "#$tagName:$tag"
//                    showTagAction.value = !showTagAction.value
                    selected = !selected
                    addSearchTag("#$tagName:$tag", false)
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
                    disabledSelectedContainerColor = Color.Transparent,
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.onPrimary,
                        MaterialTheme.shapes.small
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.large)
                    .animateItem()
            )
        }
    }
}

@Composable
fun MonthTagsGrid(
    searchTag: (String) -> Unit,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit,
    selectedTags: MutableList<String>
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

//            var tagInAction = remember { mutableStateOf("") }
//            var showTagAction = remember { mutableStateOf(false) }
//
//            if (showTagAction.value) {
//                TagAction(
//                    onSearch = { searchTag(tagInAction.value) },
//                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
//                    onCombine = { addSearchTag(tagInAction.value, false) },
//                    onDismiss = { showTagAction.value = false },
//                    tag = tag,
//                    tags = selectedTags,
//                    show = showTagAction
//                )
//            }

            var selected by remember { mutableStateOf(false) }

            FilterChip(
                selected = selected,
                onClick = {
//                    tagInAction.value = tag
//                    showTagAction.value = !showTagAction.value
                    selected = !selected
                    addSearchTag(tag, false)
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
                    disabledSelectedContainerColor = Color.Transparent,
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MonthTagsType.entries[index].color,
                        MaterialTheme.shapes.small
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.large)
                    .animateItem()
            )
        }
    }
}

@Composable
fun DayTagsGrid(
    searchTag: (String) -> Unit,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit,
    selectedTags: MutableList<String>
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

//            var tagInAction = remember { mutableStateOf("") }
//            var showTagAction = remember { mutableStateOf(false) }
//
//            if (showTagAction.value) {
//                TagAction(
//                    onSearch = { searchTag(tagInAction.value) },
//                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
//                    onCombine = { addSearchTag(tagInAction.value, false) },
//                    onDismiss = { showTagAction.value = false },
//                    tag = tag,
//                    tags = selectedTags,
//                    show = showTagAction
//                )
//            }

            var selected by remember { mutableStateOf(false) }

            FilterChip(
                selected = selected,
                onClick = {
//                    tagInAction.value = tag
//                    showTagAction.value = !showTagAction.value
                    selected = !selected
                    addSearchTag(tag, false)
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
                    disabledSelectedContainerColor = Color.Transparent,
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        DayTagsType.entries[index].color,
                        MaterialTheme.shapes.small
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.large)
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
    addSearchTag: (String, Boolean) -> Unit,
    selectedTags: MutableList<String>
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

//            var tagInAction = remember { mutableStateOf("") }
//            var showTagAction = remember { mutableStateOf(false) }
//
//            if (showTagAction.value) {
//                TagAction(
//                    onSearch = { searchTag(tagInAction.value) },
//                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
//                    onCombine = { addSearchTag(tagInAction.value, false) },
//                    onDismiss = { showTagAction.value = false },
//                    tag = tag,
//                    tags = selectedTags,
//                    show = showTagAction
//                )
//            }

            var selected by remember { mutableStateOf(false) }

            FilterChip(
                selected = selected,
                onClick = {
//                    tagInAction.value = "#$tagName:$tag"
//                    showTagAction.value = !showTagAction.value
                    selected = !selected
                    addSearchTag("#$tagName:$tag", false)
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
                    disabledSelectedContainerColor = Color.Transparent,
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.shapes.small
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.large)
                    .animateItem()
            )
        }
    }
}

@Composable
fun SelectedTagsGrid(
    onClick: () -> Unit,
    tagsItems: MutableList<String>,
    expanded: Boolean = false
){
    val rows = if (expanded && tagsItems.size > 3) 3 else 1
    val baseHeight = 60.dp
    val height = if (expanded && tagsItems.size > 3) baseHeight*rows else baseHeight

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(height)
    ) {
        items(tagsItems.size) { index ->
            val tag = tagsItems[index]

            FilterChip(
                selected = true,
                onClick = onClick,
                label = { Text(
                    text = tag,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                ) },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.shapes.small
                    )
                    .background(Color.Transparent, MaterialTheme.shapes.large)
                    .animateItem()
            )
        }
    }
}

@Composable
fun ColorTagsGrid(
    searchTag: (String) -> Unit,
    expanded: Boolean = false,
    addSearchTag: (String, Boolean) -> Unit,
    selectedTags: MutableList<String>,
    tagsItems: List<Int>
){
    val rows = if (expanded) 3 else 1
    val baseHeight = 60.dp
    val height =  if (expanded) baseHeight*rows else baseHeight
    val context = LocalContext.current

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(height)
    ) {
        items(tagsItems.size) { index ->
            if (tagsItems[index] == 0) return@items

            val tag = "#${context.getString(R.string.tag_color)}:${tagsItems[index]}"
            val color = Color(tagsItems[index])

//            var tagInAction = remember { mutableStateOf("") }
//            var showTagAction = remember { mutableStateOf(false) }
//
//            if (showTagAction.value) {
//                TagAction(
//                    onSearch = { searchTag(tagInAction.value) },
//                    onSearchCombined = { addSearchTag(tagInAction.value, true) },
//                    onCombine = { addSearchTag(tagInAction.value, false) },
//                    onDismiss = { showTagAction.value = false },
//                    tag = tag,
//                    tags = selectedTags,
//                    show = showTagAction
//                )
//            }

            var selected by remember { mutableStateOf(false) }

            FilterChip(
                selected = selected,
                onClick = {
//                    tagInAction.value = tag
//                    showTagAction.value = !showTagAction.value
                    selected = !selected
                    addSearchTag(tag, false)
                },
                label = {}, // { Text(text = tag) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    disabledSelectedContainerColor = Color.Transparent,
                    selectedContainerColor =  MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = color
                ),
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(2.dp, color),
                modifier = Modifier
                    //.background(color, MaterialTheme.shapes.large)
                    .animateItem()
            )
        }
    }
}