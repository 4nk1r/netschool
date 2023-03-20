package io.fournkoner.netschool.domain.entities.reports

data class SubjectReportRequestData(
    val period: Period,
    val pairs: List<ReportRequestData>
) {

    data class Period(
        val defaultStart: Long,
        val defaultEnd: Long,
        val availableRange: LongRange,
    )
}
