package io.fournkoner.netschool.data.models.auth

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class FileSizeLimitResponse(
    @SerializedName("laImportFileSizeLimit")
    val limit: Int
)
