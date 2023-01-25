package io.fournkoner.netschool.data.network

import io.fournkoner.netschool.data.models.auth.*
import io.fournkoner.netschool.data.utils.ContentType
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

internal interface AuthService {

    @POST("webapi/auth/getdata")
    suspend fun getAuthData(): GsonAuthDataResponse

    @GET("webapi/schools/search")
    suspend fun findSchool(): List<SchoolsSearchResponse>

    @POST("webapi/login")
    suspend fun signIn(
        @Body body: String,
        @Header("Content-Type") contentType: String = ContentType.FORM_URL_ENCODED.string,
    ): GsonAuthResponse

    @GET("webapi/student/diary/init")
    suspend fun initDiary(): GsonDiaryInitResponse

    @GET("webapi/years/current")
    suspend fun getCurrentYear(): CurrentYearResponse

    @POST("webapi/auth/logout")
    suspend fun logout()
}