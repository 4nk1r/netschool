package io.fournkoner.netschool.domain.usecases.journal

import io.fournkoner.netschool.domain.entities.journal.Journal
import io.fournkoner.netschool.domain.repositories.JournalRepository

class GetDetailedAssignmentsUseCase(private val repository: JournalRepository) {

    suspend operator fun invoke(assignments: List<Journal.Class.Assignment>) =
        repository.getDetailedAssignments(assignments)
}
