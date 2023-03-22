package io.fournkoner.netschool.domain.entities.reports

data class FinalReportPeriod(
    val name: String,
    val subjects: List<Subject>
) {

    data class Subject(
        val name: String,
        val grade: String? // not int because of possible values like "зач"
    )
}
