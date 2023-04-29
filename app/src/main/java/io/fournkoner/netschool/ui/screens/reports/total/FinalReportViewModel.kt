package io.fournkoner.netschool.ui.screens.reports.total

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.reports.FinalReportPeriod
import io.fournkoner.netschool.domain.usecases.reports.GenerateFinalReportUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

@HiltViewModel
class FinalReportViewModel @Inject constructor(
    private val generateFinalReportUseCase: GenerateFinalReportUseCase
) : ViewModel() {

    private val _report = MutableStateFlow<List<FinalReportPeriod>?>(null)
    val report: StateFlow<List<FinalReportPeriod>?> get() = _report

    init {
        viewModelScope.launch {
            generateFinalReportUseCase()
                .onSuccess {
                    _report.value = it
                }
                .onFailure {
                    @OptIn(UnreliableToastApi::class)
                    toast(R.string.error_occurred)
                }
        }
    }
}
