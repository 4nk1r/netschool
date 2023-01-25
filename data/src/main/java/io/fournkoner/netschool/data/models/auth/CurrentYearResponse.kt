package io.fournkoner.netschool.data.models.auth

import com.google.gson.annotations.SerializedName

internal data class CurrentYearResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
)
