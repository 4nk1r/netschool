package io.fournkoner.netschool.ui.screens.journal

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.Journal
import io.fournkoner.netschool.ui.components.SimpleToolbar
import io.fournkoner.netschool.ui.components.loading
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.getFormattedTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import splitties.collections.forEachWithIndex
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun JournalScreen(
    navController: NavController,
    viewModel: JournalViewModel = hiltViewModel(),
) {
    val journal = viewModel.journal.collectAsState()
    val week = viewModel.week.collectAsState()

    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()

    val fadeAnimDuration = 300

    Scaffold(
        topBar = { Toolbar(state) },
        content = { paddingValues ->
            AnimatedContent(
                targetState = journal.value,
                transitionSpec = {
                    fadeIn(tween(durationMillis = fadeAnimDuration, delayMillis = fadeAnimDuration)) with
                            fadeOut(tween(durationMillis = fadeAnimDuration))
                }
            ) { list ->
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    state = state,
                    userScrollEnabled = list != null,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(list?.days ?: (0..4).map { null }) { day ->
                        Day(day = day) { clazz ->
                            /*TODO*/
                        }
                    }
                }
            }
        },
        bottomBar = {
            WeekSelector(
                currentWeek = week.value,
                onPreviousClicked = {
                    viewModel.previousWeek()
                    scope.launch {
                        delay(fadeAnimDuration.toLong())
                        state.scrollToItem(0)
                    }
                },
                onNextClick = {
                    viewModel.nextWeek()
                    scope.launch {
                        delay(fadeAnimDuration.toLong())
                        state.scrollToItem(0)
                    }
                }
            )
        },
        backgroundColor = LocalNetSchoolColors.current.backgroundMain
    )
}

@Composable
private fun Day(
    day: Journal.Day?,
    onClickClass: (Journal.Class) -> Unit,
) {
    val name by remember(day) { mutableStateOf(day?.date?.getFormattedTime("EEEE")) }
    val date by remember(day) { mutableStateOf(day?.date?.getFormattedTime("d LLL yyyy г.")) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DayName(
            name = name,
            date = date
        )
        DayClasses(
            classes = day?.classes,
            onClick = onClickClass
        )
    }
}

@Composable
private fun DayClasses(
    classes: List<Journal.Class>?,
    onClick: (Journal.Class) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalNetSchoolColors.current.backgroundCard)
    ) {
        Divider(color = LocalNetSchoolColors.current.divider)
        (classes ?: (0..6).map { null }).forEachWithIndex { index, clazz ->
            Class(
                clazz = clazz,
                onClick = onClick
            )
            if (index < (classes?.size ?: 7) - 1) {
                Divider(
                    color = LocalNetSchoolColors.current.divider,
                    startIndent = 70.dp
                )
            }
        }
        Divider(color = LocalNetSchoolColors.current.divider)
    }
}

@Composable
private fun Class(
    clazz: Journal.Class?,
    onClick: (Journal.Class) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable(
                enabled = clazz != null && clazz.assignments.isNotEmpty(),
                onClick = { onClick(clazz!!) }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (clazz?.position ?: 0).toString(),
            style = Typography.h4.copy(color = LocalNetSchoolColors.current.textMain),
            modifier = Modifier
                .loading(clazz == null)
                .defaultMinSize(minWidth = 42.dp),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(if (clazz == null) 4.dp else 2.dp)
        ) {
            Text(
                text = clazz?.name ?: "placeholder",
                style = Typography.subtitle1.copy(color = LocalNetSchoolColors.current.textMain),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.loading(clazz == null)
            )
            if (clazz?.assignments?.isNotEmpty() == true || clazz == null) {
                Text(
                    text = clazz?.assignments?.firstOrNull { it.grade == null }?.name
                        ?: clazz?.assignments?.firstOrNull()?.name
                        ?: if (clazz == null) "placeholder" else "",
                    style = Typography.body2.copy(color = LocalNetSchoolColors.current.textSecondary),
                    modifier = Modifier.loading(clazz == null)
                )
            }
        }
        if (clazz?.grades?.isNotEmpty() == true) {
            Text(
                text = buildAnnotatedString {
                    clazz.grades.forEach {
                        withStyle(Typography.h4.copy(color = it.getGradeColor()).toSpanStyle()) {
                            append(it?.toString() ?: "•")
                        }
                    }
                },
                style = Typography.h4.copy(letterSpacing = 2.sp),
                modifier = Modifier.defaultMinSize(minWidth = 42.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DayName(
    name: String?,
    date: String?,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(if (name == date) 4.dp else 0.dp)
    ) {
        Text(
            text = name ?: "placeholder",
            style = Typography.h6.copy(color = LocalNetSchoolColors.current.textMain),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.loading(name == null)
        )
        Text(
            text = date ?: "placeholder",
            style = Typography.caption.copy(color = LocalNetSchoolColors.current.textSecondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.loading(date == null)
        )
    }
}

@Composable
private fun Toolbar(scrollState: LazyListState) {
    val showDivider = remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 0
        }
    }
    SimpleToolbar(
        title = stringResource(R.string.bn_journal),
        showDivider = showDivider.value
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun WeekSelector(
    currentWeek: String,
    onPreviousClicked: () -> Unit,
    onNextClick: () -> Unit,
) {
    var isPreviousLoading by remember(currentWeek) { mutableStateOf(false) }
    var isNextLoading by remember(currentWeek) { mutableStateOf(false) }

    Column {
        Divider(color = LocalNetSchoolColors.current.divider)
        Row(
            modifier = Modifier
                .height(42.dp)
                .fillMaxWidth()
                .background(LocalNetSchoolColors.current.backgroundCard)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            @Composable
            fun IconButton(
                icon: Painter,
                contentDescription: String,
                onClick: () -> Unit,
                isLoading: Boolean,
                isEnabled: Boolean,
            ) {
                val activityIndicator by animateFloatAsState(
                    if (isEnabled) 1f else 0.7f
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(42.dp)
                            .padding(12.dp),
                        color = LocalNetSchoolColors.current.accentInactive,
                        strokeWidth = 2.dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable(
                                enabled = isEnabled,
                                role = Role.Button,
                                onClickLabel = contentDescription,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(radius = 18.dp),
                                onClick = onClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = contentDescription,
                            tint = LocalNetSchoolColors.current.accentInactive,
                            modifier = Modifier
                                .scale(activityIndicator)
                                .alpha(activityIndicator)
                                .size(24.dp)
                        )
                    }
                }
            }

            AnimatedContent(isPreviousLoading) { isLoading ->
                IconButton(
                    icon = painterResource(R.drawable.ic_journal_previous_week),
                    contentDescription = stringResource(R.string.journal_previous_week_hint),
                    onClick = {
                        isPreviousLoading = true
                        onPreviousClicked()
                    },
                    isLoading = isLoading,
                    isEnabled = !isPreviousLoading && !isNextLoading
                )
            }
            AnimatedContent(
                targetState = currentWeek,
                transitionSpec = {
                    (slideInVertically { -it / 2 } + fadeIn() with
                            slideOutVertically { it / 2 } + fadeOut())
                        .using(SizeTransform(clip = false))
                },
                modifier = Modifier.weight(1f)
            ) { week ->
                Text(
                    text = week,
                    style = Typography.subtitle2.copy(
                        color = LocalNetSchoolColors.current.textSecondary
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AnimatedContent(isNextLoading) { isLoading ->
                IconButton(
                    icon = painterResource(R.drawable.ic_journal_next_week),
                    contentDescription = stringResource(R.string.journal_next_week_hint),
                    onClick = {
                        isNextLoading = true
                        onNextClick()
                    },
                    isLoading = isLoading,
                    isEnabled = !isPreviousLoading && !isNextLoading
                )
            }
        }
    }
}

@Composable
private fun Int?.getGradeColor(): Color {
    return when (this) {
        5 -> LocalNetSchoolColors.current.gradeGreat
        4 -> LocalNetSchoolColors.current.grateGood
        3 -> LocalNetSchoolColors.current.gradeSatisfactory
        2 -> LocalNetSchoolColors.current.gradeBad
        null -> LocalNetSchoolColors.current.gradeOnus
        else -> error("Unknown grade mark: $this")
    }
}