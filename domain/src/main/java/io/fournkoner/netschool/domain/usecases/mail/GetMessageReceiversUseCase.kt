package io.fournkoner.netschool.domain.usecases.mail

import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiverGroup
import io.fournkoner.netschool.domain.repositories.MailRepository

class GetMessageReceiversUseCase(private val repository: MailRepository) {

    suspend operator fun invoke(group: MailMessageReceiverGroup) = repository.getMessageReceivers(group)
}