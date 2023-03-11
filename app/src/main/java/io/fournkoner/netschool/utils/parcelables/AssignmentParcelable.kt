package io.fournkoner.netschool.utils.parcelables

import android.os.Parcelable
import io.fournkoner.netschool.domain.entities.journal.Journal
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssignmentParcelable(
    val id: Int,
    val name: String,
    val attachments: List<Attachment>,
    val grade: Int?,
    val type: String
) : Parcelable {

    @Parcelize
    data class Attachment(
        val name: String,
        val file: String,
    ) : Parcelable

    fun toDomain() = Journal.Class.Assignment(
        id = id,
        name = name,
        grade = grade,
        attachments = attachments.map {
            Journal.Class.Assignment.Attachment(
                name = it.name,
                file = it.file
            )
        },
        type = type
    )
}

fun Journal.Class.Assignment.toParcelable() = AssignmentParcelable(
    id = id,
    name = name,
    grade = grade,
    attachments = attachments.map {
        AssignmentParcelable.Attachment(
            name = it.name,
            file = it.file
        )
    },
    type = type
)