package io.fournkoner.netschool.data.models.journal

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class JournalAssignmentDetailedResponse(
    @SerializedName("assignmentName")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("teachers")
    val teachers: List<Teacher>,
    @SerializedName("subjectGroup")
    val subject: Subject,
) {

    @Keep
    data class Attachment(
        @SerializedName("name")
        val name: String,
        @SerializedName("id")
        val id: Int,
    )

    @Keep
    data class Teacher(
        @SerializedName("name")
        val name: String,
    )

    @Keep
    data class Subject(
        @SerializedName("name")
        val name: String,
    )
}