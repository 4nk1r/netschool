package io.fournkoner.netschool.ui.screens.short_report

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.reports.ReportRequestData
import io.fournkoner.netschool.domain.entities.reports.ShortReport
import io.fournkoner.netschool.ui.components.SimpleToolbar
import io.fournkoner.netschool.ui.components.loading
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.getGradeColor
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ShortReportScreen(
    navController: NavController,
    viewModel: ShortReportViewModel = hiltViewModel(),
) {
    val state = rememberLazyListState()

    val periods = viewModel.periods.collectAsState()
    val report = viewModel.report.collectAsState()

    var isFullscreen by rememberSaveable { mutableStateOf(true) }
    val showDivider by remember {
        derivedStateOf {
            (state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0)
                    && report.value != null
        }
    }

    Scaffold(
        topBar = {
            SimpleToolbar(title = stringResource(R.string.short_report_title)) {
                navController.popBackStack()
            }
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                PeriodSelection(periods = periods.value, isFullscreen = isFullscreen) {
                    isFullscreen = false
                    viewModel.generate(it)
                }
                Divider(
                    color = LocalNetSchoolColors.current.divider,
                    modifier = Modifier
                        .height(animateDpAsState(if (showDivider) 1.dp else 0.dp).value)
                )
                AnimatedVisibility(visible = !isFullscreen) {
                    ReportList(report.value, state)
                }
            }
        },
        backgroundColor = LocalNetSchoolColors.current.backgroundMain
    )
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun ReportList(
    shortReport: ShortReport?,
    state: LazyListState,
) {
    AnimatedContent(targetState = shortReport) { report ->
        if (report == null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                state = LazyListState(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                userScrollEnabled = false
            ) {
                item { ReportOverallGrades(null) }
                items(10) { ReportSubject(null) }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                state = state,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item { ReportOverallGrades(report.total) }
                items(report.subjects) { ReportSubject(subject = it) }
            }
        }
    }

    LaunchedEffect(shortReport) {
        if (shortReport == null) {
            delay(310)
            state.scrollToItem(0)
        }
    }
}

@Composable
private fun ReportSubject(subject: ShortReport.Subject?) {
    Column(
        modifier = Modifier
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
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.loading(subject == null),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = (subject?.name ?: "").getIconPainter(),
                contentDescription = subject?.name ?: "",
                tint = LocalNetSchoolColors.current.accentMain
            )
            Text(
                text = subject?.name ?: "placeholder",
                style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (subject != null && subject.grades.average != 0f) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        Typography.body2.copy(
                            color = LocalNetSchoolColors.current.textSecondary
                        ).toSpanStyle()
                    ) {
                        append(stringResource(R.string.short_report_average_grade))
                        append(": ")
                    }
                    withStyle(
                        Typography.body2.copy(
                            color = subject.grades.average.getGradeColor()
                        ).toSpanStyle()
                    ) {
                        append(subject.grades.average.toString())
                    }
                },
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Grades(subject?.grades, extraPadding = true, showAll = false)
    }
}

@Composable
private fun ReportOverallGrades(grades: ShortReport.Grades?) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.short_report_overall),
                style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .loading(grades == null)
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        Typography.body2.copy(
                            color = LocalNetSchoolColors.current.textSecondary
                        ).toSpanStyle()
                    ) {
                        append(stringResource(R.string.short_report_average_grade))
                        append(": ")
                    }
                    withStyle(
                        Typography.body2.copy(
                            color = grades?.average?.getGradeColor()
                                ?: LocalNetSchoolColors.current.textSecondary
                        ).toSpanStyle()
                    ) {
                        append(grades?.average.toString())
                    }
                },
                modifier = Modifier.loading(grades == null)
            )
        }
        Grades(grades, extraPadding = false, showAll = true)
    }
}

@Composable
private fun Grades(
    grades: ShortReport.Grades?,
    extraPadding: Boolean,
    showAll: Boolean
) {
    if (grades?.greatCount == 0
        && grades.goodCount == 0
        && grades.satisfactoryCount == 0
        && grades.badCount == 0
    ) {
        Text(
            text = stringResource(R.string.short_report_no_grades),
            style = Typography.body1.copy(color = LocalNetSchoolColors.current.textSecondary),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
        )
    } else {
        Row(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = if (extraPadding) 0.dp else 16.dp,
                    end = if (extraPadding) 0.dp else 16.dp,
                    bottom = if (extraPadding) 0.dp else 16.dp,
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val devicesWidth = LocalConfiguration.current.screenWidthDp

            mapOf(
                5 to (grades?.greatCount ?: 1),
                4 to (grades?.goodCount ?: 1),
                3 to (grades?.satisfactoryCount ?: 1),
                2 to (grades?.badCount ?: 1),
            ).forEach { (grade, count) ->
                if (count > 0 || showAll) {
                    Column(
                        modifier = Modifier
                            .width(((devicesWidth - if (extraPadding) 112 else 80) / 4).dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = grade.toString(),
                            style = Typography.h4.copy(color = grade.getGradeColor()),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 32.dp)
                                .loading(grades == null),
                            textAlign = TextAlign.Center
                        )

                        val configuration = LocalConfiguration.current
                        val context = LocalContext.current

                        val resources = remember(configuration, context) {
                            context.createConfigurationContext(
                                Configuration(configuration).apply {
                                    setLocale(java.util.Locale("ru"))
                                }
                            ).resources
                        }

                        Text(
                            text = resources.getQuantityString(
                                R.plurals.short_report_grades_count,
                                count % 10,
                                count
                            ),
                            style = Typography.caption.copy(color = LocalNetSchoolColors.current.textSecondary),
                            modifier = Modifier.loading(grades == null),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PeriodSelection(
    periods: List<ReportRequestData.Value>?,
    isFullscreen: Boolean,
    onChange: (ReportRequestData.Value) -> Unit,
) {
    var selected by rememberSaveable(periods) { mutableStateOf(periods?.first()?.value ?: "") }
    var isExpanded by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(visible = isFullscreen && periods != null) {
            PeriodSelectionTitle(periods, selected)
        }
        PeriodSelectionCard(periods) { list ->
            if (list.isNullOrEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(32.dp)
                        .size(28.dp),
                    color = LocalNetSchoolColors.current.accentMain,
                    strokeWidth = 2.dp
                )
            } else {
                AnimatedContent(
                    targetState = isExpanded || isFullscreen,
                    transitionSpec = {
                        fadeIn(spring()) + slideInVertically(spring()) {
                            (it * 0.95f).toInt() * if (isExpanded || isFullscreen) -1 else 1
                        } with fadeOut(spring()) + slideOutVertically(spring()) {
                            (it * 0.95f).toInt() * if (isExpanded || isFullscreen) 1 else -1
                        }
                    }
                ) { showList ->
                    if (showList) {
                        PeriodSelectionList(list, selected) { item ->
                            selected = item.value
                            if (!isFullscreen) {
                                onChange(item)
                                isExpanded = false
                            }
                        }
                    } else {
                        PeriodSelectionCollapsedContent(periods!!.find { it.value == selected }!!.name) {
                            isExpanded = true
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = isFullscreen && periods != null) {
            PeriodSelectionButton {
                isExpanded = false
                onChange(periods!!.find { it.value == selected }!!)
            }
        }
    }
}

@Composable
private fun PeriodSelectionButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = LocalNetSchoolColors.current.accentMain,
                shape = Shapes.medium
            )
            .clip(Shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.short_report_generate),
            style = Typography.button.copy(color = LocalNetSchoolColors.current.onAccent),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PeriodSelectionCollapsedContent(
    selected: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    Typography.subtitle2.copy(
                        color = LocalNetSchoolColors.current.textMain,
                        fontWeight = FontWeight.SemiBold
                    ).toSpanStyle()
                ) {
                    append(stringResource(R.string.short_report_period))
                    append(": ")
                }
                withStyle(
                    Typography.subtitle2.copy(
                        color = LocalNetSchoolColors.current.textSecondary,
                        fontWeight = FontWeight.Normal
                    ).toSpanStyle()
                ) {
                    append(selected)
                }
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_expand_collapse),
            contentDescription = stringResource(R.string.short_report_selected),
            tint = LocalNetSchoolColors.current.accentMain,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun PeriodSelectionList(
    list: List<ReportRequestData.Value>,
    selected: String,
    onChange: (ReportRequestData.Value) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        list.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { onChange(item) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = item.name,
                    style = Typography.button.copy(
                        color = LocalNetSchoolColors.current.textMain
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    painter = painterResource(R.drawable.ic_checked_mark),
                    contentDescription = stringResource(R.string.short_report_selected),
                    tint = LocalNetSchoolColors.current.accentMain,
                    modifier = Modifier
                        .alpha(animateFloatAsState(if (selected == item.value) 1f else 0f).value)
                        .size(24.dp)
                )
            }
            if (index < list.size - 1) {
                Divider(color = LocalNetSchoolColors.current.divider)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun PeriodSelectionCard(
    periods: List<ReportRequestData.Value>?,
    content: @Composable AnimatedVisibilityScope.(List<ReportRequestData.Value>?) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .background(
                color = LocalNetSchoolColors.current.backgroundCard,
                shape = Shapes.medium
            )
            .border(
                width = 1.dp,
                color = LocalNetSchoolColors.current.divider,
                shape = Shapes.medium
            )
    ) {
        AnimatedContent(
            targetState = periods,
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Composable
private fun PeriodSelectionTitle(
    periods: List<ReportRequestData.Value>?,
    selected: String,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                Typography.subtitle2.copy(
                    color = LocalNetSchoolColors.current.textMain,
                    fontWeight = FontWeight.SemiBold
                ).toSpanStyle()
            ) {
                append(stringResource(R.string.short_report_period))
                append(": ")
            }
            withStyle(
                Typography.subtitle2.copy(
                    color = LocalNetSchoolColors.current.textSecondary,
                    fontWeight = FontWeight.Normal
                ).toSpanStyle()
            ) {
                append(periods!!.find { it.value == selected }!!.name)
            }
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    )
}

@Composable
private fun String.getIconPainter(): Painter {
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