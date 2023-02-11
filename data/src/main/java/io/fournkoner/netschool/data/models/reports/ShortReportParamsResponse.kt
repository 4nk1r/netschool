package io.fournkoner.netschool.data.models.reports

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ShortReportParamsResponse(
    @SerializedName("filterSources") val filterSources: List<FilterSource>,
) {

    @Keep
    data class FilterSource(
        @SerializedName("filterId") val id: String,
        @SerializedName("defaultValue") val defaultValue: String?,
        @SerializedName("items") val items: List<Item>?,
    ) {

        @Keep
        data class Item(
            @SerializedName("title") val name: String,
            @SerializedName("value") val value: String,
        )
    }
}
