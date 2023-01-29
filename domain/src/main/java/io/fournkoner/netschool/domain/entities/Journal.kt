package io.fournkoner.netschool.domain.entities

data class Journal(
    val days: List<Day>,
    val weekStart: String,
    val weekEnd: String
) {

    data class Day(
        val date: String,
        val classes: List<Class>,
    )

    data class Class(
        val position: Int,
        val name: String,
        val assignments: List<Assignment>,
        val grades: List<Int?>,
    ) {

        data class Assignment(
            val name: String,
            val attachments: List<Attachment>,
            val grade: Int?
        ) {

            data class Attachment(
                val name: String,
                val file: String
            )
        }
    }
}
