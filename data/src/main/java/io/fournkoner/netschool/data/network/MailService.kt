package io.fournkoner.netschool.data.network

import io.fournkoner.netschool.data.models.mail.MailboxResponse
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.ContentType
import io.fournkoner.netschool.data.utils.toRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

internal interface MailService {

    @POST("angular/school/studentdiary")
    suspend fun getUnreadMessagesCount(
        @Body body: String,
        @Header("Content-Type") contentType: String = ContentType.FORM_URL_ENCODED.string,
    ): String

    @GET("asp/ajax/GetMessagesAjax.asp")
    suspend fun getMailbox(
        @Query("nBoxID") mailboxId: Int,
        @Query("jtStartIndex") startIndex: Int,
        @Query("jtPageSize") pageSize: Int,
        @Query("AT") at: String = Const.at!!,
        @Query("jtSorting") sortingMode: String = "Sent DESC",
    ): MailboxResponse

    @GET("asp/Messages/readmessage.asp")
    @Streaming
    suspend fun getMailMessageDetailed(
        @Query("MID") id: Int,
        @Query("MBID") mbid: Int = 1,
        @Query("ver") ver: String = Const.ver!!,
        @Query("at") at: String = Const.at!!
    ): ResponseBody

    @POST("asp/ajax/DeleteMessagesAjax.asp")
    suspend fun deleteMessages(
        @Body body: String,
        @Header("Content-Type") contentType: String = ContentType.FORM_URL_ENCODED.string
    )

    @POST("asp/Messages/addrbkleft.asp")
    suspend fun getMessageReceivers(
        @Body body: String,
        @Header("Content-Type") contentType: String = ContentType.FORM_URL_ENCODED.string
    ): String

    @POST("webapi/attachments")
    @Multipart
    suspend fun uploadFile(
        @Part filePart: MultipartBody.Part,
        @Part("data") dataPart: RequestBody = "{\"MessageId\":0,\"Description\":\"\"}".toRequestBody(),
        @Part("at") atPart: RequestBody = Const.at!!.toRequestBody(),
    ): Int

    @GET("asp/Messages/composemessage.asp")
    suspend fun getSendMessageData(
        @Query("ver") ver: String = Const.ver!!,
        @Query("at") at: String = Const.at!!
    ): String

    @POST("asp/Messages/sendsavemsg.asp")
    suspend fun sendMessage(
        @Body body: String,
        @Header("Content-Type") contentType: String = ContentType.FORM_URL_ENCODED.string
    )
}