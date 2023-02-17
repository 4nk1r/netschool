package io.fournkoner.netschool.domain.usecases.mail

import io.fournkoner.netschool.domain.repositories.MailRepository

class GetUnreadMessagesCountUseCase(private val repository: MailRepository) {

    suspend operator fun invoke() = repository.getUnreadMessagesCount()
}