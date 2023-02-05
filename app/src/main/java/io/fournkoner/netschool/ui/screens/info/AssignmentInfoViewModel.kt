package io.fournkoner.netschool.ui.screens.info

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.domain.entities.AssignmentDetailed
import io.fournkoner.netschool.domain.entities.Journal
import io.fournkoner.netschool.domain.usecases.journal.GetDetailedAssignmentsUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetHeadersForDownloaderUseCase
import io.fournkoner.netschool.utils.debugValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentInfoViewModel @Inject constructor(
    private val getDetailedAssignmentsUseCase: GetDetailedAssignmentsUseCase,
    private val getHeadersForDownloaderUseCase: GetHeadersForDownloaderUseCase
) : ViewModel() {

    private val _assignments = MutableStateFlow(emptyList<AssignmentDetailed>())
    val assignments: StateFlow<List<AssignmentDetailed>> get() = _assignments

    fun init(assigns: List<Journal.Class.Assignment>) {
        viewModelScope.launch {
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
}