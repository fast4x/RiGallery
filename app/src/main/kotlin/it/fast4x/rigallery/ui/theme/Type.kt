package it.fast4x.rigallery.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import it.fast4x.rigallery.R

//// Set of Material typography styles to start with
//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.SansSerif,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//)

// Declare the font families
object AppFont {
    val Poppins = FontFamily(
        Font(R.font.poppins_regular),
        Font(R.font.poppins_italic, style = FontStyle.Italic),
        Font(R.font.poppins_medium, FontWeight.Medium),
        Font(R.font.poppins_mediumit, FontWeight.Medium, style = FontStyle.Italic),
        Font(R.font.poppins_semibold, FontWeight.SemiBold),
        Font(R.font.poppins_semiboldit, FontWeight.SemiBold, style = FontStyle.Italic),
        Font(R.font.poppins_bold, FontWeight.Bold),
        Font(R.font.poppins_boldit, FontWeight.Bold, style = FontStyle.Italic)
    )

    val Montserrat = FontFamily(
        Font(R.font.montserrat_regular),
        Font(R.font.montserrat_italic, style = FontStyle.Italic),
        Font(R.font.montserrat_medium, FontWeight.Medium),
        Font(R.font.montserrat_mediumit, FontWeight.Medium, style = FontStyle.Italic),
        Font(R.font.montserrat_semibold, FontWeight.SemiBold),
        Font(R.font.montserrat_semboldit, FontWeight.SemiBold, style = FontStyle.Italic),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_boldit, FontWeight.Bold, style = FontStyle.Italic)
    )
}

private val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.Poppins),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.Poppins),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.Poppins),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.Poppins),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.Poppins),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.Poppins),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.Poppins),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.Poppins),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.Poppins),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.Poppins),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.Poppins),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.Poppins),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.Poppins),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.Poppins),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.Poppins)
)