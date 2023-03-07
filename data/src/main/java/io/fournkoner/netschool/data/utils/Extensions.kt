package io.fournkoner.netschool.data.utils

import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiverGroup
import io.fournkoner.netschool.domain.entities.mail.Mailbox

internal fun <T> List<T>.bringToFirst(selector: (T) -> Boolean): List<T> {
    val item = firstOrNull(selector) ?: return this
    val copy = toMutableList().apply {
        remove(item)
        add(0, item)
    }
    return copy
}

internal val Mailbox.id
    get() = when (this) {
        Mailbox.INBOX -> 1
        Mailbox.SENT -> 3
    }

internal val MailMessageReceiverGroup.id
    get() = when(this) {
        MailMessageReceiverGroup.TEACHERS -> "T"
        MailMessageReceiverGroup.STUDENTS -> "D"
    }