package io.fournkoner.netschool.domain.usecases.reports

import io.fournkoner.netschool.domain.entities.reports.ReportRequestData
import io.fournkoner.netschool.domain.repositories.ReportsRepository

class GenerateSubjectReportUseCase(private val repository: ReportsRepository) {

    suspend operator fun invoke(params: List<ReportRequestData>) =
        repository.generateSubjectReport(params)
}