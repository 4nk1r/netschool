package io.fournkoner.netschool.ui.screens.info

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.fournkoner.netschool.domain.entities.journal.AssignmentDetailed
import io.fournkoner.netschool.domain.entities.journal.Journal
import io.fournkoner.netschool.domain.usecases.journal.GetDetailedAssignmentsUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetHeadersForDownloaderUseCase
import io.fournkoner.netschool.utils.debugValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AssignmentInfoViewModel @AssistedInject constructor(
    @Assisted assigns: List<Journal.Class.Assignment>,
    private val getDetailedAssignmentsUseCase: GetDetailedAssignmentsUseCase,
    private val getHeadersForDownloaderUseCase: GetHeadersForDownloaderUseCase
) : ScreenModel {

    private val _assignments = MutableStateFlow(emptyList<AssignmentDetailed>())
    val assignments: StateFlow<List<AssignmentDetailed>> get() = _assignments

    init {
        coroutineScope.launch {
            _assignments.value = getDetailedAssignmentsUseCase(assigns).getOrDefault(emptyList())
        }
    }

    fun downloadFile(file: AssignmentDetailed.Attachment, context: Context) {
        val headers = getHeadersForDownloaderUseCase().debugValue("Headers")
        val downloadManager = context.getSystemService(DownloadManager::class.java)
        downloadManager.enqueue(
            DownloadManager.Request(file.file.toUri().debugValue("URI"))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file.name)
                .setTitle(file.name)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .apply {
                    headers.forEach { (name, value) ->
                        addRequestHeader(name, value)
                    }
                }
        )
    }

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(assigns: List<Journal.Class.Assignment>): AssignmentInfoViewModel
    }
}