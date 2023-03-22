package io.fournkoner.netschool.ui.screens.mailbox

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.mail.MailMessageShort
import io.fournkoner.netschool.domain.entities.mail.Mailbox
import io.fournkoner.netschool.ui.components.LoadingTransition
import io.fournkoner.netschool.ui.components.loading
import io.fournkoner.netschool.ui.screens.mail_message.MailMessageScreen
import io.fournkoner.netschool.ui.screens.new_message.NewMessageScreen
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.Const
import io.fournkoner.netschool.utils.getMessageFormattedDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MailboxScreen : AndroidScreen() {

    companion object {

        // shitty way to achieve the "start for result" behavior
        val deletedMessages = mutableListOf<Int>()
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val viewModel: MailboxViewModel = getViewModel()
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        val inboxMessages = viewModel.inboxMessages.collectAsLazyPagingItems()
        val sentMessages = viewModel.sentMessages.collectAsLazyPagingItems()
        var current by rememberSaveable { mutableStateOf(Mailbox.INBOX) }

        val state = rememberLazyListState()
        val isRefreshing by remember {
            derivedStateOf {
                if (current == Mailbox.INBOX) inboxMessages.loadState.refresh is LoadState.Loading
                else sentMessages.loadState.refresh is LoadState.Loading
            }
        }
        val pullRefreshState = rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = {
                inboxMessages.refresh()
                sentMessages.refresh()
            }
        )
        val showDivider by remember {
            derivedStateOf { state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0 }
        }

        Scaffold(
            topBar = {
                Toolbar(current = current, showDivider = showDivider) {
                    if (it != current) {
                        current = it
                        scope.launch {
                            delay(300)
                            state.scrollToItem(0)
                        }
                    }
                }
            },
            content = {
                MessagesList(
                    current = current,
                    inboxMessages = inboxMessages,
                    sentMessages = sentMessages,
                    state = state,
                    contentPaddingValues = it,
                    pullRefreshState = pullRefreshState,
                    refreshing = isRefreshing
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navigator.push(NewMessageScreen(openMode = NewMessageScreen.OpenMode.NEW))
                    },
                    backgroundColor = LocalNetSchoolColors.current.accentMain,
                    contentColor = LocalNetSchoolColors.current.onAccent
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pen),
                        contentDescription = stringResource(R.string.mail_new_message)
                    )
                }
            },
            backgroundColor = LocalNetSchoolColors.current.backgroundMain
        )
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    private fun MessagesList(
        current: Mailbox,
        inboxMessages: LazyPagingItems<MailMessageShort>,
        sentMessages: LazyPagingItems<MailMessageShort>,
        state: LazyListState,
        contentPaddingValues: PaddingValues,
        pullRefreshState: PullRefreshState,
        refreshing: Boolean,
    ) {
        val navigator = LocalNavigator.currentOrThrow

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LoadingTransition(targetState = current) { mailbox ->
                val list = if (mailbox == Mailbox.INBOX) inboxMessages else sentMessages
                LoadingTransition(targetState = list.itemCount == 0) { isEmpty ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(pullRefreshState, enabled = !isEmpty),
                        contentPadding = PaddingValues(
                            top = contentPaddingValues.calculateTopPadding(),
                            start = contentPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            end = contentPaddingValues.calculateEndPadding(LayoutDirection.Ltr),
                            bottom = contentPaddingValues.calculateBottomPadding() + /*fab*/ 56.dp
                        ),
                        state = if (!isEmpty) state else LazyListState(),
                        userScrollEnabled = !isEmpty
                    ) {
                        if (!isEmpty) {
                            for (i in 0 until list.itemCount) {
                                val message = list[i]
                                if (!deletedMessages.contains(message?.id)) item(key = message?.id) {
                                    Message(message) {
                                        navigator.push(MailMessageScreen(message!!.id, mailbox))
                                    }
                                    if (i < list.itemCount - 1) {
                                        Divider(
                                            color = LocalNetSchoolColors.current.divider,
                                            modifier = Modifier.animateItemPlacement()
                                        )
                                    }
                                }
                            }
                        } else {
                            items(20) {
                                Message(message = null) {}
                                if (it < 19) {
                                    Divider(color = LocalNetSchoolColors.current.divider)
                                }
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                backgroundColor = LocalNetSchoolColors.current.backgroundMain,
                contentColor = LocalNetSchoolColors.current.accentMain
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun LazyItemScope.Message(message: MailMessageShort?, onClick: () -> Unit) {
        var isUnread by rememberSaveable(message) { mutableStateOf(message?.unread == true) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    animateColorAsState(
                        if (isUnread) LocalNetSchoolColors.current.accentMain.copy(alpha = 0.2f)
                        else LocalNetSchoolColors.current.backgroundMain,
                        tween(100)
                    ).value
                )
                .clickable(enabled = message != null) {
                    if (isUnread) {
                        isUnread = false
                        Const.mailUnreadMessages--
                    }
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .animateItemPlacement(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = (message?.subject ?: stringResource(R.string.mail_no_subject))
                        .run { if (message == null) repeat(3) else this },
                    style = Typography.subtitle1.copy(color = LocalNetSchoolColors.current.textMain),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.loading(message == null)
                )
                Text(
                    text = message?.sender ?: "placeholder".repeat(3),
                    style = Typography.body2.copy(color = LocalNetSchoolColors.current.textSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.loading(message == null)
                )
            }
            Text(
                text = message?.date?.getMessageFormattedDate() ?: "00.00",
                style = Typography.caption.copy(color = LocalNetSchoolColors.current.textSecondary),
                modifier = Modifier.loading(message == null)
            )
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun Toolbar(
        current: Mailbox,
        showDivider: Boolean,
        onChange: (Mailbox) -> Unit,
    ) {
        @Composable
        fun Mailbox.getName() = if (this == Mailbox.SENT) {
            stringResource(R.string.mail_sent)
        } else {
            stringResource(R.string.mail_inbox)
        }

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
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var isExpanded by remember { mutableStateOf(false) }
                        AnimatedContent(
                            targetState = current,
                            transitionSpec = {
                                (fadeIn(tween()) + slideInVertically(tween()) { (it * -0.95f).toInt() } with
                                        fadeOut(tween()) + slideOutVertically(tween()) { (it * 0.95f).toInt() })
                                    .using(SizeTransform(clip = false))
                            }
                        ) {
                            Text(
                                text = it.getName(),
                                style = Typography.h4.copy(color = LocalNetSchoolColors.current.textMain),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { isExpanded = !isExpanded }
                            )
                        }
                        Box {
                            IconButton(onClick = { isExpanded = !isExpanded }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_expand_collapse),
                                    contentDescription = stringResource(R.string.mail_expand_selection_desc),
                                    tint = LocalNetSchoolColors.current.accentMain,
                                    modifier = Modifier
                                        .rotate(animateFloatAsState(if (isExpanded) 180f else 0f).value)
                                        .size(24.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = isExpanded,
                                onDismissRequest = { isExpanded = false },
                                modifier = Modifier.background(LocalNetSchoolColors.current.backgroundCard)
                            ) {
                                Mailbox.values().forEach { value ->
                                    DropdownMenuItem(
                                        onClick = {
                                            onChange(value)
                                            isExpanded = false
                                        }
                                    ) {
                                        Text(
                                            text = value.getName(),
                                            color = LocalNetSchoolColors.current.textMain
                                        )
                                    }
                                }
                            }
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
    }
}