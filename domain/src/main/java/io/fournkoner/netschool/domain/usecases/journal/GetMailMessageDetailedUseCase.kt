package io.fournkoner.netschool.domain.usecases.journal

import io.fournkoner.netschool.domain.repositories.MailRepository

class GetMailMessageDetailedUseCase(private val repository: MailRepository) {

    suspend operator fun invoke(id: Int) = repository.getMailMessageDetailed(id)
}
