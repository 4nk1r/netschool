package io.fournkoner.netschool.data.repositories

import com.google.gson.Gson
import io.fournkoner.netschool.data.jsoup.ReportsParser
import io.fournkoner.netschool.data.models.reports.ShortReportInitQueueRequest
import io.fournkoner.netschool.data.network.ReportsService
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.domain.entities.reports.ReportRequestData
import io.fournkoner.netschool.domain.entities.reports.ShortReport
import io.fournkoner.netschool.domain.repositories.ReportsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    override suspend fun generateShortReport(params: List<ReportRequestData>): Result<ShortReport> {
        return runCatching {
            val streamData = reportsService.getShortReportStreamData()
            val eventStream = reportsService.getShortReportEventStream(streamData.connectionToken)
            reportsService.startShortReportQueue(streamData.connectionToken)
            val task = reportsService.getShortReportTaskId(
                Gson().toJson(
                    ShortReportInitQueueRequest(
                        selectedData = params.map { param ->
                            ShortReportInitQueueRequest.SelectedData(
                                filterId = param.id,
                                filterValue = param.values!!.first().value,
                                filterText = param.values!!.first().name,
                            )
                        },
                        params = listOf(
                            ShortReportInitQueueRequest.Param(
                                name = "SCHOOLYEARID",
                                value = Const.yearId.toString()
                            ),
                            ShortReportInitQueueRequest.Param(
                                name = "SERVERTIMEZONE",
                                value = "5" //TODO
                            ),
                            ShortReportInitQueueRequest.Param(
                                name = "FULLSCHOOLNAME",
                                value = Const.fullSchoolName!!
                            ),
                            ShortReportInitQueueRequest.Param(
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
            reportsService.abortShortReport(streamData.connectionToken)

            val html = reportsService.getFile(fileName!!)
            ReportsParser.parseShortReport(html).debugValue()
        }
    }
}