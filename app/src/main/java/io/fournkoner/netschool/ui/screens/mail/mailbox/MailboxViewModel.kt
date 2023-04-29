package io.fournkoner.netschool.ui.screens.mail.mailbox

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

    val inboxMessages = Pager(PagingConfig(pageSize = 15)) {
        mailboxPagingSourceFactory.create(Mailbox.INBOX)
    }.flow.cachedIn(viewModelScope)

    val sentMessages = Pager(PagingConfig(pageSize = 15)) {
        mailboxPagingSourceFactory.create(Mailbox.SENT)
    }.flow.cachedIn(viewModelScope)
}
