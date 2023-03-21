package io.fournkoner.netschool.ui.screens.info

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.journal.AssignmentDetailed
import io.fournkoner.netschool.domain.entities.journal.Journal
import io.fournkoner.netschool.domain.usecases.journal.GetDetailedAssignmentsUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetHeadersForDownloaderUseCase
import io.fournkoner.netschool.utils.debugValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

class AssignmentInfoViewModel @AssistedInject constructor(
    @Assisted assigns: List<Journal.Class.Assignment>,
    private val getDetailedAssignmentsUseCase: GetDetailedAssignmentsUseCase,
    private val getHeadersForDownloaderUseCase: GetHeadersForDownloaderUseCase
) : ScreenModel {

    private val _assignments = MutableStateFlow(emptyList<AssignmentDetailed>())
    val assignments: StateFlow<List<AssignmentDetailed>> get() = _assignments

    init {
        coroutineScope.launch {
            _assignments.value = getDetailedAssignmentsUseCase(assigns).getOrElse {
                @OptIn(UnreliableToastApi::class)
                toast(R.string.error_occurred)

                emptyList()
            }
        }
    }

    fun downloadFile(file: AssignmentDetailed.Attachment, context: Context) {
        val headers = getHeadersForDownloaderUseCase().debugValue("Headers")
        io.fournkoner.netschool.utils.downloadFile(
            name = file.name,
            link = file.file,
            downloadHeaders = headers,
            context = context
        )
    }

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(assigns: List<Journal.Class.Assignment>): AssignmentInfoViewModel
    }
}