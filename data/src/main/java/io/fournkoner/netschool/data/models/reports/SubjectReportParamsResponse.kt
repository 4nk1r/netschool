package io.fournkoner.netschool.data.models.reports

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class SubjectReportParamsResponse(
    @SerializedName("filterSources") val filterSources: List<FilterSource>,
) {

    @Keep
    data class FilterSource(
        @SerializedName("filterId") val id: String,
        @SerializedName("defaultValue") val defaultValue: String?,
        @SerializedName("items") val items: List<Item>?,
        @SerializedName("range") val range: Range?,
        @SerializedName("defaultRange") val defaultRange: Range?
    ) {

        @Keep
        data class Range(
            @SerializedName("start") val start: String,
            @SerializedName("end") val end: String,
        )

        @Keep
        data class Item(
            @SerializedName("title") val name: String,
            @SerializedName("value") val value: String,
        )
    }
}
