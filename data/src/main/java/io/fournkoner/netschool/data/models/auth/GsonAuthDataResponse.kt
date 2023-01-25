package io.fournkoner.netschool.data.models.auth

import com.google.gson.annotations.SerializedName

internal data class GsonAuthDataResponse(
    @SerializedName("lt") val lt: String,
    @SerializedName("salt") val salt: String,
    @SerializedName("ver") val ver: String
)
