package io.fournkoner.netschool.data.repositories

import io.fournkoner.netschool.data.jsoup.MailParser
import io.fournkoner.netschool.data.network.MailService
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.data.utils.id
import io.fournkoner.netschool.data.utils.toFormUrlEncodedString
import io.fournkoner.netschool.domain.entities.mail.MailMessageShort
import io.fournkoner.netschool.domain.entities.mail.Mailbox
import io.fournkoner.netschool.domain.repositories.MailRepository
import java.text.SimpleDateFormat
import java.util.*

internal class MailRepositoryImpl(
    private val mailService: MailService,
) : MailRepository {

    override suspend fun getUnreadMessagesCount(): Result<Int> = runCatching {
        val html = mailService.getUnreadMessagesCount(
            body = mapOf(
                "LoginType" to "0",
                "AT" to Const.at!!,
                "VER" to Const.ver!!
            ).toFormUrlEncodedString()
        ).debugValue()
        MailParser.getUnreadMessagesCount(html)
    }

    override suspend fun getMailbox(mailbox: Mailbox, page: Int): Result<List<MailMessageShort>> =
        runCatching {
            val response = mailService.getMailbox(
                mailboxId = mailbox.id,
                startIndex = (page - 1) * Const.MAILBOX_PAGE_SIZE,
                pageSize = Const.MAILBOX_PAGE_SIZE
            ).debugValue()
            response.messages.map { message ->
                MailMessageShort(
                    id = message.id,
                    subject = message.subject.takeIf { it.isNotBlank() },
                    sender = message.sender,
                    unread = message.isRead == "N",
                    date = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault())
                        .parse(message.date)?.time ?: 0
                )
            }
        }
}