package io.fournkoner.netschool.ui.style

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class NetSchoolColors(
    val backgroundMain: Color,
    val backgroundCard: Color,
    val shimmer: Color,

    val accentMain: Color,
    val accentInactive: Color,
    val onAccent: Color,

    val textMain: Color,
    val textSecondary: Color,

    val divider: Color,

    val gradeGreat: Color, // 5
    val grateGood: Color, // 4
    val gradeSatisfactory: Color, // 3
    val gradeBad: Color, // 2
    val gradeOnus: Color, // долг
) {

    companion object {

        val lightPalette: NetSchoolColors
            get() = NetSchoolColors(
                backgroundMain = Color(0xFFFFFFFF),
                backgroundCard = Color(0xFFF9F9F9),
                shimmer = Color(0x10000000),

                accentMain = Color(0xFF3E95E5),
                accentInactive = Color(0xFF637484),
                onAccent = Color(0xFFFFFFFF),

                textMain = Color(0xFF000000),
                textSecondary = Color(0x9A000000),

                divider = Color(0xFFEEEEEE),

                gradeGreat = Color(0xFF50C069),
                grateGood = Color(0xFF508AC0),
                gradeSatisfactory = Color(0xFFEC8711),
                gradeBad = Color(0xFFEC1111),
                gradeOnus = Color(0xFFEC1111)
            )

        val darkPalette: NetSchoolColors
            get() = NetSchoolColors(
                backgroundMain = Color(0xFF1A1A1A),
                backgroundCard = Color(0xFF212121),
                shimmer = Color(0x10FFFFFF),

                accentMain = Color(0xFF68AFF0),
                accentInactive = Color(0xFF6D7378),
                onAccent = Color(0xFFFFFFFF),

                textMain = Color(0xFFFFFFFF),
                textSecondary = Color(0x9AFFFFFF),

                divider = Color(0xFF2A2A2A),

                gradeGreat = Color(0xFF5DCF76),
                grateGood = Color(0xFF7BBAF4),
                gradeSatisfactory = Color(0xFFF59E38),
                gradeBad = Color(0xFFF04242),
                gradeOnus = Color(0xFFF04242)
            )
    }
}

val LocalNetSchoolColors =
    compositionLocalOf<NetSchoolColors> { error("LocalNetSchoolColors provides nothing") }