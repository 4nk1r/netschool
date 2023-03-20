package io.fournkoner.netschool.ui.screens.reports

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.components.SimpleToolbar
import io.fournkoner.netschool.ui.screens.HelloWorldScreen
import io.fournkoner.netschool.ui.screens.short_report.ShortReportScreen
import io.fournkoner.netschool.ui.screens.subject_report.SubjectReportScreen
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography

class ReportsScreen : AndroidScreen() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val state = rememberLazyListState()
        val showDivider by remember {
            derivedStateOf {
                state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0
            }
        }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                SimpleToolbar(
                    title = stringResource(R.string.bn_reports),
                    showDivider = showDivider
                )
            },
            content = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = state
                ) {
                    item {
                        ReportCard(
                            icon = painterResource(R.drawable.ic_report_short),
                            name = stringResource(R.string.reports_short_name),
                            description = stringResource(R.string.reports_short_description)
                        ) { navigator.push(ShortReportScreen()) }
                    }
                    item {
                        ReportCard(
                            icon = painterResource(R.drawable.ic_report_full),
                            name = stringResource(R.string.reports_full_name),
                            description = stringResource(R.string.reports_full_description)
                        ) { navigator.push(HelloWorldScreen()) }
                    }
                    item {
                        ReportCard(
                            icon = painterResource(R.drawable.ic_report_subject),
                            name = stringResource(R.string.reports_subject_name),
                            description = stringResource(R.string.reports_subject_description)
                        ) { navigator.push(SubjectReportScreen()) }
                    }
                    item {
                        ReportCard(
                            icon = painterResource(R.drawable.ic_report_results),
                            name = stringResource(R.string.reports_results_name),
                            description = stringResource(R.string.reports_result_description)
                        ) { navigator.push(HelloWorldScreen()) }
                    }
                }
            },
            backgroundColor = LocalNetSchoolColors.current.backgroundMain
        )
    }
}

@Composable
private fun ReportCard(
    icon: Painter,
    name: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
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
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = name,
            tint = LocalNetSchoolColors.current.accentMain
        )
        Text(
            text = name,
            style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = description,
            style = Typography.body1.copy(color = LocalNetSchoolColors.current.textSecondary),
        )
    }
}