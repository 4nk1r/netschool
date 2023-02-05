package io.fournkoner.netschool.data.utils

internal fun <T> List<T>.bringToFirst(selector: (T) -> Boolean): List<T> {
    val item = firstOrNull(selector) ?: return this
    val copy = toMutableList().apply {
        remove(item)
        add(0, item)
    }
    return copy
}