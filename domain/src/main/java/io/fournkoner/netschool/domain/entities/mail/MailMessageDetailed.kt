package io.fournkoner.netschool.domain.entities.mail

data class MailMessageDetailed(
    val subject: String,
    val body: String,
    val sender: String,
    val receivers: String,
    val date: Long,
    val attachments: List<Attachment>,
) {

    data class Attachment(
        val name: String,
        val file: String,
    )
}
