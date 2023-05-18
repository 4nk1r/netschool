package io.fournkoner.netschool.ui.screens.mail.newmsg

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Size
import android.webkit.MimeTypeMap
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiver
import io.fournkoner.netschool.ui.components.TopAppBarIcon
import io.fournkoner.netschool.ui.screens.mail.newmsg.receivers.MessageReceiversBottomSheet
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.debugValue
import io.fournkoner.netschool.utils.parcelables.MailMessageParcelable
import io.fournkoner.netschool.utils.parcelables.MailMessageReceiverParcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

@Parcelize
data class NewMessageScreen(
    private val openMode: OpenMode,
    private val message: MailMessageParcelable? = null,
    private val receiver: MailMessageReceiverParcelable? = null
) : AndroidScreen(), Parcelable {

    companion object {

        // shitty way to achieve the "start for result" behavior
        var selectedReceiver by mutableStateOf<MailMessageReceiver?>(null)
    }

    @OptIn(UnreliableToastApi::class, ExperimentalComposeUiApi::class)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter", "Range", "UnreliableToastApi")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel: NewMessageViewModel = getViewModel()
        val context = LocalContext.current
        val softwareKeyboardController = LocalSoftwareKeyboardController.current

        val state = rememberLazyListState()
        val showDivider by remember {
            derivedStateOf {
                state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0
            }
        }
        var isSendLoading by rememberSaveable { mutableStateOf(false) }

        val receiver by remember(selectedReceiver) {
            mutableStateOf(selectedReceiver ?: receiver?.toDomainObject())
        }
        var subject by rememberSaveable {
            mutableStateOf(
                if (message?.subject != null) {
                    when (openMode) {
                        OpenMode.FORWARD -> context.getString(
                            R.string.new_message_forwarded,
                            message.subject
                        )
                        OpenMode.REPLY -> context.getString(
                            R.string.new_message_reply,
                            message.subject
                        )
                        else -> message.subject
                    }
                } else {
                    ""
                }
            )
        }
        var body by rememberSaveable {
            mutableStateOf(
                if (message?.body != null) {
                    when (openMode) {
                        OpenMode.FORWARD, OpenMode.REPLY -> context.getString(
                            R.string.new_message_body_source,
                            message.body,
                            message.sender
                        )
                        else -> message.body
                    }
                } else {
                    ""
                }
            )
        }
        var attachments by rememberSaveable {
            mutableStateOf(mapOf<String, String>()) // uri to name
        }

        val attachContract =
            rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                it?.let { uri ->
                    val cursor =
                        context.contentResolver.query(uri, null, null, null, null) ?: return@let
                    val size = cursor
                        .apply { moveToFirst() }
                        .getInt(cursor.getColumnIndex(OpenableColumns.SIZE))
                        .div(1024)

                    if (size > viewModel.messageFileSizeLimit) {
                        toast(
                            context.getString(
                                R.string.new_message_file_size_limit,
                                viewModel.messageFileSizeLimit / 1024
                            )
                        )
                        cursor.close()
                        return@let
                    }

                    val fileName = when (uri.scheme) {
                        "file" -> uri.lastPathSegment!!
                        "content" ->
                            cursor
                                .apply { moveToFirst() }
                                .getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        else -> "Unknown attachment"
                    }
                    attachments = attachments + (uri.toString() to fileName)

                    cursor.close()
                }
            }

        Scaffold(
            topBar = {
                Toolbar(
                    showDivider = showDivider,
                    sendEnabled = subject.isNotEmpty() &&
                        (selectedReceiver ?: receiver) != null &&
                        (body.isNotEmpty() || attachments.isNotEmpty()),
                    sendLoading = isSendLoading,
                    onAttach = {
                        attachContract.launch(arrayOf("*/*"))
                    },
                    onSend = {
                        softwareKeyboardController?.hide()
                        isSendLoading = true

                        viewModel.send(
                            subject = subject,
                            receiver = (selectedReceiver ?: receiver)!!,
                            body = body,
                            attachments = attachments,
                            context = context
                        ) {
                            navigator.pop()
                            selectedReceiver = null
                        }
                    },
                    onBack = {
                        selectedReceiver = null
                        navigator.pop()
                    }
                )
            },
            content = {
                LazyColumn(
                    modifier = Modifier
                        .alpha(animateFloatAsState(if (isSendLoading) 0.8f else 1f).value)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { ReceiverSelection(receiver) }
                    item {
                        SubjectInput(
                            subject = subject,
                            onSubjectChange = { subject = it }
                        )
                    }
                    item {
                        Body(
                            value = body,
                            onValueChanged = { body = it }
                        )
                    }
                    item {
                        Attachments(
                            list = attachments,
                            onDelete = { uri ->
                                attachments = attachments.filterKeys { it != uri }
                            }
                        )
                    }
                }
            },
            modifier = Modifier
                .then(
                    if (isSendLoading) {
                        Modifier
                            // disable modifying the message
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {}
                    } else {
                        Modifier
                    }
                )
        )
        LaunchedEffect(Unit) {
            if (openMode == OpenMode.FORWARD) bottomSheetNavigator.show(MessageReceiversBottomSheet())
        }
        BackHandler {
            selectedReceiver = null
            navigator.pop()
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun Toolbar(
        showDivider: Boolean,
        sendEnabled: Boolean,
        sendLoading: Boolean,
        onAttach: () -> Unit,
        onSend: () -> Unit,
        onBack: () -> Unit
    ) {
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
                        onClick = onBack
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.new_message_title),
                        style = Typography.h4.copy(color = LocalNetSchoolColors.current.textMain),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    TopAppBarIcon(
                        iconPainter = painterResource(R.drawable.ic_attach),
                        tint = LocalNetSchoolColors.current.accentMain,
                        onClick = onAttach
                    )
                    AnimatedContent(targetState = sendLoading) {
                        if (it) {
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = LocalNetSchoolColors.current.accentInactive,
                                    strokeWidth = 2.dp
                                )
                            }
                        } else {
                            TopAppBarIcon(
                                iconPainter = painterResource(R.drawable.ic_send),
                                tint = animateColorAsState(
                                    if (sendEnabled) {
                                        LocalNetSchoolColors.current.accentMain
                                    } else {
                                        LocalNetSchoolColors.current.accentInactive
                                    }
                                ).value,
                                onClick = onSend,
                                enabled = sendEnabled
                            )
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

    @Composable
    private fun ReceiverSelection(receiver: MailMessageReceiver?) {
        val navigator = LocalBottomSheetNavigator.current

        TitledRowContent(
            title = stringResource(R.string.new_message_receiver),
            modifier = Modifier.clickable { navigator.show(MessageReceiversBottomSheet()) }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_person),
                contentDescription = stringResource(R.string.new_message_receiver),
                tint = LocalNetSchoolColors.current.accentMain
            )
            Text(
                text = receiver?.name ?: stringResource(R.string.new_message_receiver_not_set),
                style = Typography.body1.copy(
                    color = if (receiver == null) {
                        LocalNetSchoolColors.current.textSecondary
                    } else {
                        LocalNetSchoolColors.current.textMain
                    }
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_expand_collapse),
                contentDescription = stringResource(R.string.new_message_receiver),
                tint = LocalNetSchoolColors.current.accentMain
            )
        }
    }

    @Composable
    private fun SubjectInput(
        subject: String,
        onSubjectChange: (String) -> Unit
    ) {
        TitledRowContent(title = stringResource(R.string.new_message_subject)) {
            Icon(
                painter = painterResource(R.drawable.ic_subject),
                contentDescription = stringResource(R.string.new_message_subject),
                tint = LocalNetSchoolColors.current.accentMain
            )
            BasicTextField(
                value = subject,
                onValueChange = onSubjectChange,
                maxLines = 3,
                textStyle = Typography.body1.copy(color = LocalNetSchoolColors.current.textMain),
                cursorBrush = SolidColor(LocalNetSchoolColors.current.accentMain),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.animateContentSize()
            ) { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (subject.isEmpty()) {
                        Text(
                            text = stringResource(R.string.new_message_subject_not_set),
                            style = Typography.body1.copy(color = LocalNetSchoolColors.current.textSecondary)
                        )
                    }
                    innerTextField()
                }
            }
        }
    }

    @Composable
    private fun TitledRowContent(
        title: String,
        modifier: Modifier = Modifier,
        content: @Composable RowScope.() -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = Typography.subtitle1.copy(color = LocalNetSchoolColors.current.textMain),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
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
                    .then(modifier)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                content = content
            )
        }
    }

    @Composable
    private fun Body(
        value: String,
        onValueChanged: (String) -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.new_message_body),
                style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            BodyTextField(value = value, onValueChanged = onValueChanged)
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun Attachments(
        list: Map<String, String>, // uri to name
        onDelete: (String) -> Unit
    ) {
        LaunchedEffect(list) { list.debugValue() }
        val context = LocalContext.current

        AnimatedVisibility(
            visible = list.isNotEmpty(),
            enter = scaleIn(initialScale = 0.9f) + fadeIn(),
            exit = scaleOut(targetScale = 0.9f) + fadeOut()
        ) root@{
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.message_attachments),
                    style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                list.forEach { (uri, name) ->
                    val isVisible = remember { MutableTransitionState(true) }

                    AnimatedVisibility(
                        visibleState = isVisible,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .fillMaxWidth()
                                .height(64.dp)
                                .background(
                                    color = LocalNetSchoolColors.current.backgroundCard,
                                    shape = Shapes.medium
                                )
                                .border(
                                    width = 1.dp,
                                    color = LocalNetSchoolColors.current.divider,
                                    shape = Shapes.medium
                                )
                                .clip(Shapes.medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val thumb = remember(uri) {
                                if (uri.toUri().checkIfImage(context)) {
                                    uri.getThumbnail(context)
                                } else {
                                    null
                                }
                            }

                            if (thumb != null) {
                                Image(
                                    bitmap = thumb.asImageBitmap(),
                                    contentDescription = name,
                                    modifier = Modifier.size(64.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            LocalNetSchoolColors.current.accentMain.copy(alpha = 0.1f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_file_no_preview),
                                        contentDescription = name,
                                        tint = LocalNetSchoolColors.current.accentMain
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .widthIn(1.dp)
                                    .background(LocalNetSchoolColors.current.divider)
                            )
                            Text(
                                text = name,
                                style = Typography.button.copy(color = LocalNetSchoolColors.current.accentMain),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            )
                            TopAppBarIcon(
                                iconPainter = painterResource(R.drawable.ic_trash_bin),
                                tint = LocalNetSchoolColors.current.gradeBad,
                                onClick = { isVisible.targetState = false }
                            )
                        }
                    }
                    if (isVisible.isIdle && !isVisible.currentState) {
                        onDelete(uri.debugValue("delete"))
                    }
                }
            }
        }
    }

    @Composable
    private fun BodyTextField(
        value: String,
        onValueChanged: (String) -> Unit
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChanged,
            cursorBrush = SolidColor(LocalNetSchoolColors.current.accentMain),
            textStyle = Typography.body1.copy(color = LocalNetSchoolColors.current.textMain)
        ) { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                innerTextField()
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.new_message_body_placeholder),
                        style = Typography.body1.copy(color = LocalNetSchoolColors.current.textSecondary)
                    )
                }
            }
        }
    }

    private fun Uri.checkIfImage(context: Context): Boolean {
        return if (ContentResolver.SCHEME_CONTENT == scheme) {
            context.contentResolver.getType(this)
        } else {
            MimeTypeMap
                .getSingleton()
                .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(toString()))
        }?.startsWith("image") == true
    }

    private fun String.getThumbnail(context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver.loadThumbnail(toUri(), Size(256, 256), CancellationSignal())
        } else {
	        @Suppress("deprecation")
            MediaStore.Images.Thumbnails.getThumbnail(
                context.contentResolver,
                toUri().getId(),
                MediaStore.Images.Thumbnails.MINI_KIND,
                null
            )
        }
    }

    private fun Uri.getId() = lastPathSegment!!.split(':')[1].toLong()

    enum class OpenMode {
        FORWARD,
        REPLY,
        NEW
    }

    @IgnoredOnParcel
    override val key = uniqueScreenKey
}
