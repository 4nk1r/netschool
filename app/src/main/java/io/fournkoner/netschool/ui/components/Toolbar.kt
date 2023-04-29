package io.fournkoner.netschool.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.ui.style.mediumDp

@Composable
fun SimpleToolbar(
    title: String,
    showDivider: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    BaseToolbar(
        title = title,
        navigationIcon = painterResource(R.drawable.ic_arrow_back),
        showDivider = showDivider,
        topPadding = WindowInsets.statusBars
            .asPaddingValues()
            .calculateTopPadding(),
        onBack = onBack
    )
}

@Composable
fun SimpleBottomSheetToolbar(
    title: String,
    showDivider: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    BaseToolbar(
        title = title,
        navigationIcon = painterResource(R.drawable.ic_close),
        showDivider = showDivider,
        topPadding = Shapes.mediumDp,
        onBack = onBack
    )
}

@Composable
private fun BaseToolbar(
    title: String,
    navigationIcon: Painter?,
    showDivider: Boolean,
    topPadding: Dp,
    onBack: (() -> Unit)?
) {
    Column {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp + topPadding)
                .background(LocalNetSchoolColors.current.backgroundMain)
                .padding(top = topPadding),
            navigationIcon = if (onBack != null && navigationIcon != null) {
                {
                    TopAppBarIcon(
                        iconPainter = navigationIcon,
                        tint = LocalNetSchoolColors.current.accentMain,
                        onClick = onBack
                    )
                }
            } else {
                null
            },
            title = {
                Text(
                    text = title,
                    style = Typography.h4.copy(color = LocalNetSchoolColors.current.textMain),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            backgroundColor = LocalNetSchoolColors.current.backgroundMain,
            elevation = 0.dp
        )
        AnimatedVisibility(
            visible = showDivider,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Divider(color = LocalNetSchoolColors.current.divider)
        }
    }
}

@Composable
fun TopAppBarIcon(
    iconPainter: Painter,
    contentDescription: String? = null,
    tint: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(radius = 18.dp),
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
    }
}
