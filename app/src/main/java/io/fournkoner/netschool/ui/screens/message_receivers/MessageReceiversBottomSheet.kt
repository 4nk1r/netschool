package io.fournkoner.netschool.ui.screens.message_receivers

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiver
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiverGroup
import io.fournkoner.netschool.ui.components.BottomSheet
import io.fournkoner.netschool.ui.components.LoadingTransition
import io.fournkoner.netschool.ui.components.TopAppBarIcon
import io.fournkoner.netschool.ui.screens.HelloWorldScreen
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.ui.style.mediumDp
import kotlinx.coroutines.launch

class MessageReceiversBottomSheet : AndroidScreen() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() = BottomSheet {
        val viewModel: MessageReceiversViewModel = getViewModel()
        val state = rememberLazyListState()
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val scope = rememberCoroutineScope()

        val currentGroup = viewModel.currentGroup.collectAsState()
        val receivers = viewModel.receivers.collectAsState()
        val showDivider by remember {
            derivedStateOf {
                state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0
            }
        }

        var searchQuery by rememberSaveable { mutableStateOf("") }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    showDivider = showDivider,
                    currentGroup = currentGroup.value,
                    currentSearchQuery = searchQuery,
                    onUpdateGroup = {
                        viewModel.updateGroup(it)
                        if (showDivider) scope.launch { state.animateScrollToItem(0) }
                    },
                    onTypeSearch = {
                        searchQuery = it
                        viewModel.search(it)
                        if (showDivider) scope.launch { state.animateScrollToItem(0) }
                    }
                )
            },
            content = {
                LoadingTransition(targetState = receivers.value.isEmpty()) { isEmpty ->
                    if (!isEmpty) {
                        ReceiversList(
                            segmentedList = receivers.value,
                            state = state,
                            onClick = {
                                bottomSheetNavigator.hide()
                                navigator.push(HelloWorldScreen())
                            }
                        )
                    } else {
                        if (searchQuery.isNotEmpty()) {
                            NobodyFound()
                        }
                    }
                }
            },
            backgroundColor = LocalNetSchoolColors.current.backgroundMain
        )
    }

    @Composable
    private fun NobodyFound() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nobody_found),
                contentDescription = stringResource(R.string.select_receiver_nobody_found),
                modifier = Modifier.size(48.dp),
                tint = LocalNetSchoolColors.current.accentInactive
            )
            Text(
                text = stringResource(R.string.select_receiver_nobody_found),
                style = Typography.subtitle1.copy(color = LocalNetSchoolColors.current.accentInactive)
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ReceiversList(
        segmentedList: Map<Char, List<MailMessageReceiver>>,
        state: LazyListState,
        onClick: (MailMessageReceiver) -> Unit,
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
            segmentedList.forEach { (letter, receivers) ->
                item(key = letter) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .background(LocalNetSchoolColors.current.backgroundMain)
                            .padding(horizontal = 16.dp)
                            .animateItemPlacement(),
                    ) {
                        Text(
                            text = letter.toString(),
                            style = Typography.subtitle1.copy(LocalNetSchoolColors.current.textSecondary),
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        Divider(
                            color = LocalNetSchoolColors.current.divider,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }
                items(receivers, key = { it.id }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(LocalNetSchoolColors.current.backgroundMain)
                            .clickable { onClick(it) }
                            .padding(horizontal = 16.dp)
                            .animateItemPlacement(),
                    ) {
                        Text(
                            text = it.name,
                            style = Typography.subtitle2.copy(LocalNetSchoolColors.current.textMain),
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        Divider(
                            color = LocalNetSchoolColors.current.divider,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun Toolbar(
        showDivider: Boolean,
        currentGroup: MailMessageReceiverGroup,
        currentSearchQuery: String,
        onUpdateGroup: (MailMessageReceiverGroup) -> Unit,
        onTypeSearch: (String) -> Unit,
    ) {
        val navigator = LocalBottomSheetNavigator.current

        Column {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp + Shapes.mediumDp)
                    .background(LocalNetSchoolColors.current.backgroundMain)
                    .padding(top = Shapes.mediumDp),
                navigationIcon = {
                    TopAppBarIcon(
                        iconPainter = painterResource(R.drawable.ic_close),
                        tint = LocalNetSchoolColors.current.accentMain,
                        onClick = { navigator.hide() }
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.select_receiver_title),
                        style = Typography.h4.copy(color = LocalNetSchoolColors.current.textMain),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                backgroundColor = LocalNetSchoolColors.current.backgroundMain,
                elevation = 0.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SearchTextField(value = currentSearchQuery, onValueChanged = onTypeSearch)
                GroupSelector(value = currentGroup, onValueChanged = onUpdateGroup)
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

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun RowScope.SearchTextField(
        value: String,
        onValueChanged: (String) -> Unit,
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        BasicTextField(
            value = value,
            onValueChange = onValueChanged,
            singleLine = true,
            textStyle = Typography.body1.copy(color = LocalNetSchoolColors.current.textMain),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
            cursorBrush = SolidColor(LocalNetSchoolColors.current.accentMain),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .height(42.dp)
                .weight(1f),
        ) { innerTextField ->
            Row(
                modifier = Modifier
                    .height(42.dp)
                    .weight(1f)
                    .background(
                        color = LocalNetSchoolColors.current.backgroundCard,
                        shape = Shapes.small
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_content_description),
                    tint = LocalNetSchoolColors.current.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_content_description),
                            style = Typography.body1.copy(color = LocalNetSchoolColors.current.textSecondary)
                        )
                    }
                    innerTextField()
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun GroupSelector(
        value: MailMessageReceiverGroup,
        onValueChanged: (MailMessageReceiverGroup) -> Unit,
    ) {
        var isExpanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .height(42.dp)
                .widthIn(max = 128.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    (fadeIn(tween()) + slideInVertically(tween()) { (it * -0.95f).toInt() } with
                            fadeOut(tween()) + slideOutVertically(tween()) { (it * 0.95f).toInt() })
                        .using(SizeTransform(clip = false))
                }
            ) {
                Text(
                    text = value.uiName,
                    style = Typography.subtitle2.copy(color = LocalNetSchoolColors.current.accentMain),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box {
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_expand_collapse),
                        contentDescription = stringResource(R.string.select_receiver_change_group),
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
                    MailMessageReceiverGroup.values().forEach { group ->
                        DropdownMenuItem(
                            onClick = {
                                onValueChanged(group)
                                isExpanded = false
                            }
                        ) {
                            Text(
                                text = group.uiName,
                                color = LocalNetSchoolColors.current.textMain
                            )
                        }
                    }
                }
            }
        }
    }

    private val MailMessageReceiverGroup.uiName
        @Composable
        get() = when (this) {
            MailMessageReceiverGroup.TEACHERS -> stringResource(R.string.select_receiver_teachers)
            MailMessageReceiverGroup.STUDENTS -> stringResource(R.string.select_receiver_students)
        }
}
