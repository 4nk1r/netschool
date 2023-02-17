package io.fournkoner.netschool.data.network

import io.fournkoner.netschool.data.utils.ContentType
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

internal interface MailService {

    @POST("angular/school/studentdiary")
    suspend fun getUnreadMessagesCount(
        @Body body: String,
        @Header("Content-Type") contentType: String = ContentType.FORM_URL_ENCODED.string
    ): String
}