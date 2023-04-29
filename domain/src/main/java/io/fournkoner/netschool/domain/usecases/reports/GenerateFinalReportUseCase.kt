package io.fournkoner.netschool.domain.usecases.reports

import io.fournkoner.netschool.domain.repositories.ReportsRepository

class GenerateFinalReportUseCase(private val repository: ReportsRepository) {

    suspend operator fun invoke() = repository.generateFinalReport()
}
