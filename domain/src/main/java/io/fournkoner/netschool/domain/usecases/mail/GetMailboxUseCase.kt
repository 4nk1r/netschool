package io.fournkoner.netschool.domain.usecases.mail

import io.fournkoner.netschool.domain.entities.mail.Mailbox
import io.fournkoner.netschool.domain.repositories.MailRepository

class GetMailboxUseCase(private val repository: MailRepository) {

    suspend operator fun invoke(mailbox: Mailbox, page: Int) = repository.getMailbox(mailbox, page)
}
