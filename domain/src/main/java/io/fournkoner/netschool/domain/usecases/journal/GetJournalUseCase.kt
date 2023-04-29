package io.fournkoner.netschool.domain.usecases.journal

import io.fournkoner.netschool.domain.repositories.JournalRepository

class GetJournalUseCase(private val repository: JournalRepository) {

    suspend operator fun invoke(weekStart: String, weekEnd: String) =
        repository.getJournal(weekStart, weekEnd)
}
