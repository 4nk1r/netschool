package io.fournkoner.netschool.data.utils

import android.util.Log

internal fun <T> T.debugValue(description: String = ""): T {
    if (description.isNotEmpty()) {
        Log.d("NetSchool", "$description: $this")
    } else {
        Log.d("NetSchool", this.toString())
    }
    return this
}
