package io.fournkoner.netschool.domain.repositories

import io.fournkoner.netschool.domain.entities.mail.MailMessageDetailed
import io.fournkoner.netschool.domain.entities.mail.MailMessageShort
import io.fournkoner.netschool.domain.entities.mail.Mailbox

interface MailRepository {

    suspend fun getUnreadMessagesCount(): Result<Int>

    suspend fun getMailbox(mailbox: Mailbox, page: Int): Result<List<MailMessageShort>>

    suspend fun getMailMessageDetailed(id: Int): Result<MailMessageDetailed>

    suspend fun deleteMessages(ids: List<Int>, mailbox: Mailbox)
}