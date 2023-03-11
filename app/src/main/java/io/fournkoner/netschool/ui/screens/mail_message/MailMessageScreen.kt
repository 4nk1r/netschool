package io.fournkoner.netschool.ui.screens.mail_message

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.mail.MailMessageDetailed
import io.fournkoner.netschool.domain.entities.mail.Mailbox
import io.fournkoner.netschool.ui.components.LoadingTransition
import io.fournkoner.netschool.ui.components.TopAppBarIcon
import io.fournkoner.netschool.ui.components.loading
import io.fournkoner.netschool.ui.screens.mailbox.MailboxScreen
import io.fournkoner.netschool.ui.screens.new_message.NewMessageScreen
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.formatDate
import io.fournkoner.netschool.utils.parcelables.MailMessageReceiverParcelable
import io.fournkoner.netschool.utils.parcelables.toParcelable
import splitties.toast.UnreliableToastApi
import splitties.toast.toast
import java.util.*

data class MailMessageScreen(
    private val id: Int,
    private val mailbox: Mailbox,
) : AndroidScreen() {

    @OptIn(UnreliableToastApi::class)
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MailMessageViewModel, MailMessageViewModel.Factory> {
            it.create(id, mailbox)
        }
        val state = rememberLazyListState()
        val navigator = LocalNavigator.currentOrThrow

        val message = viewModel.message.collectAsState()
        val senderId = viewModel.senderId.collectAsState()
        val showDivider by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }

        Scaffold(
            topBar = {
                Toolbar(
                    title = message.value?.subject ?: "",
                    showDivider = showDivider,
                    onDelete = {
                        viewModel.deleteMessage()

                        // shitty way to achieve the "start for result" behavior
                        MailboxScreen.deletedMessages += id
                        navigator.pop()
                    }
                )
            },
            content = {
                LoadingTransition(message.value) { msg ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp + it.calculateBottomPadding(),
                        )
                    ) {
                        item { Subject(title = msg?.subject) }
                        item { Details(message = msg) }
                        item {
                            Title(
                                text = stringResource(R.string.message_body),
                                isLoading = msg == null
                            )
                        }
                        item { Body(msg?.body) }
                        if (!msg?.attachments.isNullOrEmpty()) {
                            item {
                                Title(
                                    text = stringResource(R.string.message_attachments),
                                    isLoading = msg == null
                                )
                            }
                            item {
                                val context = LocalContext.current
                                Files(files = msg!!.attachments) {
                                    toast(context.getString(R.string.downloading_started))
                                    viewModel.downloadFile(it, context)
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                BottomButtons(
                    onReply = {
                        navigator.push(
                            NewMessageScreen(
                                openMode = NewMessageScreen.OpenMode.REPLY,
                                receiver = senderId.value?.let {
                                    MailMessageReceiverParcelable(
                                        id = it,
                                        name = message.value!!.sender
                                    )
                                },
                                message = message.value?.toParcelable()
                            )
                        )
                    },
                    onForward = {
                        navigator.push(
                            NewMessageScreen(
                                openMode = NewMessageScreen.OpenMode.FORWARD,
                                message = message.value?.toParcelable()
                            )
                        )
                    },
                    isLoading = message.value == null
                )
            },
            backgroundColor = LocalNetSchoolColors.current.backgroundMain
        )
    }

    @Composable
    private fun Subject(title: String?) {
        Text(
            text = (title ?: "placeholder".repeat(5))
                .takeIf { it.isNotEmpty() } ?: stringResource(R.string.mail_no_subject),
            style = Typography.h5.copy(
                color = LocalNetSchoolColors.current.textMain,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.loading(title == null)
        )
    }

    @Composable
    private fun Body(body: String?) {
        @Composable
        fun Text() {
            Text(
                text = body ?: "placeholder".repeat(20),
                style = Typography.body1.copy(color = LocalNetSchoolColors.current.textSecondary),
                modifier = Modifier.loading(body == null)
            )
        }
        if (body != null) SelectionContainer {
            Text()
        } else Text()
    }

    @Composable
    private fun Details(message: MailMessageDetailed?) {
        var isExpanded by rememberSaveable { mutableStateOf(false) }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable(enabled = message != null) { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.message_additional_info),
                    style = Typography.button.copy(color = LocalNetSchoolColors.current.accentMain),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .loading(message == null)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_expand_collapse),
                    contentDescription = stringResource(R.string.message_expand),
                    tint = LocalNetSchoolColors.current.accentMain,
                    modifier = Modifier
                        .alpha(if (message == null) 0f else 1f)
                        .rotate(animateFloatAsState(if (isExpanded) 180f else 0f).value)
                        .size(24.dp)
                )
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                val context = LocalContext.current
                val map = remember(message) {
                    mapOf(
                        context.getString(R.string.message_when) to (message?.date?.formatDate()
                            ?: ""),
                        context.getString(R.string.message_from) to (message?.sender ?: ""),
                        context.getString(R.string.message_sent) to (message?.receivers ?: ""),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    map.forEach { (name, value) ->
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    Typography.body2.copy(
                                        color = LocalNetSchoolColors.current.textMain,
                                        fontWeight = FontWeight.SemiBold
                                    ).toSpanStyle()
                                ) { append("$name: ") }
                                withStyle(
                                    Typography.body2.copy(
                                        color = LocalNetSchoolColors.current.textSecondary,
                                    ).toSpanStyle()
                                ) { append(value) }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomButtons(
        onReply: () -> Unit,
        onForward: () -> Unit,
        isLoading: Boolean,
    ) {
        @Composable
        fun Button(
            onClick: () -> Unit,
            content: @Composable RowScope.() -> Unit,
        ) {
            val configuration = LocalConfiguration.current
            val width = remember(configuration) { (configuration.screenWidthDp - 48) / 2 }

            Row(
                modifier = Modifier
                    .height(48.dp)
                    .width(width.dp)
                    .clip(CircleShape)
                    .background(
                        color = LocalNetSchoolColors.current.backgroundCard,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = LocalNetSchoolColors.current.divider,
                        shape = CircleShape
                    )
                    .clickable(enabled = !isLoading, onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                content = content
            )
        }

        AnimatedVisibility(
            visible = !isLoading,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                LocalNetSchoolColors.current.backgroundMain,
                                LocalNetSchoolColors.current.backgroundMain,
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = onReply) {
                    Icon(
                        painter = painterResource(R.drawable.ic_reply),
                        contentDescription = stringResource(R.string.message_reply),
                        tint = LocalNetSchoolColors.current.accentInactive
                    )
                    Text(
                        text = stringResource(R.string.message_reply),
                        style = Typography.button.copy(color = LocalNetSchoolColors.current.accentInactive),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Button(onClick = onForward) {
                    Text(
                        text = stringResource(R.string.message_forward),
                        style = Typography.button.copy(color = LocalNetSchoolColors.current.accentInactive),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_forward),
                        contentDescription = stringResource(R.string.message_forward),
                        tint = LocalNetSchoolColors.current.accentInactive
                    )
                }
            }
        }
    }

    @Composable
    private fun Toolbar(
        title: String,
        showDivider: Boolean,
        onDelete: () -> Unit,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val topPadding = WindowInsets.statusBars
            .asPaddingValues()
            .calculateTopPadding()

        Column {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = showDivider,
                        enter = fadeIn(tween(200)),
                        exit = fadeOut(tween(200))
                    ) {
                        Text(
                            text = title,
                            style = Typography.h4.copy(color = LocalNetSchoolColors.current.textMain),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    TopAppBarIcon(
                        iconPainter = painterResource(R.drawable.ic_arrow_back),
                        tint = LocalNetSchoolColors.current.accentMain,
                        onClick = { navigator.pop() }
                    )
                },
                actions = {
                    TopAppBarIcon(
                        iconPainter = painterResource(R.drawable.ic_trash_bin),
                        tint = LocalNetSchoolColors.current.gradeBad,
                        onClick = onDelete
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp + topPadding)
                    .background(LocalNetSchoolColors.current.backgroundMain)
                    .padding(top = topPadding),
                backgroundColor = LocalNetSchoolColors.current.backgroundMain,
                elevation = 0.dp
            )
            AnimatedVisibility(
                visible = showDivider,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) { Divider(color = LocalNetSchoolColors.current.divider) }
        }
    }

    @Composable
    private fun Files(
        files: List<MailMessageDetailed.Attachment>,
        download: (MailMessageDetailed.Attachment) -> Unit,
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
                        contentDescription = stringResource(
                            R.string.assignment_download,
                            file.name
                        ),
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
    private fun Title(text: String, isLoading: Boolean) {
        Text(
            text = text,
            style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.loading(isLoading)
        )
    }
}