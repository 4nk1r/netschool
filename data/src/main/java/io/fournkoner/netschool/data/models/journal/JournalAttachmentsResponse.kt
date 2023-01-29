package io.fournkoner.netschool.data.models.journal

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class JournalAttachmentsResponse(
    @SerializedName("assignmentId") val assignmentId: Int,
    @SerializedName("attachments") val attachments: List<Attachment>
) {

    @Keep
    data class Attachment(
        @SerializedName("id") val id: Int,
        @SerializedName("originalFileName") val fileName: String
    )
}
