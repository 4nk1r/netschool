package io.fournkoner.netschool.utils.parcelables

import android.os.Parcelable
import io.fournkoner.netschool.domain.entities.mail.MailMessageDetailed
import kotlinx.parcelize.Parcelize

@Parcelize
data class MailMessageParcelable(
    val subject: String,
    val body: String,
    val sender: String,
    val receivers: String,
    val date: Long,
    val attachments: List<Attachment>,
) : Parcelable {

    @Parcelize
    data class Attachment(
        val name: String,
        val file: String,
    ) : Parcelable {

        fun toDomainObject() = MailMessageDetailed.Attachment(
            name = name,
            file = file
        )
    }

    fun toDomainObject() = MailMessageDetailed(
        subject = subject,
        body = body,
        sender = sender,
        receivers = receivers,
        date = date,
        attachments = attachments.map(Attachment::toDomainObject)
    )
}

fun MailMessageDetailed.toParcelable() = MailMessageParcelable(
    subject = subject,
    body = body,
    sender = sender,
    receivers = receivers,
    date = date,
    attachments = attachments.map { MailMessageParcelable.Attachment(it.name, it.file) }
)
