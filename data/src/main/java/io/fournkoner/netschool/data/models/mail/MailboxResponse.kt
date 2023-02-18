package io.fournkoner.netschool.data.models.mail

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class MailboxResponse(
    @SerializedName("Records") val messages: List<Message>
) {

    @Keep
    data class Message(
        @SerializedName("MessageId") val id: Int,
        @SerializedName("FromName") val sender: String,
        @SerializedName("Subj") val subject: String,
        @SerializedName("Read") val isRead: String,
        @SerializedName("Sent") val date: String
    )
}
