package io.fournkoner.netschool.data.network

import io.fournkoner.netschool.data.models.journal.JournalAssignmentDetailedResponse
import io.fournkoner.netschool.data.models.journal.JournalAttachmentsResponse
import io.fournkoner.netschool.data.models.journal.JournalOverdueClassesResponse
import io.fournkoner.netschool.data.models.journal.JournalResponse
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.ContentType
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface JournalService {

    @GET("webapi/student/diary")
    suspend fun getJournal(
        @Query("weekStart") weekStart: String,
        @Query("weekEnd") weekEnd: String,
        @Query("studentId") studentId: Int = Const.studentId!!,
        @Query("vers") vers: String = Const.ver!!,
        @Query("yearId") yearId: Int = Const.yearId!!,
        @Query("withLaAssigns") withLaAssigns: Boolean = true
    ): JournalResponse

    @POST("webapi/student/diary/get-attachments")
    suspend fun getAttachments(
        @Body attachmentIds: String,
        @Query("studentId") studentId: Int = Const.studentId!!,
        @Header("Content-Type") contentType: String = ContentType.JSON.string
    ): List<JournalAttachmentsResponse>

    @GET("webapi/student/diary/pastMandatory")
    suspend fun getOverdueClasses(
        @Query("weekStart") weekStart: String,
        @Query("weekEnd") weekEnd: String,
        @Query("studentId") studentId: Int = Const.studentId!!,
        @Query("yearId") yearId: Int = Const.yearId!!
    ): List<JournalOverdueClassesResponse>

    @GET("webapi/student/diary/assigns/{id}")
    suspend fun getAttachmentDetailed(
        @Path("id") id: Int,
        @Query("studentId") studentId: Int = Const.studentId!!
    ): JournalAssignmentDetailedResponse
}
