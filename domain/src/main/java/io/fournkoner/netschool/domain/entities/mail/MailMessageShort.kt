package io.fournkoner.netschool.domain.entities.mail

data class MailMessageShort(
    val id: Int,
    val sender: String,
    val subject: String?,
    val date: Long,
    val unread: Boolean
)
