package io.fournkoner.netschool.data.models.reports

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ShortReportInitQueueRequest(
    @SerializedName("params") val params: List<Param>,
    @SerializedName("selectedData") val selectedData: List<SelectedData>
) {

    @Keep
    data class Param(
        @SerializedName("name") val name: String,
        @SerializedName("value") val value: String
    )

    @Keep
    data class SelectedData(
        @SerializedName("filterId") val filterId: String,
        @SerializedName("filterValue") val filterValue: String,
        @SerializedName("filterText") val filterText: String
    )
}
