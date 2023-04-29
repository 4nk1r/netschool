package io.fournkoner.netschool.data.models.journal

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class JournalResponse(
    @SerializedName("weekDays") val weekDays: List<Day>,
    @SerializedName("weekStart") val weekStart: String,
    @SerializedName("weekEnd") val weekEnd: String
) {

    @Keep
    data class Day(
        @SerializedName("date") val date: String,
        @SerializedName("lessons") val classes: List<Class>
    )

    @Keep
    data class Class(
        @SerializedName("number") val position: Int,
        @SerializedName("subjectName") val name: String,
        @SerializedName("assignments") val assignments: List<Assignment>?
    ) {

        @Keep
        data class Assignment(
            @SerializedName("id") val id: Int,
            @SerializedName("assignmentName") val name: String,
            @SerializedName("mark") val grade: Grade? = null,
            @SerializedName("typeId") val type: Int
        )

        @Keep
        data class Grade(
            @SerializedName("mark") val mark: Int?
        )
    }
}
