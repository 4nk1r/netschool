package io.fournkoner.netschool.ui.screens.subject_report

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.components.BottomSheet
import io.fournkoner.netschool.ui.components.SimpleBottomSheetToolbar
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.getIconPainter

data class SubjectSelectionBottomSheet(
    private val subjects: Map<String, String>, //id to name
) : AndroidScreen() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() = BottomSheet {
        val state = rememberLazyListState()
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        val showDivider by remember {
            derivedStateOf {
                state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SimpleBottomSheetToolbar(
                    title = stringResource(R.string.subject_report_select_subject),
                    showDivider = showDivider,
                    onBack = bottomSheetNavigator::hide
                )
            },
            content = {
                SubjectList(
                    state = state,
                    onClick = {
                        SubjectReportScreen.selectedSubjectResult = it.first

                        bottomSheetNavigator.hide()
                    }
                )
            },
            backgroundColor = LocalNetSchoolColors.current.backgroundMain
        )
    }

    @Composable
    private fun SubjectList(
        state: LazyListState,
        onClick: (Pair<String, String>) -> Unit,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = 16.dp + WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
        ) {
            itemsIndexed(subjects.toList()) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(LocalNetSchoolColors.current.backgroundMain)
                        .clickable { onClick(item) }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = item.second.getIconPainter(),
                        contentDescription = item.second,
                        tint = LocalNetSchoolColors.current.accentMain
                    )
                    Text(
                        text = item.second,
                        style = Typography.body1.copy(LocalNetSchoolColors.current.textMain),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (index < subjects.size - 1) {
                    Divider(color = LocalNetSchoolColors.current.divider)
                }
            }
        }
    }
}
