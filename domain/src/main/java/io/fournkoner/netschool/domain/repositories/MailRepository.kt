package io.fournkoner.netschool.domain.repositories

import io.fournkoner.netschool.domain.entities.mail.*
import java.io.File

interface MailRepository {

    suspend fun getUnreadMessagesCount(): Result<Int>

    suspend fun getMailbox(mailbox: Mailbox, page: Int): Result<List<MailMessageShort>>

    suspend fun getMailMessageDetailed(id: Int): Result<MailMessageDetailed>

    suspend fun deleteMessages(ids: List<Int>, mailbox: Mailbox)

    suspend fun getMessageReceivers(group: MailMessageReceiverGroup): Result<List<MailMessageReceiver>>

    fun getMessageFileSizeLimit(): Int

    suspend fun sendMessageUseCase(
        receiver: MailMessageReceiver,
        subject: String,
        body: String,
        attachments: Map<File, String>,
    ): Result<Boolean>
}