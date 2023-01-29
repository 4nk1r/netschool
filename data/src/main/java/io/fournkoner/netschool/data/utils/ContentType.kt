package io.fournkoner.netschool.data.utils

internal enum class ContentType(val string: String) {
    FORM_URL_ENCODED("application/x-www-form-urlencoded; charset=UTF-8"),
    JSON("application/json;charset=UTF-8")
}