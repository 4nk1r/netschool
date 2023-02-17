package io.fournkoner.netschool.domain.repositories

interface MailRepository {

    suspend fun getUnreadMessagesCount(): Result<Int>
}