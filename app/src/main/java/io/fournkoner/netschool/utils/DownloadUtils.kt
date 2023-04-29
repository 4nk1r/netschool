package io.fournkoner.netschool.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

fun downloadFile(
    name: String,
    link: String,
    downloadHeaders: Map<String, String>,
    context: Context
) {
    val downloadManager = context.getSystemService(DownloadManager::class.java)
    downloadManager.enqueue(
        DownloadManager.Request(link.toUri().debugValue("URI"))
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
            .setTitle(name)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .apply {
                downloadHeaders.forEach { (name, value) ->
                    addRequestHeader(name, value)
                }
            }
    )
}
