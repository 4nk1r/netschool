package io.fournkoner.netschool.domain.repositories

import io.fournkoner.netschool.domain.entities.reports.ReportRequestData
import io.fournkoner.netschool.domain.entities.reports.ShortReport
import io.fournkoner.netschool.domain.entities.reports.SubjectReport
import io.fournkoner.netschool.domain.entities.reports.SubjectReportRequestData

interface ReportsRepository {

    suspend fun getShortReportRequestData(): Result<List<ReportRequestData>>

    suspend fun generateShortReport(params: List<ReportRequestData>): Result<ShortReport?>

    suspend fun getSubjectReportRequestData(): Result<SubjectReportRequestData>

    suspend fun generateSubjectReport(params: List<ReportRequestData>): Result<SubjectReport?>
}