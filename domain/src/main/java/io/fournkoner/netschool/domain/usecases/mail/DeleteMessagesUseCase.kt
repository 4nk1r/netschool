package io.fournkoner.netschool.domain.usecases.mail

import io.fournkoner.netschool.domain.entities.mail.Mailbox
import io.fournkoner.netschool.domain.repositories.MailRepository

class DeleteMessagesUseCase(private val repository: MailRepository) {

    suspend operator fun invoke(ids: List<Int>, mailbox: Mailbox) =
        repository.deleteMessages(ids, mailbox)
}