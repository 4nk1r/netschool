package io.fournkoner.netschool.data.models.auth

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class DiaryInitResponse(
    @SerializedName("students") val students: List<Student>,
    @SerializedName("weekStart") val startWeek: String
) {
    @Keep
    data class Student(
        @SerializedName("studentId") val id: Int,
        @SerializedName("nickName") val name: String
    )
}
