package io.fournkoner.netschool.ui.screens.info

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.journal.AssignmentDetailed
import io.fournkoner.netschool.domain.entities.journal.Journal
import io.fournkoner.netschool.ui.components.*
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.getGradeColor
import splitties.collections.forEachWithIndex

@Composable
fun AssignmentInfoBottomSheet(
    assigns: List<Journal.Class.Assignment>,
    navController: NavController,
    viewModel: AssignmentInfoViewModel = hiltViewModel(),
) = BottomSheet {
    val state = rememberScrollState()
    val assignments = viewModel.assignments.collectAsState()

    SimpleBottomSheetToolbar(
        title = stringResource(R.string.assignment_title),
        showDivider = state.value > 0
    ) { navController.popBackStack() }
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
                        title = assignment?.type?.getAssignmentTypeName(),
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

    LaunchedEffect(assigns) {
        viewModel.init(assigns)
    }
}

@Composable
private fun TitledFiles(
    attachments: List<AssignmentDetailed.Attachment>,
    download: (AssignmentDetailed.Attachment) -> Unit
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.ContentText(
    text: String?,
    copyable: Boolean,
) {
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    Text(
        text = text ?: "placeholder",
        style = Typography.body1.copy(color = LocalNetSchoolColors.current.textSecondary),
        modifier = Modifier
            .weight(1f)
            .combinedClickable(
                enabled = copyable && text != null,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    clipboardManager.setText(buildAnnotatedString { append(text!!) })
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {}
            )
            .loading(text == null)
    )
}

@Composable
private fun Files(
    files: List<AssignmentDetailed.Attachment>,
    download: (AssignmentDetailed.Attachment) -> Unit
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

@Composable
private fun Journal.Class.Assignment.Type.getAssignmentTypeName() = when (this) {
    Journal.Class.Assignment.Type.Homework -> stringResource(R.string.assignment_homework)
    Journal.Class.Assignment.Type.IndependentWork -> stringResource(R.string.assignment_independent_work)
    Journal.Class.Assignment.Type.Answer -> stringResource(R.string.assignment_answer)
    Journal.Class.Assignment.Type.PracticalWork -> stringResource(R.string.assignment_practical_word)
    Journal.Class.Assignment.Type.Unknown -> stringResource(R.string.assignment_unknown)
}