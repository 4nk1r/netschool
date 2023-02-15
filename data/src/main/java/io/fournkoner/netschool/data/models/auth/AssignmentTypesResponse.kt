package io.fournkoner.netschool.data.models.auth

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class AssignmentTypesResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
