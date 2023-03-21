package io.fournkoner.netschool.ui.screens.subject_report

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.reports.SubjectReport
import io.fournkoner.netschool.ui.components.TopAppBarIcon
import io.fournkoner.netschool.ui.components.VSpace
import io.fournkoner.netschool.ui.components.loading
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.debugValue
import io.fournkoner.netschool.utils.getGradeColor
import io.fournkoner.netschool.utils.toLocalDate
import kotlinx.coroutines.delay
import java.util.*

class SubjectReportScreen : AndroidScreen() {

    companion object {

        // shitty way to achieve the "start for result" behavior
        var selectedSubjectResult by mutableStateOf<String?>(null)
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val viewModel: SubjectReportViewModel = getViewModel()
        val state = rememberLazyListState()
        val calendarSelectionState = rememberUseCaseState()
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        val showDivider by remember {
            derivedStateOf {
                state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0
            }
        }

        val defaultPeriodRange = viewModel.defaultRange.collectAsState()
        val availablePeriodRange = viewModel.availableRange.collectAsState()

        val subjects = viewModel.subjects.collectAsState()

        var selectStart by remember { mutableStateOf(false) }
        var selectedStart by rememberSaveable(defaultPeriodRange.value) {
            mutableStateOf(defaultPeriodRange.value?.first ?: -1L).debugValue("start")
        }
        var selectedEnd by rememberSaveable(defaultPeriodRange.value) {
            mutableStateOf(defaultPeriodRange.value?.last ?: -1L).debugValue("end")
        }

        val selectedSubject = viewModel.selectedSubject.collectAsState()
        val selectedRange = viewModel.selectedRange.collectAsState()

        val subjectReport = viewModel.subjectReport.collectAsState()
        val reportEmpty = viewModel.reportEmpty.collectAsState()

        Scaffold(
            topBar = {
                Toolbar(
                    subjectName = if (selectedRange.value == null) null else selectedSubject.value?.name,
                    range = selectedRange.value,
                    showDivider = showDivider,
                    onResetChoice = {
                        viewModel.resetChoice()
                        selectedSubjectResult = null
                    }
                )
            },
            content = {
                AnimatedContent(
                    targetState = selectedSubject.value != null && selectedRange.value != null,
                    transitionSpec = {
                        fadeIn(tween(500)) +
                                slideInVertically(tween(500)) { (it * 0.95f).toInt() } +
                                scaleIn(tween(500), initialScale = 0.9f) with
                                fadeOut(tween(500)) +
                                slideOutVertically(tween(500)) { (it * -0.95f).toInt() } +
                                scaleOut(tween(500), targetScale = 0.9f)
                    }
                ) { isSubjectChosen ->
                    if (isSubjectChosen) {
                        ReportList(
                            subjectReport = subjectReport.value,
                            reportEmpty = reportEmpty.value,
                            state = state
                        )
                    } else {
                        Selection(
                            selectedStart = remember(selectedStart) {
                                if (selectedStart != -1L) {
                                    selectedStart
                                        .toLocalDate()
                                        .run {
                                            String.format(
                                                "%02d.%02d.%s",
                                                dayOfMonth,
                                                monthValue,
                                                year.toString().takeLast(2)
                                            )
                                        }
                                } else null
                            },
                            selectedEnd = remember(selectedEnd) {
                                if (selectedEnd != -1L) {
                                    selectedEnd
                                        .toLocalDate()
                                        .run {
                                            String.format(
                                                "%02d.%02d.%s",
                                                dayOfMonth,
                                                monthValue,
                                                year.toString().takeLast(2)
                                            )
                                        }
                                } else null
                            },
                            selectedSubject = selectedSubject.value?.name,
                            onExpandStartSelection = {
                                selectStart = true
                                calendarSelectionState.show()
                            },
                            onExpandEndSelection = {
                                selectStart = false
                                calendarSelectionState.show()
                            },
                            onExpandSubjectSelection = {
                                bottomSheetNavigator.show(
                                    SubjectSelectionBottomSheet(
                                        subjects = subjects.value!!.associate { it.value to it.name }
                                    )
                                )
                            },
                            onContinue = { viewModel.generate(selectedStart..selectedEnd) },
                        )
                    }
                }
            }
        )
        CalendarDialog(
            state = calendarSelectionState,
            selection = CalendarSelection.Date { newDate ->
                val ms = newDate.toEpochDay() * 86_400_000 // ms in a day
                if (selectStart) selectedStart = ms else selectedEnd = ms

                if (selectedStart > selectedEnd) {
                    val temp = selectedStart
                    selectedStart = selectedEnd
                    selectedEnd = temp
                }
            },
            config = CalendarConfig(
                locale = Locale("ru"),
                boundary = (availablePeriodRange.value?.first ?: 0).toLocalDate()..
                        (availablePeriodRange.value?.last ?: 0).toLocalDate(),
                disabledDates = if (selectStart) {
                    listOf(selectedEnd.toLocalDate())
                } else {
                    listOf(selectedStart.toLocalDate())
                },
            ),
        )
        LaunchedEffect(selectedSubjectResult) {
            viewModel.selectSubject(selectedSubjectResult)
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun ReportList(
        subjectReport: SubjectReport?,
        reportEmpty: Boolean,
        state: LazyListState,
    ) {
        AnimatedContent(targetState = subjectReport) { report ->
            if (report == null) {
                AnimatedContent(targetState = reportEmpty) { isEmpty ->
                    if (!isEmpty) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            state = LazyListState(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            userScrollEnabled = false
                        ) {
                            item { ReportOverallGrades(null) }
                            items(10) { Task(null) }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                16.dp,
                                Alignment.CenterVertically
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nobody_found),
                                contentDescription = stringResource(R.string.reports_no_data_for_period),
                                modifier = Modifier.size(48.dp),
                                tint = LocalNetSchoolColors.current.accentInactive
                            )
                            Text(
                                text = stringResource(R.string.reports_no_data_for_period),
                                style = Typography.subtitle1.copy(color = LocalNetSchoolColors.current.accentInactive)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    state = state,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item { ReportOverallGrades(report.total) }
                    items(report.tasks) { Task(it) }
                }
            }
        }

        LaunchedEffect(subjectReport) {
            if (subjectReport == null) {
                delay(310)
                state.scrollToItem(0)
            }
        }
    }

    @Composable
    private fun Task(task: SubjectReport.Task?) {
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
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task?.type ?: "placeholder",
                    modifier = Modifier
                        .loading(task == null)
                        .weight(1f),
                    style = Typography.caption.copy(color = LocalNetSchoolColors.current.textSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = task?.date ?: "placeholder",
                    modifier = Modifier.loading(task == null),
                    style = Typography.caption.copy(color = LocalNetSchoolColors.current.textSecondary),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task?.name ?: "placeholder".repeat(4),
                    modifier = Modifier
                        .weight(1f)
                        .loading(task == null),
                    style = Typography.body1.copy(color = LocalNetSchoolColors.current.textMain),
                )
                Text(
                    text = task?.grade?.toString() ?: "•",
                    modifier = Modifier
                        .defaultMinSize(minWidth = 42.dp)
                        .loading(task == null),
                    style = Typography.h4.copy(color = task?.grade.getGradeColor()),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun ReportOverallGrades(grades: SubjectReport.Grades?) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
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
            Grades(grades)
        }
    }

    @Composable
    private fun Grades(grades: SubjectReport.Grades?) {
        Row(
            modifier = Modifier
                .padding(16.dp)
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
                Column(
                    modifier = Modifier
                        .width(((devicesWidth - 80) / 4).dp),
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
                                setLocale(Locale("ru"))
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

    @Composable
    private fun Selection(
        selectedStart: String?,
        selectedEnd: String?,
        selectedSubject: String?,
        onExpandStartSelection: () -> Unit,
        onExpandEndSelection: () -> Unit,
        onExpandSubjectSelection: () -> Unit,
        onContinue: () -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.subject_report_period),
                style = Typography.subtitle2.copy(color = LocalNetSchoolColors.current.textMain),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            VSpace(8.dp)
            PeriodSelection(
                selectedStart = selectedStart,
                selectedEnd = selectedEnd,
                onExpandStartSelection = onExpandStartSelection,
                onExpandEndSelection = onExpandEndSelection
            )
            VSpace(16.dp)
            Text(
                text = stringResource(R.string.subject_report_period),
                style = Typography.subtitle2.copy(color = LocalNetSchoolColors.current.textMain),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            VSpace(8.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        color = LocalNetSchoolColors.current.backgroundCard,
                        shape = Shapes.medium
                    )
                    .border(
                        width = 1.dp,
                        color = LocalNetSchoolColors.current.divider,
                        shape = Shapes.medium
                    )
                    .clip(Shapes.medium)
                    .clickable(
                        enabled = selectedStart != null && selectedEnd != null,
                        onClick = onExpandSubjectSelection
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_subject_unknown),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .loading(selectedStart == null && selectedEnd == null),
                    tint = LocalNetSchoolColors.current.accentMain
                )
                Text(
                    text = selectedSubject
                        ?: stringResource(R.string.subject_report_select_subject),
                    style = Typography.body1.copy(
                        color = if (selectedSubject != null) {
                            LocalNetSchoolColors.current.textMain
                        } else {
                            LocalNetSchoolColors.current.textSecondary
                        }
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .loading(selectedStart == null && selectedEnd == null)
                        .weight(1f)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_expand_collapse),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .loading(selectedStart == null && selectedEnd == null),
                    tint = LocalNetSchoolColors.current.accentMain
                )
            }
            VSpace(16.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        color = animateColorAsState(
                            if (selectedSubject == null) {
                                LocalNetSchoolColors.current.accentInactive
                            } else {
                                LocalNetSchoolColors.current.accentMain
                            }
                        ).value,
                        shape = Shapes.medium
                    )
                    .clip(Shapes.medium)
                    .clickable(
                        enabled = selectedSubject != null,
                        onClick = onContinue
                    )
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
    }

    @Composable
    private fun PeriodSelection(
        selectedStart: String?,
        selectedEnd: String?,
        onExpandStartSelection: () -> Unit,
        onExpandEndSelection: () -> Unit,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            @Composable
            fun Item(
                text: String?,
                onClick: () -> Unit,
            ) {
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .background(
                            color = LocalNetSchoolColors.current.backgroundCard,
                            shape = Shapes.medium
                        )
                        .border(
                            width = 1.dp,
                            color = LocalNetSchoolColors.current.divider,
                            shape = Shapes.medium
                        )
                        .clip(Shapes.medium)
                        .clickable(
                            onClick = onClick,
                            enabled = selectedStart != null && selectedEnd != null
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = text ?: "00.00.0000",
                        style = Typography.body1.copy(color = LocalNetSchoolColors.current.textMain),
                        modifier = Modifier
                            .animateContentSize()
                            .loading(text == null)
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_expand_collapse),
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .loading(text == null),
                        tint = LocalNetSchoolColors.current.accentMain
                    )
                }
            }

            Item(text = selectedStart, onClick = onExpandStartSelection)
            // cringe timeline, i'm lazy to use canvas
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(LocalNetSchoolColors.current.divider),
                )
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                color = LocalNetSchoolColors.current.divider,
                                shape = CircleShape
                            )
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(LocalNetSchoolColors.current.divider)
                )
            }
            Item(text = selectedEnd, onClick = onExpandEndSelection)
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun Toolbar(
        subjectName: String?,
        range: String?,
        showDivider: Boolean,
        onResetChoice: () -> Unit
    ) {
        val navigator = LocalNavigator.currentOrThrow

        Column {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        56.dp + WindowInsets.statusBars
                            .asPaddingValues()
                            .calculateTopPadding()
                    )
                    .background(LocalNetSchoolColors.current.backgroundMain)
                    .statusBarsPadding(),
                navigationIcon = {
                    TopAppBarIcon(
                        iconPainter = painterResource(R.drawable.ic_arrow_back),
                        tint = LocalNetSchoolColors.current.accentMain,
                        onClick = { navigator.pop() }
                    )
                },
                title = {
                    AnimatedContent(
                        targetState = subjectName to range,
                        transitionSpec = {
                            (fadeIn(tween()) + slideInVertically(tween()) { (it * 0.95f).toInt() } with
                                    fadeOut(tween()) + slideOutVertically(tween()) { (it * -0.95f).toInt() })
                                .using(SizeTransform(clip = false))
                        }
                    ) { pair ->
                        if (pair.first != null && pair.second != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = onResetChoice
                                    ),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = pair.first!!,
                                    style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = pair.second!!,
                                    style = Typography.body2.copy(color = LocalNetSchoolColors.current.textSecondary),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.reports_subject_name),
                                style = Typography.h4.copy(color = LocalNetSchoolColors.current.textMain),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
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
        BackHandler {
            selectedSubjectResult = null
            navigator.pop()
        }
    }
}