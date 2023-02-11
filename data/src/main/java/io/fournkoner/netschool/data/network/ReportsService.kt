package io.fournkoner.netschool.data.network

import io.fournkoner.netschool.data.models.reports.ShortReportParamsResponse
import io.fournkoner.netschool.data.models.reports.ShortReportStreamDataResponse
import io.fournkoner.netschool.data.models.reports.ShortReportTaskResponse
import io.fournkoner.netschool.data.utils.Accept
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.ContentType
import okhttp3.ResponseBody
import retrofit2.http.*

internal interface ReportsService {

    @GET("webapi/reports/parentinfoletter")
    suspend fun getShortReportParams(): ShortReportParamsResponse

    @GET("WebApi/signalr/negotiate")
    suspend fun getShortReportStreamData(
        @Query("clientProtocol") clientProtocol: String = "1.5",
        @Query("at") at: String = Const.at!!,
        @Query("connectionData") connectionData: String = "[{\"name\":\"queuehub\"}]",
        @Query("_") huh: Long = System.currentTimeMillis(),
    ): ShortReportStreamDataResponse

    @GET("WebApi/signalr/connect")
    @Streaming
    suspend fun getShortReportEventStream(
        @Query("connectionToken") connectionToken: String,
        @Query("transport") transport: String = "serverSentEvents",
        @Query("clientProtocol") clientProtocol: String = "1.5",
        @Query("at") at: String = Const.at!!,
        @Query("connectionData") connectionData: String = "[{\"name\":\"queuehub\"}]",
        @Query("tid") tid: Int = 6,
        @Header("Accept") accept: String = Accept.EVENT_STREAM.string,
    ): ResponseBody

    @GET("WebApi/signalr/start")
    suspend fun startShortReportQueue(
        @Query("connectionToken") connectionToken: String,
        @Query("transport") transport: String = "serverSentEvents",
        @Query("clientProtocol") clientProtocol: String = "1.5",
        @Query("connectionData") connectionData: String = "[{\"name\":\"queuehub\"}]",
        @Query("at") at: String = Const.at!!,
        @Query("_") huh: Long = System.currentTimeMillis(),
    )

    @POST("webapi/reports/parentinfoletter/queue")
    suspend fun getShortReportTaskId(
        @Body body: String,
        @Header("Content-Type") contentType: String = ContentType.JSON.string
    ): ShortReportTaskResponse

    @POST("WebApi/signalr/send")
    suspend fun sendStartEventShortReport(
        @Body body: String,
        @Query("connectionToken") connectionToken: String,
        @Query("transport") transport: String = "serverSentEvents",
        @Query("clientProtocol") clientProtocol: String = "1.5",
        @Query("at") at: String = Const.at!!,
        @Query("connectionData") connectionData: String = "[{\"name\":\"queuehub\"}]",
        @Header("Content-Type") contentType: String = ContentType.FORM_URL_ENCODED.string
    )

    @POST("WebApi/signalr/abort")
    suspend fun abortShortReport(
        @Query("connectionToken") connectionToken: String,
        @Query("transport") transport: String = "serverSentEvents",
        @Query("clientProtocol") clientProtocol: String = "1.5",
        @Query("connectionData") connectionData: String = "[{\"name\":\"queuehub\"}]",
        @Query("at") at: String = Const.at!!,
        @Header("Content-Type") contentType: String = ContentType.JSON.string
    )

    @GET("webapi/files/{file}")
    suspend fun getFile(@Path("file") file: String): String
}