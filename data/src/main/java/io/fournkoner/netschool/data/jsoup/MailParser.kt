package io.fournkoner.netschool.data.jsoup

import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.domain.entities.mail.MailMessageDetailed
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiver
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

internal object MailParser {

    fun getUnreadMessagesCount(html: String): Int {
        val site = Jsoup.parse(html)

        return site.select("span.icon-envelope.mail").first()
            .also { if (it?.children().isNullOrEmpty()) return 0 }!!
            .select("span.numberMail").text().toInt()
    }

    fun parseMailMessageDetailed(html: String): MailMessageDetailed {
        val site = Jsoup.parse(html.debugValue())

        var sender: String? = null
        var receivers: String? = null
        var date: Long? = null

        site.select("div#message_headers").select("div.form-group").forEach { div ->
            val content = div.select("input.form-control")[0].attr("value")

            when (div.select("label.control-label")[0].text()) {
                "От кого" -> sender = content
                "Кому" -> receivers = content
                "Отправлено" -> {
                    date = SimpleDateFormat("dd.MM.yy hh:mm", Locale.getDefault())
                        .parse(content)?.time ?: 0
                }
            }
        }

        var subject: String? = null
        var body: String? = null
        val attachments = mutableListOf<MailMessageDetailed.Attachment>()

        site.select("div#message_body").select("div.form-group").forEach { div ->
            when (div.select("label.control-label")[0].text()) {
                "Тема" -> {
                    subject = div.select("input.form-control")[0].attr("value")
                }
                "Текст" -> {
                    body = div.children()[1].text()
                }
                "Прикреплённые файлы" -> {
                    attachments.addAll(
                        div.select("div.file-attachment").debugValue().map { attachment ->
                            val onClick = attachment.attr("onclick")

                            val name = attachment.text()
                            val id = onClick
                                .substringAfter(',')
                                .substringBefore(')')
                                .trim()

                            MailMessageDetailed.Attachment(
                                name = name,
                                file = "${Const.HOST}webapi/attachments/$id"
                            )
                        })
                }
            }
        }

        return MailMessageDetailed(
            subject = subject ?: throw RuntimeException("Parsing failed: subject is null"),
            body = body ?: throw RuntimeException("Parsing failed: body is null"),
            sender = sender ?: throw RuntimeException("Parsing failed: sender is null"),
            receivers = receivers ?: throw RuntimeException("Parsing failed: receivers is null"),
            date = date ?: throw RuntimeException("Parsing failed: date is null"),
            attachments = attachments,
        )
    }

    fun parseMessageReceivers(html: String): List<MailMessageReceiver> {
        val site = Jsoup.parse(html)

        return site.select("div.row").select("a").map { receiver ->
            val id = receiver.attr("onclick")
                .substringAfter('\'')
                .substringBefore('\'')
                .toInt()
            val name = receiver.text()

            MailMessageReceiver(
                id = id,
                name = name
            )
        }
    }
}