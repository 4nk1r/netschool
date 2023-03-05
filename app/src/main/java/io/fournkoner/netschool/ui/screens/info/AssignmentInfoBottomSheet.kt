package io.fournkoner.netschool.ui.screens.info

import android.os.Parcelable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.journal.AssignmentDetailed
import io.fournkoner.netschool.ui.components.*
import io.fournkoner.netschool.ui.navigation.AssignmentParcelable
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.getGradeColor
import kotlinx.parcelize.Parcelize
import splitties.collections.forEachWithIndex
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

@Parcelize
data class AssignmentInfoBottomSheet(
    private val assigns: List<AssignmentParcelable>,
) : AndroidScreen(), Parcelable {

    @OptIn(UnreliableToastApi::class)
    @Composable
    override fun Content() = BottomSheet {
        val viewModel = getScreenModel<AssignmentInfoViewModel, AssignmentInfoViewModel.Factory> {
            it.create(assigns.map { a -> a.toDomain() })
        }
        val sheetNavigator = LocalBottomSheetNavigator.current
        val state = rememberScrollState()
        val assignments = viewModel.assignments.collectAsState()

        SimpleBottomSheetToolbar(
            title = stringResource(R.string.assignment_title),
            showDivider = state.value > 0
        ) { sheetNavigator.hide() }
        LoadingTransition(targetState = assignments.value) { list ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state)
                    .padding(16.dp)
            ) {
                (list.takeIf { it.isNotEmpty() } ?: listOf(null))
                    .forEachWithIndex { index, assignment ->
                        TitledContent(
                            title = assignment?.type,
                            content = assignment?.name,
                            grade = assignment?.grade,
                        )
                        if (assignment?.description != null) {
                            VSpace(12.dp)
                            TitledContent(
                                title = stringResource(R.string.assignment_description),
                                content = assignment.description,
                            )
                        }
                        if (!assignment?.attachments.isNullOrEmpty()) {
                            val context = LocalContext.current

                            VSpace(12.dp)
                            TitledFiles(assignment!!.attachments) { file ->
                                toast(context.getString(R.string.downloading_started))
                                viewModel.downloadFile(file, context)
                            }
                        }
                        if (index < list.size - 1) {
                            Divider(
                                color = LocalNetSchoolColors.current.divider,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                Divider(
                    color = LocalNetSchoolColors.current.divider,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                SecondaryTitledContent(
                    title = stringResource(R.string.assignment_subject).takeIf { list.isNotEmpty() },
                    content = list.firstOrNull()?.subject
                )
                VSpace(8.dp)
                SecondaryTitledContent(
                    title = stringResource(R.string.assignment_teacher).takeIf { list.isNotEmpty() },
                    content = list.firstOrNull()?.teacher
                )
                VSpace(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
            }
        }
    }
}

@Composable
private fun TitledFiles(
    attachments: List<AssignmentDetailed.Attachment>,
    download: (AssignmentDetailed.Attachment) -> Unit,
) {
    TitleText(stringResource(R.string.assignment_attachments))
    VSpace(12.dp)
    Files(files = attachments, download = download)
}

@Composable
private fun TitledContent(
    title: String?,
    content: String?,
    grade: Int? = null,
    copyable: Boolean = true,
) {
    TitleText(title)
    VSpace(4.dp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContentText(text = content, copyable = copyable)
        if (grade != null) {
            Text(
                text = grade.toString(),
                style = Typography.h4.copy(color = grade.getGradeColor()),
                modifier = Modifier.defaultMinSize(minWidth = 42.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TitleText(text: String?) {
    Text(
        text = text ?: "placeholder",
        style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.loading(text == null)
    )
}

@Composable
private fun RowScope.ContentText(
    text: String?,
    copyable: Boolean,
) {
    @Composable
    fun Text() {
        Text(
            text = text ?: "placeholder",
            style = Typography.body1.copy(color = LocalNetSchoolColors.current.textSecondary),
            modifier = Modifier
                .weight(1f)
                .loading(text == null)
        )
    }

    if (copyable && text != null) SelectionContainer(
        modifier = Modifier.weight(1f)
    ) { Text() } else Text()
}

@Composable
private fun Files(
    files: List<AssignmentDetailed.Attachment>,
    download: (AssignmentDetailed.Attachment) -> Unit,
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
    ) {
        files.forEachIndexed { index, file ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { download(file) }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = file.name,
                    style = Typography.button.copy(color = LocalNetSchoolColors.current.accentMain),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = stringResource(R.string.assignment_download, file.name),
                    tint = LocalNetSchoolColors.current.accentMain
                )
            }
            if (index < files.size - 1) {
                Divider(color = LocalNetSchoolColors.current.divider)
            }
        }
    }
}

@Composable
private fun SecondaryTitledContent(
    title: String?,
    content: String?,
) {
    SecondaryTitleText(title)
    VSpace(2.dp)
    SecondaryContentText(content)
}

@Composable
private fun SecondaryTitleText(text: String?) {
    Text(
        text = text ?: "placeholder",
        style = Typography.subtitle1.copy(color = LocalNetSchoolColors.current.textMain),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.loading(text == null)
    )
}

@Composable
private fun SecondaryContentText(text: String?) {
    Text(
        text = text ?: "placeholder",
        style = Typography.body2.copy(color = LocalNetSchoolColors.current.textSecondary),
        modifier = Modifier.loading(text == null)
    )
}