package io.fournkoner.netschool.domain.repositories

import io.fournkoner.netschool.domain.entities.reports.*

interface ReportsRepository {

    suspend fun getShortReportRequestData(): Result<List<ReportRequestData>>

    suspend fun generateShortReport(params: List<ReportRequestData>): Result<ShortReport?>

    suspend fun getSubjectReportRequestData(): Result<SubjectReportRequestData>

    suspend fun generateSubjectReport(params: List<ReportRequestData>): Result<SubjectReport?>

    suspend fun generateFinalReport(): Result<List<FinalReportPeriod>>
}