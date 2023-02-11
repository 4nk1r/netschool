package io.fournkoner.netschool.domain.entities.journal

data class Journal(
    val days: List<Day>,
    val weekStart: String,
    val weekEnd: String,
    val overdueClasses: List<OverdueClass>
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
            val id: Int,
            val name: String,
            val attachments: List<Attachment>,
            val grade: Int?,
            val type: Type
        ) {

            enum class Type {
                Homework,
                IndependentWork,
                Answer,
                PracticalWork,
                Unknown
            }

            data class Attachment(
                val name: String,
                val file: String
            )
        }
    }

    data class OverdueClass(
        val subject: String,
        val name: String,
        val due: String
    )
}
