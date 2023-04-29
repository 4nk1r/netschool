package io.fournkoner.netschool.domain.entities.reports

data class SubjectReport(
    val total: Grades,
    val tasks: List<Task>
) {

    data class Task(
        val name: String,
        val type: String,
        val date: String,
        val grade: Int?
    )

    data class Grades(
        val greatCount: Int,
        val goodCount: Int,
        val satisfactoryCount: Int,
        val badCount: Int,
        val average: Float
    )
}
