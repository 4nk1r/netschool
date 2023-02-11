package io.fournkoner.netschool.ui.navigation

import android.os.Parcelable
import io.fournkoner.netschool.domain.entities.journal.Journal
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssignmentParcelable(
    val id: Int,
    val name: String,
    val attachments: List<Attachment>,
    val grade: Int?,
    val type: Type
) : Parcelable {

    @Parcelize
    enum class Type: Parcelable {
        Homework,
        IndependentWork,
        Answer,
        PracticalWork,
        Unknown
    }

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
        type = when(type) {
            Type.Homework -> Journal.Class.Assignment.Type.Homework
            Type.IndependentWork -> Journal.Class.Assignment.Type.IndependentWork
            Type.Answer -> Journal.Class.Assignment.Type.Answer
            Type.PracticalWork -> Journal.Class.Assignment.Type.PracticalWork
            Type.Unknown -> Journal.Class.Assignment.Type.Unknown
        }
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
    type = when(type) {
        Journal.Class.Assignment.Type.Homework -> AssignmentParcelable.Type.Homework
        Journal.Class.Assignment.Type.IndependentWork -> AssignmentParcelable.Type.IndependentWork
        Journal.Class.Assignment.Type.Answer -> AssignmentParcelable.Type.Answer
        Journal.Class.Assignment.Type.PracticalWork -> AssignmentParcelable.Type.PracticalWork
        Journal.Class.Assignment.Type.Unknown -> AssignmentParcelable.Type.Unknown
    }
)