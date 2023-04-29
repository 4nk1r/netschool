package io.fournkoner.netschool.data.models.reports

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ReportStreamDataResponse(
    @SerializedName("ConnectionId") val connectionId: String,
    @SerializedName("ConnectionToken") val connectionToken: String
)
