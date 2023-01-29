package io.fournkoner.netschool.ui.components

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes

fun Modifier.loading(
    shown: Boolean,
    shape: CornerBasedShape = Shapes.medium,
) = composed {
    placeholder(
        visible = shown,
        color = LocalNetSchoolColors.current.shimmer,
        shape = RoundedCornerShape(shape.topStart),
        highlight = PlaceholderHighlight.shimmer(
            highlightColor = LocalNetSchoolColors.current.backgroundMain
        ),
    )
}