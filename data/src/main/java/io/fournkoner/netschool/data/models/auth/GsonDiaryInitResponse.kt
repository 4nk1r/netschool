package io.fournkoner.netschool.data.models.auth

import com.google.gson.annotations.SerializedName

internal data class GsonDiaryInitResponse(
    @SerializedName("students") val students: List<Student>,
    @SerializedName("weekStart") val startWeek: String
) {
    data class Student(
        @SerializedName("studentId") val id: Int,
        @SerializedName("nickName") val name: String
    )
}
