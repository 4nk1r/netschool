package io.fournkoner.netschool.domain.entities.reports

data class ReportRequestData(
    val id: String,
    val defaultValue: String?,
    val values: List<Value>?
) {

     data class Value(
         val name: String,
         val value: String
     )
}