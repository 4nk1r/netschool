package io.fournkoner.netschool.ui.screens.reports.total

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.reports.FinalReportPeriod
import io.fournkoner.netschool.ui.components.LoadingTransition
import io.fournkoner.netschool.ui.components.TopAppBarIcon
import io.fournkoner.netschool.ui.components.VSpace
import io.fournkoner.netschool.ui.components.loading
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.getIconPainter
import kotlinx.coroutines.launch
import splitties.collections.forEachWithIndex

class FinalReportScreen : AndroidScreen() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel: FinalReportViewModel = getViewModel()
        val pagerState = rememberPagerState()
        val listState = rememberScrollState()
        val showDivider by remember {
            derivedStateOf {
                listState.value > 0
            }
        }

        val report = viewModel.report.collectAsState()

        Scaffold(
            topBar = {
                Toolbar(
                    tabs = report.value?.map { it.name },
                    pagerState = pagerState,
                    showDivider = showDivider
                )
            },
            content = {
                HorizontalPager(
                    pageCount = report.value?.size ?: 1,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = report.value != null,
                    state = pagerState,
                    beyondBoundsPageCount = 5
                ) { pageNumber ->
                    SubjectsList(
                        subjects = report.value?.getOrNull(pageNumber)?.subjects,
                        state = listState
                    )
                }
            }
        )
    }

    @Composable
    private fun SubjectsList(
        subjects: List<FinalReportPeriod.Subject>?,
        state: ScrollState
    ) {
        LoadingTransition(targetState = subjects) { list ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state, enabled = subjects != null)
            ) {
                VSpace(8.dp)
                (list ?: (0..20).map { null }).forEachWithIndex { index, subject ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                if (index % 2 == 1) {
                                    LocalNetSchoolColors.current.backgroundCard
                                } else {
                                    LocalNetSchoolColors.current.backgroundMain
                                }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = subject?.name?.getIconPainter()
                                ?: painterResource(R.drawable.ic_subject_unknown),
                            contentDescription = subject?.name,
                            tint = LocalNetSchoolColors.current.accentMain,
                            modifier = Modifier.loading(subject == null)
                        )
                        Text(
                            text = subject?.name ?: "",
                            style = Typography.body1.copy(color = LocalNetSchoolColors.current.textMain),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .loading(subject == null)
                                .weight(1f)
                        )
                        Text(
                            text = subject?.grade ?: "??",
                            style = Typography.h4.copy(color = subject?.grade?.toIntOrNull().getGradeColor()),
                            modifier = Modifier.loading(subject == null)
                        )
                    }
                    if (index < (list?.size ?: 20) - 1) {
                        Divider(
                            color = LocalNetSchoolColors.current.divider
                        )
                    }
                }
                VSpace(8.dp)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Toolbar(
        tabs: List<String>?,
        pagerState: PagerState,
        showDivider: Boolean
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
                        onClick = navigator::pop
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.reports_results_name),
                        style = Typography.h4.copy(color = LocalNetSchoolColors.current.textMain),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                backgroundColor = LocalNetSchoolColors.current.backgroundMain,
                elevation = 0.dp
            )
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                color = LocalNetSchoolColors.current.accentMain.copy(alpha = 0.1f),
                                shape = Shapes.small
                            )
                    )
                },
                divider = {},
                edgePadding = 16.dp,
                backgroundColor = LocalNetSchoolColors.current.backgroundMain,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                val scope = rememberCoroutineScope()

                (tabs ?: (0..2).map { null }).forEachIndexed { index, tab ->
                    Tab(
                        selected = index == pagerState.currentPage,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        modifier = Modifier.clip(Shapes.small)
                    ) {
                        Text(
                            text = tab ?: "placeholder",
                            style = Typography.subtitle1.copy(
                                color = LocalNetSchoolColors.current.accentMain
                            ),
                            modifier = Modifier
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 12.dp
                                )
                                .animateContentSize()
                                .loading(tab == null)
                        )
                    }
                }
            }
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
    private fun Int?.getGradeColor(): Color {
        return when (this) {
            5 -> LocalNetSchoolColors.current.gradeGreat
            4 -> LocalNetSchoolColors.current.grateGood
            3 -> LocalNetSchoolColors.current.gradeSatisfactory
            2 -> LocalNetSchoolColors.current.gradeBad
            else -> LocalNetSchoolColors.current.accentInactive
        }
    }

    override val key = uniqueScreenKey
}
