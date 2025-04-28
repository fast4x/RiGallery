package it.fast4x.rigallery.feature_node.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.core.enums.TagsType

@Composable
fun TagsGrid(
    searchTag: (String) -> Unit,
){
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(50.dp)
    ) {
        items(TagsType.entries.size) { index ->
            FilterChip(
                selected = true,
                onClick = { searchTag(TagsType.entries[index].tag) },
                label = { Text(text = "#${TagsType.entries[index].name}") },
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