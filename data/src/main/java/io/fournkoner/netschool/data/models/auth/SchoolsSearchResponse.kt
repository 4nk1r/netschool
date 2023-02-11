package io.fournkoner.netschool.data.models.auth

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class SchoolsSearchResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("shortName") val name: String,
    @SerializedName("name") val fullName: String,
)
