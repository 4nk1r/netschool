package io.fournkoner.netschool.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: ((String) -> Unit)
) = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)
    LocalAutofillTree.current += autofillNode

    this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
        }
        .onFocusChanged { focusState ->
            autofill?.run {
                if (focusState.isFocused) {
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}

@Composable
fun Int?.getGradeColor(): Color {
    return when (this) {
        5 -> LocalNetSchoolColors.current.gradeGreat
        4 -> LocalNetSchoolColors.current.grateGood
        3 -> LocalNetSchoolColors.current.gradeSatisfactory
        2 -> LocalNetSchoolColors.current.gradeBad
        null, 0 -> LocalNetSchoolColors.current.gradeOnus
        else -> error("Unknown grade mark: $this")
    }
}

@Composable
fun Float.getGradeColor() = roundToInt().getGradeColor()

@Composable
fun String.getIconPainter(): Painter {
    val s = toLowerCase(Locale.current)
    return painterResource(
        when {
            s.contains("астроном") -> R.drawable.ic_subject_astronomy
            s.contains("биолог") -> R.drawable.ic_subject_biology
            s.contains("географ") -> R.drawable.ic_subject_geography
            s.contains("хим") -> R.drawable.ic_subject_chemistry
            s.contains("физик") -> R.drawable.ic_subject_physics
            s.contains("информат") -> R.drawable.ic_subject_computer_science
            s.contains("матем") || s.contains("алгебр") -> R.drawable.ic_subject_math
            s.contains("геометр") -> R.drawable.ic_subject_geometry
            s.contains("истор") -> R.drawable.ic_subject_history
            s.contains("обществ") -> R.drawable.ic_subject_social_science
            s.contains("безопасности") || s.contains("обж") -> R.drawable.ic_subject_bsl
            s.contains("физкульт") || s.contains("физическ") -> R.drawable.ic_subject_pe
            s.contains("литер") -> R.drawable.ic_subject_literature
            s.contains("русс") -> R.drawable.ic_subject_russian
            s.contains("музык") -> R.drawable.ic_subject_music
            s.contains("изо") -> R.drawable.ic_subject_art
            s.contains("эконом") -> R.drawable.ic_subject_economy
            else -> R.drawable.ic_subject_unknown
        }
    )
}
