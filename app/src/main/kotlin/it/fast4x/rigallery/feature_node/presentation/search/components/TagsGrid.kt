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
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.enums.MetadataTagsType
import it.fast4x.rigallery.core.enums.TagsType

@Composable
fun TagsGrid(
    searchTag: (String) -> Unit,
    expanded: Boolean = false
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
            FilterChip(
                selected = true,
                onClick = { searchTag(tag) },
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
            )
        }
    }
}


@Composable
fun RecentTagsGrid(
    searchTag: (String) -> Unit,
    historyTagsItems: SnapshotStateList<Pair<String, String>>,
){
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(50.dp)
    ) {
        items(historyTagsItems.size) { index ->
            val tag = historyTagsItems[index].second
            val tagType = TagsType.entries.find { it.tag == tag }
            FilterChip(
                selected = true,
                onClick = { searchTag(tag) },
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
            )
        }
    }
}

@Composable
fun MetadataTagsGrid(
    searchTag: (String) -> Unit,
){
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(50.dp)
    ) {
        items(MetadataTagsType.entries.size) { index ->
            val tag = MetadataTagsType.entries[index].tag
            FilterChip(
                selected = true,
                onClick = { searchTag(tag) },
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
            )
        }
    }
}

@Composable
fun LocationTagsGrid(
    searchTag: (String) -> Unit,
    tagsItems: List<String?>,
    locationIsCountry: Boolean = true
){
    val tagName = stringResource(if (locationIsCountry) R.string.tag_country else R.string.tag_locality)

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(50.dp)
    ) {
        items(tagsItems.size) { index ->
            val tag = tagsItems[index]
            FilterChip(
                selected = true,
                onClick = { searchTag("#$tagName:$tag") },
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
            )
        }
    }
}