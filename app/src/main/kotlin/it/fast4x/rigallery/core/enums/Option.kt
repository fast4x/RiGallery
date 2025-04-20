package it.fast4x.rigallery.core.enums

import androidx.compose.ui.graphics.vector.ImageVector

data class Option(
    val ordinal: Int,
    val name: String,
    val title: String,
    val icon: ImageVector? = null,
)
