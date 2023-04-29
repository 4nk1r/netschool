package io.fournkoner.netschool.ui.navigation

import android.graphics.Path
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.core.view.animation.PathInterpolatorCompat
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import cafe.adriel.voyager.transitions.ScreenTransitionContent

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Android13NavigationTransition(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    content: ScreenTransitionContent
) {
    val coefficient = 10
    val animationSpec = tween<IntOffset>(
        easing = FastOutExtraSlowInInterpolator,
        durationMillis = 500
    )

    ScreenTransition(
        navigator = navigator,
        modifier = modifier,
        enterTransition = {
            fadeIn() + slideInHorizontally(animationSpec = animationSpec) { it / coefficient } with
                fadeOut() + slideOutHorizontally(animationSpec = animationSpec) { -it / coefficient }
        },
        exitTransition = {
            fadeIn() + slideInHorizontally(animationSpec = animationSpec) { -it / coefficient } with
                fadeOut() + slideOutHorizontally(animationSpec = animationSpec) { it / coefficient }
        },
        content = content
    )
}

private object FastOutExtraSlowInInterpolator : Easing {

    private val fastOutExtraSlowInInterpolator = PathInterpolatorCompat.create(
        Path().apply {
            moveTo(0f, 0f)
            cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.4f)
            cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
        }
    )

    override fun transform(fraction: Float): Float {
        return fastOutExtraSlowInInterpolator.getInterpolation(fraction)
    }
}
