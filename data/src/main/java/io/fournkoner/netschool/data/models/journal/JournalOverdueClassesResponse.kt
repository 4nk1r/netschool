package io.fournkoner.netschool.data.models.journal

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class JournalOverdueClassesResponse(
    @SerializedName("subjectName")
    val subject: String,
    @SerializedName("assignmentName")
    val name: String,
    @SerializedName("dueDate")
    val due: String
)
