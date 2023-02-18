package io.fournkoner.netschool.ui.screens.mailbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.data.paging.MailboxPagingSource
import io.fournkoner.netschool.domain.entities.mail.Mailbox
import javax.inject.Inject

@HiltViewModel
class MailboxViewModel @Inject constructor(
    mailboxPagingSourceFactory: MailboxPagingSource.Factory
) : ViewModel() {

    private val inboxMailSource = mailboxPagingSourceFactory.create(Mailbox.INBOX)
    private val sentMailSource = mailboxPagingSourceFactory.create(Mailbox.SENT)

    val inboxMessages = Pager(PagingConfig(pageSize = 15)) {
        inboxMailSource
    }.flow.cachedIn(viewModelScope)

    val sentMessages = Pager(PagingConfig(pageSize = 15)) {
        sentMailSource
    }.flow.cachedIn(viewModelScope)
}