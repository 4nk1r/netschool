package io.fournkoner.netschool.data.repositories

import com.google.gson.Gson
import io.fournkoner.netschool.data.jsoup.ReportsParser
import io.fournkoner.netschool.data.models.reports.ReportInitQueueRequest
import io.fournkoner.netschool.data.network.ReportsService
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.domain.entities.reports.*
import io.fournkoner.netschool.domain.repositories.ReportsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

internal class ReportsRepositoryImpl(
    private val reportsService: ReportsService,
) : ReportsRepository {

    override suspend fun getShortReportRequestData(): Result<List<ReportRequestData>> {
        return runCatching {
            reportsService.getShortReportParams().filterSources
                .filter { it.defaultValue != null && it.items != null }
                .map { source ->
                    ReportRequestData(
                        id = source.id,
                        defaultValue = source.defaultValue,
                        values = source.items!!.map { item ->
                            ReportRequestData.Value(
                                name = item.name,
                                value = item.value
                            )
                        }
                    )
                }
        }
    }

    override suspend fun generateShortReport(params: List<ReportRequestData>): Result<ShortReport?> {
        return runCatching {
            val fileName = getReportFileName("parentinfoletter", params)
            val html = reportsService.getFile(fileName)
            ReportsParser.parseShortReport(html).debugValue()
        }
    }

    override suspend fun getSubjectReportRequestData(): Result<SubjectReportRequestData> {
        return runCatching {
            val response = reportsService.getSubjectReportParams()

            val pairs = response.filterSources
                .filter { it.defaultValue != null && it.items != null }
                .map { source ->
                    ReportRequestData(
                        id = source.id,
                        defaultValue = source.defaultValue,
                        values = source.items!!.map { item ->
                            ReportRequestData.Value(
                                name = item.name,
                                value = item.value
                            )
                        }
                    )
                }
                .filter { it.id != "period" }

            val period = response.filterSources
                .find { it.id == "period" }!!
                .let {
                    SubjectReportRequestData.Period(
                        defaultStart = it.defaultRange!!.start.let { strDate ->
                            LocalDate
                                .parse(strDate.substringBefore('T'))
                                .atStartOfDay(ZoneId.systemDefault())
                                .toEpochSecond() * 1000L //ms
                        },
                        defaultEnd = it.defaultRange.end.let { strDate ->
                            LocalDate
                                .parse(strDate.substringBefore('T'))
                                .atStartOfDay(ZoneId.systemDefault())
                                .toEpochSecond() * 1000L //ms
                        },
                        availableRange = it.range.let { strRange ->
                            LocalDate
                                .parse(strRange!!.start.substringBefore('T'))
                                .atStartOfDay(ZoneId.systemDefault())
                                .toEpochSecond() * 1000L /*ms*/..
                                    LocalDate
                                        .parse(strRange.end.substringBefore('T'))
                                        .atStartOfDay(ZoneId.systemDefault())
                                        .toEpochSecond() * 1000L //ms
                        }
                    )
                }

            SubjectReportRequestData(period = period, pairs = pairs)
        }
    }

    override suspend fun generateSubjectReport(params: List<ReportRequestData>): Result<SubjectReport?> {
        return runCatching {
            val fileName = getReportFileName("studentgrades", params)
            val html = reportsService.getFile(fileName)
            ReportsParser.parseSubjectReport(html).debugValue()
        }
    }

    override suspend fun generateFinalReport(): Result<List<FinalReportPeriod>> {
        return runCatching {
            val params = reportsService.getFinalReportParams()
            val fileName = getReportFileName(
                reportName = "studenttotalmarks",
                params = params.filterSources
                    .filter { it.defaultValue != null && it.items != null }
                    .map { source ->
                        ReportRequestData(
                            id = source.id,
                            defaultValue = source.defaultValue,
                            values = source.items!!.map { item ->
                                ReportRequestData.Value(
                                    name = item.name,
                                    value = item.value
                                )
                            }
                        )
                    }
            )
            val html = reportsService.getFile(fileName)
            ReportsParser.parseFinalReport(html).debugValue()
        }
    }

    private suspend fun getReportFileName(
        reportName: String,
        params: List<ReportRequestData>,
    ): String {
        val streamData = reportsService.getReportStreamData()
        val eventStream = reportsService.getReportEventStream(streamData.connectionToken)
        reportsService.startReportQueue(streamData.connectionToken)
        val task = reportsService.getReportTaskId(
            reportName = reportName,
            body = Gson().toJson(
                ReportInitQueueRequest(
                    selectedData = params.map { param ->
                        ReportInitQueueRequest.SelectedData(
                            filterId = param.id,
                            filterValue = param.values!!.first().value,
                            filterText = param.values!!.first().name,
                        )
                    },
                    params = listOf(
                        ReportInitQueueRequest.Param(
                            name = "SCHOOLYEARID",
                            value = Const.yearId.toString()
                        ),
                        ReportInitQueueRequest.Param(
                            name = "SERVERTIMEZONE",
                            value = Const.serverTimeZone
                        ),
                        ReportInitQueueRequest.Param(
                            name = "FULLSCHOOLNAME",
                            value = Const.fullSchoolName!!
                        ),
                        ReportInitQueueRequest.Param(
                            name = "DATEFORMAT",
                            value = "d\u0001mm\u0001yy\u0001."
                        )
                    )
                )
            )
        )
        reportsService.sendStartEventShortReport(
            body = "data={\"H\":\"queuehub\",\"M\":\"StartTask\",\"A\":[${task.id}],\"I\":0}",
            connectionToken = streamData.connectionToken
        )

        var fileName: String? = null
        withContext(Dispatchers.IO) {
            val input = eventStream.byteStream().bufferedReader()
            do {
                val line = input.readLine() ?: continue
                if (line.contains(Regex("\"Data\":\".+\""))) {
                    fileName = line
                        .substringAfter("\"Data\":\"")
                        .substringBefore('"')
                }
            } while (fileName == null)
        }
        reportsService.abortReport(streamData.connectionToken)

        return fileName!!
    }
}