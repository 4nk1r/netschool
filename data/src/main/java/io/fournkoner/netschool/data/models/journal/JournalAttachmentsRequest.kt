package io.fournkoner.netschool.data.models.journal

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class JournalAttachmentsRequest(
    @SerializedName("assignId") val assignmentIds: List<Int>
)
