package io.fournkoner.netschool.ui.screens.reports.calculator

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.components.BottomSheet
import io.fournkoner.netschool.ui.components.SimpleBottomSheetToolbar
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.getGradeColor
import java.util.Locale

/**
 * @param grades grade to count
 */
data class CalculatorBottomSheet(private val grades: Map<Int, Int>) : AndroidScreen() {

    @Composable
    override fun Content() = BottomSheet {
        val viewModel: CalculatorViewModel = getViewModel(CalculatorViewModel.Factory(grades))
        val average by viewModel.average.collectAsState()
        val added by viewModel.added.collectAsState()
        val needed by viewModel.neededMarks.collectAsState()
        val navigator = LocalBottomSheetNavigator.current

        SimpleBottomSheetToolbar(stringResource(R.string.calculator_title)) {
            navigator.hide()
        }
        AnimatedAverageOverview(average)
        Grades(
            added = added,
            onMinus = viewModel::minus,
            onPlus = viewModel::plus
        )
        Divider(color = LocalNetSchoolColors.current.divider)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
        ) {
            items(needed) {
                NeededGradesCard(
                    grade = it.first,
                    ways = it.second
                )
            }
        }
    }

    @Composable
    private fun NeededGradesCard(
        grade: Float,
        ways: Map<Int, Int>
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .background(
                    color = LocalNetSchoolColors.current.backgroundCard,
                    shape = Shapes.medium
                )
                .border(
                    width = 1.dp,
                    color = LocalNetSchoolColors.current.divider,
                    shape = Shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(Typography.subtitle1.copy(color = LocalNetSchoolColors.current.textMain).toSpanStyle()) {
                        append(stringResource(R.string.calculator_needed_grades_beginning))
                        append(" ")
                    }
                    withStyle(Typography.subtitle1.copy(color = grade.getGradeColor()).toSpanStyle()) {
                        append("%.2f ".format(grade))
                    }
                    withStyle(Typography.subtitle1.copy(color = LocalNetSchoolColors.current.textMain).toSpanStyle()) {
                        append(stringResource(R.string.calculator_needed_grades_ending))
                    }
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ways.toList().forEachIndexed { index, pair ->
                    Column(
                        modifier = Modifier.width(((LocalConfiguration.current.screenWidthDp - 80) / 4).dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = pair.first.toString(),
                            style = Typography.h4.copy(color = pair.first.getGradeColor()),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 32.dp),
                            textAlign = TextAlign.Center
                        )

                        val configuration = LocalConfiguration.current
                        val context = LocalContext.current

                        val resources = remember(configuration, context) {
                            context.createConfigurationContext(
                                Configuration(configuration).apply {
                                    setLocale(Locale("ru"))
                                }
                            ).resources
                        }

                        Text(
                            text = resources.getQuantityString(
                                R.plurals.short_report_grades_count,
                                pair.second,
                                pair.second
                            ),
                            style = Typography.caption.copy(color = LocalNetSchoolColors.current.textSecondary),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (index < ways.size - 1) {
                        Text(
                            text = stringResource(R.string.calculator_or),
                            style = Typography.caption.copy(
                                color = LocalNetSchoolColors.current.accentMain,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier
                                .padding(4.dp)
                                .background(
                                    color = LocalNetSchoolColors.current.accentMain.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AnimatedAverageOverview(average: Float) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.short_report_overall),
                style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            AverageCounterText(average)
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun AverageCounterText(current: Float) {
        var oldCount by remember { mutableStateOf(current) }

        Row {
            Text(
                text = "${stringResource(R.string.short_report_average_grade)}: ",
                style = Typography.body2.copy(color = LocalNetSchoolColors.current.textSecondary)
            )

            val countString = "%.2f".format(current)
            val oldCountString = "%.2f".format(oldCount)

            for (i in countString.indices) {
                val oldChar = oldCountString.getOrNull(i)
                val newChar = countString[i]
                val char = if (oldChar == newChar) oldCountString[i] else countString[i]

                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        val coefficient = if (oldCount < current) 1 else -1

                        (
                            slideInVertically(spring()) { it * coefficient } + fadeIn(spring()) with
                                slideOutVertically(spring()) { -it * coefficient } + fadeOut(spring())
                            )
                            .using(SizeTransform(clip = false))
                    }
                ) { c ->
                    Text(
                        text = c.toString(),
                        style = Typography.body2.copy(
                            color = animateColorAsState(current.getGradeColor()).value
                        )
                    )
                }
            }
        }
        SideEffect {
            oldCount = current
        }
    }

    @Composable
    private fun Grades(
        added: Map<Int, Int>,
        onMinus: (grade: Int) -> Unit,
        onPlus: (grade: Int) -> Unit
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val devicesWidth = LocalConfiguration.current.screenWidthDp

            for (grade in 5 downTo 2) {
                Column(
                    modifier = Modifier.width(((devicesWidth - 80) / 4).dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = grade.toString(),
                        style = Typography.h4.copy(color = grade.getGradeColor()),
                        modifier = Modifier.defaultMinSize(minWidth = 32.dp),
                        textAlign = TextAlign.Center
                    )

                    val configuration = LocalConfiguration.current
                    val context = LocalContext.current

                    val resources = remember(configuration, context) {
                        context.createConfigurationContext(
                            Configuration(configuration).apply {
                                setLocale(Locale("ru"))
                            }
                        ).resources
                    }
                    val count = grades.getOrDefault(grade, 0)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = resources.getQuantityString(
                                R.plurals.short_report_grades_count,
                                count,
                                count
                            ),
                            style = Typography.caption.copy(color = LocalNetSchoolColors.current.textSecondary),
                            modifier = Modifier.weight(1f).padding(vertical = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        AnimatedBadge(added.getOrDefault(grade, 0))
                    }
                    Row(
                        modifier = Modifier
                            .padding(vertical = 14.dp)
                            .fillMaxWidth()
                    ) {
                        val minusEnabled = added.getOrDefault(grade, 0) > 0
                        Icon(
                            painter = painterResource(R.drawable.ic_minus_square),
                            contentDescription = null,
                            tint = LocalNetSchoolColors.current.gradeBad,
                            modifier = Modifier
                                .scale(animateFloatAsState(if (minusEnabled) 1f else 0.8f).value)
                                .alpha(animateFloatAsState(if (minusEnabled) 1f else 0.5f).value)
                                .weight(1f)
                                .height(24.dp)
                                .clickable(
                                    enabled = minusEnabled,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onMinus(grade) }
                        )
                        val plusEnabled = added.getOrDefault(grade, 0) <= MAX_ADD_COUNT
                        Icon(
                            painter = painterResource(R.drawable.ic_plus_square),
                            contentDescription = null,
                            tint = LocalNetSchoolColors.current.gradeGreat,
                            modifier = Modifier
                                .scale(animateFloatAsState(if (plusEnabled) 1f else 0.8f).value)
                                .alpha(animateFloatAsState(if (plusEnabled) 1f else 0.5f).value)
                                .weight(1f)
                                .height(24.dp)
                                .clickable(
                                    enabled = plusEnabled,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onPlus(grade) }
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun AnimatedBadge(addedCount: Int) {
        var oldCount by remember { mutableStateOf(addedCount) }

        AnimatedVisibility(addedCount > 0) {
            Row(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .background(
                        color = LocalNetSchoolColors.current.accentMain.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "+",
                    style = Typography.caption.copy(
                        color = LocalNetSchoolColors.current.accentMain,
                        fontWeight = FontWeight.Medium
                    )
                )

                val countString = addedCount.toString()
                val oldCountString = oldCount.toString()

                for (i in MAX_ADD_COUNT.toString().indices) {
                    val diff = MAX_ADD_COUNT.toString().length - countString.length
                    val char = if (i < diff) {
                        '\u0000'
                    } else {
                        val oldChar = oldCountString.getOrNull(i - diff)
                        val newChar = countString[i - diff]
                        if (oldChar == newChar) oldCountString[i - diff] else countString[i - diff]
                    }

                    AnimatedContent(
                        targetState = char,
                        transitionSpec = {
                            val coefficient = if (oldCount < addedCount) 1 else -1

                            slideInVertically(spring()) { it * coefficient } + fadeIn(spring()) with
                                slideOutVertically(spring()) { -it * coefficient } + fadeOut(spring())
                        }
                    ) { c ->
                        Text(
                            text = c.toString(),
                            style = Typography.caption.copy(
                                color = LocalNetSchoolColors.current.accentMain,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
        SideEffect {
            oldCount = addedCount
        }
    }

    companion object {

        private const val MAX_ADD_COUNT = 99
    }
}
