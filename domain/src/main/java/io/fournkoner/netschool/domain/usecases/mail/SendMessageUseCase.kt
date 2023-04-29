package io.fournkoner.netschool.domain.usecases.mail

import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiver
import io.fournkoner.netschool.domain.repositories.MailRepository
import java.io.File

class SendMessageUseCase(private val repository: MailRepository) {

    suspend operator fun invoke(
        receiver: MailMessageReceiver,
        subject: String,
        body: String,
        attachments: Map<File, String>
    ) = repository.sendMessageUseCase(receiver, subject, body, attachments)
}
