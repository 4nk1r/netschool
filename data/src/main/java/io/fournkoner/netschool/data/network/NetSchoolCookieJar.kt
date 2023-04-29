package io.fournkoner.netschool.data.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

object NetSchoolCookieJar : CookieJar {

    val cookies = mutableListOf<Cookie>()

    override fun loadForRequest(httpUrl: HttpUrl): List<Cookie> {
        cookies.removeAll { it.expiresAt() < System.currentTimeMillis() }
        return cookies.filter { it.matches(httpUrl) }
    }

    override fun saveFromResponse(httpUrl: HttpUrl, cookies: List<Cookie>) {
        this.cookies.addAll(cookies)
    }
}
