package io.fournkoner.netschool.data.jsoup

import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.domain.entities.reports.ShortReport
import io.fournkoner.netschool.domain.entities.reports.SubjectReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

internal object ReportsParser {

    suspend fun parseShortReport(html: String): ShortReport? {
        return withContext(Dispatchers.IO) {
            val site = Jsoup.parse(html)
            val table = site.select("table.table-print")
                .also { if (it.isEmpty()) return@withContext null }

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

    suspend fun parseSubjectReport(html: String): SubjectReport? {
        return withContext(Dispatchers.IO) {
            val site = Jsoup.parse(html.debugValue())
            val table = site.select("table.table-print")
                .also { if (it.isEmpty()) return@withContext null }

            val tasks = mutableListOf<SubjectReport.Task>()

            var averageGrade = 0f
            for (tr in table.select("tr")) {
                val columns = tr.getElementsByTag("td").debugValue()
                if (columns.size != 5) {
                    if (columns.size == 3) {
                        averageGrade = columns[2].text()
                            .substringAfter(':').trim()
                            .replace(',', '.').toFloat()
                    }
                    continue
                }

                tasks += SubjectReport.Task(
                    name = columns[1].text(),
                    type = columns[0].text(),
                    date = columns[2].text(),
                    grade = columns[4].text().toIntOrNull()
                )
            }

            val greatCount = tasks.count { it.grade == 5 }
            val goodCount = tasks.count { it.grade == 4 }
            val satisfactoryCount = tasks.count { it.grade == 3 }
            val badCount = tasks.count { it.grade == 2 }

            val total = SubjectReport.Grades(
                greatCount = greatCount,
                goodCount = goodCount,
                satisfactoryCount = satisfactoryCount,
                badCount = badCount,
                average = averageGrade
            )

            SubjectReport(total, tasks)
        }
    }
}