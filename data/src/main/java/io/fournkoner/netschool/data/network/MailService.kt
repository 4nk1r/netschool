package io.fournkoner.netschool.data.network

import io.fournkoner.netschool.data.models.mail.MailboxResponse
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.ContentType
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
}