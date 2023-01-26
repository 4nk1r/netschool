package io.fournkoner.netschool.data.models.auth

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class AuthDataResponse(
    @SerializedName("lt") val lt: String,
    @SerializedName("salt") val salt: String,
    @SerializedName("ver") val ver: String
)
