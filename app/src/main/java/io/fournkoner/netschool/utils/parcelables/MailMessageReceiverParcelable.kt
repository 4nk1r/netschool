package io.fournkoner.netschool.utils.parcelables

import android.os.Parcelable
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiver
import kotlinx.parcelize.Parcelize

@Parcelize
data class MailMessageReceiverParcelable(
    val id: Int,
    val name: String
): Parcelable {

    fun toDomainObject() = MailMessageReceiver(
        id = id,
        name = name
    )
}

fun MailMessageReceiver.toParcelable() = MailMessageReceiverParcelable(
    id = id,
    name = name
)
