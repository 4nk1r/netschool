package io.fournkoner.netschool.domain.entities.reports

data class ShortReport(
    val total: Grades,
    val subjects: List<Subject>
) {

    data class Subject(
        val name: String,
        val grades: Grades
    )

    data class Grades(
        val greatCount: Int,
        val goodCount: Int,
        val satisfactoryCount: Int,
        val badCount: Int,
        val average: Float
    )
}
