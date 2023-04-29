package io.fournkoner.netschool.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> LoadingTransition(
    targetState: T,
    fadeAnimDuration: Int = 300,
    content: @Composable AnimatedVisibilityScope.(T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            fadeIn(tween(durationMillis = fadeAnimDuration, delayMillis = fadeAnimDuration)) with
                fadeOut(tween(durationMillis = fadeAnimDuration))
        },
        content = content
    )
}
