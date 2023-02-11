package io.fournkoner.netschool.data.jsoup

import io.fournkoner.netschool.domain.entities.reports.ShortReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

internal object ReportsParser {

    suspend fun parseShortReport(html: String): ShortReport {
        return withContext(Dispatchers.IO) {
            val site = Jsoup.parse(html)
            val table = site.select("table.table-print")

            var totals: ShortReport.Grades? = null
            val subjects = mutableListOf<ShortReport.Subject>()

            for (tr in table.select("tr")) {
                val items = tr.select("td")

                if (tr.hasClass("totals")) {
                    totals = ShortReport.Grades(
                        greatCount = items[1].text().toIntOrNull() ?: 0,
                        goodCount = items[2].text().toIntOrNull() ?: 0,
                        satisfactoryCount = items[3].text().toIntOrNull() ?: 0,
                        badCount = items[4].text().toIntOrNull() ?: 0,
                        average = items[5].text().replace(',', '.').toFloatOrNull() ?: 0f
                    )
                    continue
                }
                if (tr.children().size != 6) continue

                subjects += ShortReport.Subject(
                    name = items[0].text().trim(),
                    grades = ShortReport.Grades(
                        greatCount = items[1].text().toIntOrNull() ?: 0,
                        goodCount = items[2].text().toIntOrNull() ?: 0,
                        satisfactoryCount = items[3].text().toIntOrNull() ?: 0,
                        badCount = items[4].text().toIntOrNull() ?: 0,
                        average = items[5].text().replace(',', '.').toFloatOrNull() ?: 0f,
                    )
                )
            }

            ShortReport(total = totals!!, subjects = subjects)
        }
    }
}