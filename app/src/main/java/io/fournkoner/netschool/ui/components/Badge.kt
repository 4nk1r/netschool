package io.fournkoner.netschool.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import io.fournkoner.netschool.utils.debugValue

@Composable
fun BadgedLayout(
    modifier: Modifier = Modifier,
    badge: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Layout(
        {
            Box(
                modifier = Modifier.layoutId("anchor"),
                contentAlignment = Alignment.Center,
                content = content
            )
            Box(
                modifier = Modifier.layoutId("badge"),
                content = badge
            )
        },
        modifier = modifier
    ) { measurables, constraints ->
        val badgePlaceable = measurables.first { it.layoutId == "badge" }.measure(
            constraints.copy(minHeight = 0, minWidth = 0)
        )
        val anchorPlaceable = measurables.first { it.layoutId == "anchor" }.measure(constraints)

        val firstBaseline = anchorPlaceable[FirstBaseline]
        val lastBaseline = anchorPlaceable[LastBaseline]
        val totalWidth = anchorPlaceable.width.debugValue("totalWidth")
        val totalHeight = anchorPlaceable.height
        val badgeWidth = badgePlaceable.width.debugValue("badgeWidth")

        layout(
            totalWidth,
            totalHeight,
            // Provide custom baselines based only on the anchor content to avoid default baseline
            // calculations from including by any badge content.
            mapOf(FirstBaseline to firstBaseline, LastBaseline to lastBaseline)
        ) {
            anchorPlaceable.placeRelative(0, 0)
            badgePlaceable.placeRelative(
                x = if (badgeWidth < totalWidth / 2) totalWidth / 2 else totalWidth - badgeWidth,
                y = 4.dp.toPx().toInt()
            )
        }
    }
}