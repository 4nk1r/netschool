package io.fournkoner.netschool.ui.screens.journal

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.journal.Journal
import io.fournkoner.netschool.ui.components.LoadingTransition
import io.fournkoner.netschool.ui.components.SimpleToolbar
import io.fournkoner.netschool.ui.components.VSpace
import io.fournkoner.netschool.ui.components.loading
import io.fournkoner.netschool.ui.screens.journal.info.AssignmentInfoBottomSheet
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.getFormattedTime
import io.fournkoner.netschool.utils.getGradeColor
import io.fournkoner.netschool.utils.parcelables.toParcelable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import splitties.collections.forEachWithIndex

class JournalScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel: JournalViewModel = getViewModel()
        val sheetNavigator = LocalBottomSheetNavigator.current

        val journal = viewModel.journal.collectAsState()
        val week = viewModel.week.collectAsState()

        val scope = rememberCoroutineScope()
        val state = rememberLazyListState()

        val fadeAnimDuration = 300

        Scaffold(
            topBar = { Toolbar(state) },
            content = { paddingValues ->
                LoadingTransition(
                    targetState = journal.value,
                    fadeAnimDuration = fadeAnimDuration
                ) { journal ->
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        state = state,
                        userScrollEnabled = journal != null,
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        if (!journal?.overdueClasses.isNullOrEmpty()) {
                            item {
                                OverdueClasses(classes = journal!!.overdueClasses)
                            }
                        }

                        val list = (journal?.days ?: (0..4).map { null })
                        list.forEachWithIndex { index, day ->
                            Day(day = day) { clazz ->
                                sheetNavigator.show(
                                    AssignmentInfoBottomSheet(clazz.assignments.map { it.toParcelable() })
                                )
                            }
                            if (index < list.size - 1) {
                                item {
                                    VSpace(16.dp)
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                WeekSelector(currentWeek = week.value, onPreviousClicked = {
                    viewModel.previousWeek()
                    scope.launch {
                        delay(fadeAnimDuration.toLong())
                        state.scrollToItem(0)
                    }
                }, onNextClick = {
                        viewModel.nextWeek()
                        scope.launch {
                            delay(fadeAnimDuration.toLong())
                            state.scrollToItem(0)
                        }
                    })
            },
            backgroundColor = LocalNetSchoolColors.current.backgroundMain
        )
    }
}

private fun LazyListScope.Day(
    day: Journal.Day?,
    onClickClass: (Journal.Class) -> Unit
) {
    item {
        val name by remember(day) { mutableStateOf(day?.date?.getFormattedTime("EEEE")) }
        val date by remember(day) { mutableStateOf(day?.date?.getFormattedTime("d LLL yyyy г.")) }
        DayName(
            name = name,
            date = date
        )
    }
    item { VSpace(8.dp) }
    DayClasses(
        classes = day?.classes,
        onClick = onClickClass
    )
}

private fun LazyListScope.DayClasses(
    classes: List<Journal.Class>?,
    onClick: (Journal.Class) -> Unit
) {
    item { Divider(color = LocalNetSchoolColors.current.divider) }
    itemsIndexed(classes ?: (0..6).map { null }) { index, clazz ->
        Class(
            clazz = clazz,
            onClick = onClick
        )
        if (index < (classes?.size ?: 7) - 1) {
            Divider(
                color = LocalNetSchoolColors.current.divider,
                startIndent = 70.dp,
                modifier = Modifier.background(LocalNetSchoolColors.current.backgroundCard)
            )
        }
    }
    item { Divider(color = LocalNetSchoolColors.current.divider) }
}

@Composable
private fun Class(
    clazz: Journal.Class?,
    onClick: (Journal.Class) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .background(LocalNetSchoolColors.current.backgroundCard)
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
                    text = clazz?.assignments?.firstOrNull()?.name
                        ?: if (clazz == null) "placeholder" else "",
                    style = Typography.body2.copy(color = LocalNetSchoolColors.current.textSecondary),
                    modifier = Modifier.loading(clazz == null),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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
    date: String?
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
    onNextClick: () -> Unit
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
                isEnabled: Boolean
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
                    (slideInVertically { -it / 2 } + fadeIn() with slideOutVertically { it / 2 } + fadeOut()).using(
                        SizeTransform(clip = false)
                    )
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
private fun OverdueClasses(classes: List<Journal.OverdueClass>) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .background(LocalNetSchoolColors.current.backgroundCardNegative)
    ) {
        Divider(color = LocalNetSchoolColors.current.dividerOnNegative)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.journal_overdue_classes),
                style = Typography.h6.copy(color = LocalNetSchoolColors.current.gradeOnus),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_expand_collapse),
                contentDescription = if (isExpanded) {
                    stringResource(R.string.journal_overdue_classes_collapse)
                } else {
                    stringResource(R.string.journal_overdue_classes_expand)
                },
                modifier = Modifier.rotate(animateFloatAsState(if (isExpanded) 180f else 0f).value),
                tint = LocalNetSchoolColors.current.gradeOnus
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                classes.forEachIndexed { index, clazz ->
                    OverdueClass(clazz)
                    if (index < classes.size - 1) {
                        Divider(
                            color = LocalNetSchoolColors.current.dividerOnNegative,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            )
                        )
                    }
                }
            }
        }
        Divider(color = LocalNetSchoolColors.current.dividerOnNegative)
    }
}

@Composable
private fun OverdueClass(clazz: Journal.OverdueClass) {
    Column {
        Text(
            text = clazz.subject,
            style = Typography.h6.copy(color = LocalNetSchoolColors.current.textMain),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stringResource(
                R.string.journal_due_date,
                clazz.due.getFormattedTime("dd.MM.yyyy")
            ),
            style = Typography.caption.copy(color = LocalNetSchoolColors.current.textSecondary)
        )
        VSpace(8.dp)
        Text(
            text = clazz.name,
            style = Typography.body1.copy(color = LocalNetSchoolColors.current.textMain)
        )
    }
}
