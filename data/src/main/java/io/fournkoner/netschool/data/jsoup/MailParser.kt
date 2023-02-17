package io.fournkoner.netschool.data.jsoup

import org.jsoup.Jsoup

internal object MailParser {

    fun getUnreadMessagesCount(html: String): Int {
        val site = Jsoup.parse(html)

        return site.select("span.icon-envelope.mail").first()
            .also { if (it?.children().isNullOrEmpty()) return 0 }!!
            .select("span.numberMail").text().toInt()
    }
}