package io.fournkoner.netschool.ui.screens.mail_message

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.fournkoner.netschool.domain.entities.mail.MailMessageDetailed
import io.fournkoner.netschool.domain.entities.mail.Mailbox
import io.fournkoner.netschool.domain.usecases.journal.GetHeadersForDownloaderUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetMailMessageDetailedUseCase
import io.fournkoner.netschool.domain.usecases.mail.DeleteMessagesUseCase
import io.fournkoner.netschool.utils.debugValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MailMessageViewModel @AssistedInject constructor(
    @Assisted private val id: Int,
    @Assisted private val mailbox: Mailbox,
    private val getMailMessageDetailedUseCase: GetMailMessageDetailedUseCase,
    private val getHeadersForDownloaderUseCase: GetHeadersForDownloaderUseCase,
    private val deleteMessagesUseCase: DeleteMessagesUseCase
) : ScreenModel {

    private val _message = MutableStateFlow<MailMessageDetailed?>(null)
    val message: StateFlow<MailMessageDetailed?> get() = _message

    init {
        coroutineScope.launch {
            _message.value = getMailMessageDetailedUseCase(id).getOrElse {
                it.printStackTrace()
                null
            }.debugValue()
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