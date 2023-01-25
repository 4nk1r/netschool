package io.fournkoner.netschool.data.models.auth

import com.google.gson.annotations.SerializedName

data class SchoolsSearchResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("shortName") val name: String
)
