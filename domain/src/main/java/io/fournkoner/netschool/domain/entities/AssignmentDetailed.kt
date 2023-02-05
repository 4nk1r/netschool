package io.fournkoner.netschool.domain.entities

data class AssignmentDetailed(
    val name: String,
    val description: String?,
    val attachments: List<Attachment>,
    val teacher: String,
    val subject: String,
    val type: Journal.Class.Assignment.Type,
    val grade: Int?
) {
    data class Attachment(
        val name: String,
        val file: String
    )
}