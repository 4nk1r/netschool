package io.fournkoner.netschool.ui.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
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
val Shapes.mediumDp get() = 8.dp

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
    subtitle2 = TextStyle(
        fontSize = 16.sp,
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Medium
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

private val Colors
    @Composable get() = Colors(
        primary = LocalNetSchoolColors.current.accentMain,
        primaryVariant = LocalNetSchoolColors.current.accentMain,
        secondary = LocalNetSchoolColors.current.accentMain,
        secondaryVariant = LocalNetSchoolColors.current.accentMain,
        background = LocalNetSchoolColors.current.backgroundMain,
        surface = LocalNetSchoolColors.current.backgroundCard,
        error = LocalNetSchoolColors.current.gradeBad,
        onPrimary = LocalNetSchoolColors.current.onAccent,
        onSecondary = LocalNetSchoolColors.current.onAccent,
        onBackground = LocalNetSchoolColors.current.textMain,
        onSurface = LocalNetSchoolColors.current.textMain,
        onError = LocalNetSchoolColors.current.onBadge,
        isLight = !isSystemInDarkTheme()
    )

@Composable
fun NetSchoolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalNetSchoolColors provides if (darkTheme) {
            NetSchoolColors.darkPalette
        } else {
            NetSchoolColors.lightPalette
        },
    ) {
        MaterialTheme(
            shapes = Shapes,
            typography = Typography,
            colors = Colors,
        ) {
            androidx.compose.material3.MaterialTheme(
                colorScheme = androidx.compose.material3.MaterialTheme.colorScheme.copy(
                    primary = LocalNetSchoolColors.current.accentMain,
                    primaryContainer = LocalNetSchoolColors.current.accentMain,
                    secondary = LocalNetSchoolColors.current.accentMain,
                    secondaryContainer = LocalNetSchoolColors.current.accentMain,
                    tertiary = LocalNetSchoolColors.current.accentMain,
                    tertiaryContainer = LocalNetSchoolColors.current.accentMain,
                    onPrimary = LocalNetSchoolColors.current.onAccent,
                    onSecondary = LocalNetSchoolColors.current.onAccent,
                    onTertiary = LocalNetSchoolColors.current.onAccent,
                    error = LocalNetSchoolColors.current.gradeBad,
                    onError = LocalNetSchoolColors.current.backgroundMain,
                    surface = LocalNetSchoolColors.current.backgroundCard,
                    background = LocalNetSchoolColors.current.backgroundMain,
                    onBackground = LocalNetSchoolColors.current.textMain,
                    onSurface = LocalNetSchoolColors.current.textMain,
                ),
                androidx.compose.material3.MaterialTheme.shapes.copy(
                    small = Shapes.small,
                    medium = Shapes.medium,
                    large = Shapes.large
                ),
                content = content
            )
        }
    }
}