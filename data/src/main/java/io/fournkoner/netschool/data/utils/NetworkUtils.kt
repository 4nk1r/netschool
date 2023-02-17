package io.fournkoner.netschool.data.utils

import okhttp3.Request

internal fun <K, V> Map<K, V>.toFormUrlEncodedString(): String {
    return map { "${it.key}=${it.value}" }.joinToString("&")
}

internal fun Request.insertHeaders(): Request {
    return newBuilder()
        .header("Accept", "application/json, text/javascript, */*; q=0.01")
        .header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
        .header("X-Requested-With", "XMLHttpRequest")
        .header("Origin", Const.HOST)
        .header("Referer", this.url().toString())
        .header(
            "User-Agent",
            " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36"
        )
        .apply { if (Const.at != null) header("at", Const.at!!) }
        .build()
}