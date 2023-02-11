package io.fournkoner.netschool.domain.repositories

import io.fournkoner.netschool.domain.entities.reports.ReportRequestData
import io.fournkoner.netschool.domain.entities.reports.ShortReport

interface ReportsRepository {

    suspend fun getShortReportRequestData(): Result<List<ReportRequestData>>

    suspend fun generateShortReport(params: List<ReportRequestData>): Result<ShortReport>
}