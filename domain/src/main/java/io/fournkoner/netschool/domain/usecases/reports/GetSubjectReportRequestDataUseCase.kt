package io.fournkoner.netschool.domain.usecases.reports

import io.fournkoner.netschool.domain.repositories.ReportsRepository

class GetSubjectReportRequestDataUseCase(private val repository: ReportsRepository) {

    suspend operator fun invoke() = repository.getSubjectReportRequestData()
}
