package io.fournkoner.netschool.ui.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.fournkoner.netschool.R

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

private val interFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semi_bold, FontWeight.SemiBold),
)
private val manropeFontFamily = FontFamily(
    Font(R.font.manrope_semi_bold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)
val Typography = Typography(
    h4 = TextStyle(
        fontSize = 24.sp,
        fontFamily = manropeFontFamily,
        fontWeight = FontWeight.Bold
    ),
    h5 = TextStyle(
        fontSize = 20.sp,
        fontFamily = manropeFontFamily,
        fontWeight = FontWeight.Bold
    ),
    h6 = TextStyle(
        fontSize = 20.sp,
        fontFamily = interFontFamily,
        fontWeight = FontWeight.SemiBold
    ),
    subtitle1 = TextStyle(
        fontSize = 16.sp,
        fontFamily = manropeFontFamily,
        fontWeight = FontWeight.SemiBold
    ),
    body1 = TextStyle(
        fontSize = 16.sp,
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Normal
    ),
    body2 = TextStyle(
        fontSize = 14.sp,
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Normal
    ),
    caption = TextStyle(
        fontSize = 12.sp,
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Normal
    ),
    button = TextStyle(
        fontSize = 16.sp,
        fontFamily = interFontFamily,
        fontWeight = FontWeight.SemiBold
    ),
)

@Composable
fun NetSchoolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        shapes = Shapes,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalNetSchoolColors provides if (darkTheme) {
                NetSchoolColors.darkPalette
            } else {
                NetSchoolColors.lightPalette
            },
            content = content
        )
    }
}