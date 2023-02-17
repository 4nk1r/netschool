package io.fournkoner.netschool.data.repositories

import io.fournkoner.netschool.data.jsoup.MailParser
import io.fournkoner.netschool.data.network.MailService
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.data.utils.toFormUrlEncodedString
import io.fournkoner.netschool.domain.repositories.MailRepository

internal class MailRepositoryImpl(
    private val mailService: MailService
) : MailRepository {

    override suspend fun getUnreadMessagesCount(): Result<Int> = runCatching {
        val html = mailService.getUnreadMessagesCount(
            body = mapOf(
                "LoginType" to "0",
                "AT" to Const.at!!,
                "VER" to Const.ver!!
            ).toFormUrlEncodedString()
        ).debugValue()
        MailParser.getUnreadMessagesCount(html)
    }
}