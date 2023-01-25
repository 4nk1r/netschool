package io.fournkoner.netschool.data.models.auth

import com.google.gson.annotations.SerializedName

internal data class GsonAuthResponse(
    @SerializedName("at") val at: String,
    @SerializedName("timeOut") val timeout: Int,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
)
