package io.fournkoner.netschool.ui.screens.mail.message

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.mail.MailMessageDetailed
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiverGroup
import io.fournkoner.netschool.domain.entities.mail.Mailbox
import io.fournkoner.netschool.domain.usecases.journal.GetHeadersForDownloaderUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetMailMessageDetailedUseCase
import io.fournkoner.netschool.domain.usecases.mail.DeleteMessagesUseCase
import io.fournkoner.netschool.domain.usecases.mail.GetMessageReceiversUseCase
import io.fournkoner.netschool.utils.debugValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

class MailMessageViewModel @AssistedInject constructor(
    @Assisted private val id: Int,
    @Assisted private val mailbox: Mailbox,
    private val getMailMessageDetailedUseCase: GetMailMessageDetailedUseCase,
    private val getHeadersForDownloaderUseCase: GetHeadersForDownloaderUseCase,
    private val deleteMessagesUseCase: DeleteMessagesUseCase,
    private val getMessageReceiversUseCase: GetMessageReceiversUseCase
) : ScreenModel {

    private val _message = MutableStateFlow<MailMessageDetailed?>(null)
    val message: StateFlow<MailMessageDetailed?> get() = _message

    private val _senderId = MutableStateFlow<Int?>(null)
    val senderId: StateFlow<Int?> get() = _senderId

    init {
        coroutineScope.launch {
            _message.value = getMailMessageDetailedUseCase(id).getOrElse {
                it.printStackTrace()
                null
            }
            getMessageReceiversUseCase(MailMessageReceiverGroup.TEACHERS).onSuccess { teachers ->
                var id = teachers.find { _message.value?.sender?.contains(it.name) == true }
                if (id != null) {
                    _senderId.value = id.id
                } else {
                    getMessageReceiversUseCase(MailMessageReceiverGroup.STUDENTS).onSuccess { students ->
                        id = students.find { _message.value?.sender?.contains(it.name) == true }
                        if (id != null) {
                            _senderId.value = id!!.id
                        }
                    }.onFailure {
                        @OptIn(UnreliableToastApi::class)
                        toast(R.string.error_occurred)
                    }
                }
            }.onFailure {
                @OptIn(UnreliableToastApi::class)
                toast(R.string.error_occurred)
            }
        }
    }

    fun downloadFile(file: MailMessageDetailed.Attachment, context: Context) {
        val headers = getHeadersForDownloaderUseCase().debugValue("Headers")
        io.fournkoner.netschool.utils.downloadFile(
            name = file.name,
            link = file.file,
            downloadHeaders = headers,
            context = context
        )
    }

    fun deleteMessage() {
        coroutineScope.launch {
            deleteMessagesUseCase(listOf(id), mailbox)
        }
    }

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(id: Int, mailbox: Mailbox): MailMessageViewModel
    }
}
