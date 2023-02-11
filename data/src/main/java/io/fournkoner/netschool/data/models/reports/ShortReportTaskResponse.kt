package io.fournkoner.netschool.data.models.reports

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ShortReportTaskResponse(
    @SerializedName("taskId") val id: Int
)
