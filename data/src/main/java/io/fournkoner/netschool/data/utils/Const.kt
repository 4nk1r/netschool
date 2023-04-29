package io.fournkoner.netschool.data.utils

import io.fournkoner.netschool.data.BuildConfig

internal object Const {

    const val HOST = BuildConfig.NETSCHOOL_BASE_URL
    const val SCHOOL_NAME = BuildConfig.SCHOOL_NAME

    var ver: String? = null
    var at: String? = null
    var studentId: Int? = null
    var yearId: Int? = null
    var fullSchoolName: String? = null
    var assignmentTitles: Map<Int, String>? = null
    var fileSizeLimit: Int? = null // kb
    const val serverTimeZone = "5" // TODO

    const val MAILBOX_PAGE_SIZE = 50
}
