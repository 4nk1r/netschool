package io.fournkoner.netschool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes

@Composable
fun BottomSheet(content: @Composable ColumnScope.() -> Unit) {
    val shape = remember {
        RoundedCornerShape(
            topStart = Shapes.medium.topStart,
            topEnd = Shapes.medium.topEnd,
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 56.dp)
                .fillMaxSize()
                .shadow(
                    elevation = 4.dp,
                    shape = shape
                )
                .background(
                    color = LocalNetSchoolColors.current.backgroundMain,
                    shape = shape
                ),
            content = content
        )
    }
}
