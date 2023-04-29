package io.fournkoner.netschool.domain.usecases.mail

import io.fournkoner.netschool.domain.repositories.MailRepository

class GetMessageFileSizeLimitUseCase(private val repository: MailRepository) {

    operator fun invoke() = repository.getMessageFileSizeLimit()
}
