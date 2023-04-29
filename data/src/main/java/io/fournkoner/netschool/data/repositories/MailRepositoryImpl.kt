package io.fournkoner.netschool.data.repositories

import io.fournkoner.netschool.data.jsoup.MailParser
import io.fournkoner.netschool.data.network.MailService
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.data.utils.id
import io.fournkoner.netschool.data.utils.toFormUrlEncodedString
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiver
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiverGroup
import io.fournkoner.netschool.domain.entities.mail.MailMessageShort
import io.fournkoner.netschool.domain.entities.mail.Mailbox
import io.fournkoner.netschool.domain.repositories.MailRepository
import java.io.File
import java.net.URLConnection
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Scanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

internal class MailRepositoryImpl(
    private val mailService: MailService
) : MailRepository {

    override suspend fun getUnreadMessagesCount() = runCatching {
        val html = mailService.getUnreadMessagesCount(
            body = mapOf(
                "LoginType" to "0",
                "AT" to Const.at!!,
                "VER" to Const.ver!!
            ).toFormUrlEncodedString()
        )
        MailParser.getUnreadMessagesCount(html)
    }

    override suspend fun getMailbox(mailbox: Mailbox, page: Int) = runCatching {
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

    override suspend fun getMailMessageDetailed(id: Int) = runCatching {
        var html = ""
        val stream = mailService.getMailMessageDetailed(id)

        withContext(Dispatchers.IO) {
            val scanner = Scanner(stream.byteStream(), Charsets.UTF_8.name())
            while (scanner.hasNext()) html += scanner.nextLine()
        }

        MailParser.parseMailMessageDetailed(html)
    }

    override suspend fun deleteMessages(ids: List<Int>, mailbox: Mailbox) {
        runCatching {
            mailService.deleteMessages(
                body = mapOf(
                    "AT" to Const.at!!,
                    "nBoxId" to mailbox.id
                ).toList()
                    .joinToString(separator = "&") { "${it.first}=${it.second}" }
                    .plus("&")
                    .plus(ids.joinToString(separator = "&") { "deletedMessages=$it" })
            )
        }
    }

    override suspend fun getMessageReceivers(group: MailMessageReceiverGroup): Result<List<MailMessageReceiver>> {
        return runCatching {
            val html = mailService.getMessageReceivers(
                body = mapOf(
                    "LoginType" to "0",
                    "AT" to Const.at!!,
                    "VER" to Const.ver!!,
                    "A" to "",
                    "OrgType" to "0",
                    "FL" to group.id
                ).toFormUrlEncodedString()
            )
            MailParser.parseMessageReceivers(html)
        }
    }

    override fun getMessageFileSizeLimit(): Int {
        return Const.fileSizeLimit!!
    }

    override suspend fun sendMessageUseCase(
        receiver: MailMessageReceiver,
        subject: String,
        body: String,
        attachments: Map<File, String>
    ): Result<Boolean> = runCatching {
        val attachmentIds = attachments.debugValue().map {
            mailService.uploadFile(
                filePart = MultipartBody.Part.createFormData(
                    "fileAttachment",
                    it.value,
                    RequestBody.create(
                        MediaType.parse(URLConnection.guessContentTypeFromName(it.value)),
                        it.key
                    )
                )
            )
        }.debugValue()

        val requestBody = MailParser
            .parseSendMessageData(mailService.getSendMessageData())
            .mapValues {
                when (it.key) {
                    "LTO" -> receiver.id
                    "ATO" -> URLEncoder.encode(receiver.name, "UTF-8")
                    "SU" -> URLEncoder.encode(subject, "UTF-8")
                    "NEEDNOTIFY" -> 1
                    else -> it.value
                }
            }
            .toFormUrlEncodedString()
            .plus("&BO=${URLEncoder.encode(body, "UTF-8")}")
            .plus(
                if (attachmentIds.isNotEmpty()) {
                    "&" + attachmentIds.joinToString("&") { "attachment=$it" }
                } else {
                    ""
                }
            )
            .debugValue()

        mailService.sendMessage(requestBody)
        true
    }
}
