package io.fournkoner.netschool.data.utils

import java.nio.charset.Charset
import java.security.MessageDigest

internal fun hexMD5(string: String, charset: String = "UTF-8"): String {
    return hexMD5(string.toByteArray(Charset.forName(charset)))
}

internal fun hexMD5(bytes: ByteArray): String {
    val stringBuilder = StringBuilder()
    MessageDigest.getInstance("MD5").digest(bytes).forEach {
        stringBuilder.append(Integer.toHexString(it.toInt() and 0xFF or 0x100), 1, 3)
    }
    return stringBuilder.toString()
}
