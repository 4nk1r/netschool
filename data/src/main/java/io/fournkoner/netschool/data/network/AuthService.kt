package io.fournkoner.netschool.data.network

import io.fournkoner.netschool.data.models.auth.*
import io.fournkoner.netschool.data.utils.ContentType
import retrofit2.http.*

internal interface AuthService {

    @POST("webapi/auth/getdata")
    suspend fun getAuthData(): AuthDataResponse

    @GET("webapi/schools/search")
    suspend fun findSchool(): List<SchoolsSearchResponse>

    @POST("webapi/login")
    suspend fun signIn(
        @Body body: String,
        @Header("Content-Type") contentType: String = ContentType.FORM_URL_ENCODED.string,
    ): AuthResponse

    @GET("webapi/student/diary/init")
    suspend fun initDiary(): DiaryInitResponse

    @GET("webapi/years/current")
    suspend fun getCurrentYear(): CurrentYearResponse

    @GET("webapi/grade/assignment/types")
    suspend fun getAssignmentTypes(@Query("all") all: Boolean = false): List<AssignmentTypesResponse>

    @GET("webapi/attachments/uploadLimits")
    suspend fun getFileSizeLimit(): FileSizeLimitResponse

    @POST("webapi/auth/logout")
    suspend fun logout()
}