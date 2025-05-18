package it.fast4x.rigallery.core.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import it.fast4x.rigallery.R

enum class BaseColors {
    MAGENTA,
    RED,
    WHITE,
    YELLOW,
    BLACK,
    BLUE,
    CYAN,
    DARK_GRAY,
    GRAY,
    GREEN,
    LIGHT_GRAY;

    val tag: String
        @Composable
        get() = when (this) {
            BLACK -> "#${stringResource(R.string.tag_black)}"
            BLUE -> "#${stringResource(R.string.tag_blue)}"
            CYAN -> "#${stringResource(R.string.tag_cyan)}"
            DARK_GRAY -> "#${stringResource(R.string.tag_darkgray)}"
            GRAY -> "#${stringResource(R.string.tag_gray)}"
            GREEN -> "#${stringResource(R.string.tag_green)}"
            LIGHT_GRAY -> "#${stringResource(R.string.tag_lightgray)}"
            MAGENTA -> "#${stringResource(R.string.tag_magenta)}"
            RED -> "#${stringResource(R.string.tag_red)}"
            WHITE -> "#${stringResource(R.string.tag_white)}"
            YELLOW -> "#${stringResource(R.string.tag_yellow)}"
        }

    val color: Color
        get() = when (this) {
            BLACK -> Color.Black
            BLUE -> Color.Blue
            CYAN -> Color.Cyan
            DARK_GRAY -> Color.DarkGray
            GRAY -> Color.Gray
            GREEN -> Color.Green
            LIGHT_GRAY -> Color.LightGray
            MAGENTA -> Color.Magenta
            RED -> Color.Red
            WHITE -> Color.White
            YELLOW -> Color.Yellow
        }

    val icon: ImageVector
        get() = Icons.Filled.Colorize
}

