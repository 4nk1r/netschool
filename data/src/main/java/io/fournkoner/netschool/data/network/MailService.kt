package io.fournkoner.netschool.data.network

import io.fournkoner.netschool.data.models.mail.MailboxResponse
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.ContentType
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
}