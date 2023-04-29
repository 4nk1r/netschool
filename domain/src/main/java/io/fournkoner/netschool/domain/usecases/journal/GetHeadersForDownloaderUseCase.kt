package io.fournkoner.netschool.domain.usecases.journal

import io.fournkoner.netschool.domain.repositories.JournalRepository

class GetHeadersForDownloaderUseCase(private val repository: JournalRepository) {

    operator fun invoke() = repository.getHeadersForDownloader()
}
