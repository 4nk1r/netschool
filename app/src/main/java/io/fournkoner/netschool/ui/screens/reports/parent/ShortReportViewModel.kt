package io.fournkoner.netschool.ui.screens.reports.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.reports.ReportRequestData
import io.fournkoner.netschool.domain.entities.reports.ShortReport
import io.fournkoner.netschool.domain.usecases.reports.GenerateShortReportUseCase
import io.fournkoner.netschool.domain.usecases.reports.GetShortReportRequestDataUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

@HiltViewModel
class ShortReportViewModel @Inject constructor(
    private val getShortReportRequestDataUseCase: GetShortReportRequestDataUseCase,
    private val generateShortReportUseCase: GenerateShortReportUseCase
) : ViewModel() {

    private val immutableRequestData = mutableListOf<ReportRequestData>()
    private var lastSelectedPeriod: ReportRequestData.Value? = null

    private val _periods = MutableStateFlow<List<ReportRequestData.Value>?>(null)
    val periods: StateFlow<List<ReportRequestData.Value>?> get() = _periods

    private val _report = MutableStateFlow<ShortReport?>(null)
    val report: StateFlow<ShortReport?> get() = _report

    private val _reportEmpty = MutableStateFlow(false)
    val reportEmpty: StateFlow<Boolean> get() = _reportEmpty

    init {
        viewModelScope.launch {
            getShortReportRequestDataUseCase().getOrElse {
                @OptIn(UnreliableToastApi::class)
                toast(R.string.error_occurred)

                null
            }?.let { reportRequestData ->
                reportRequestData.forEach { item ->
                    if (item.id == REQUEST_DATA_ITEM_ID_PERIOD) {
                        _periods.value = item.values
                    } else {
                        immutableRequestData += item
                    }
                }
            }
        }
    }

    fun generate(period: ReportRequestData.Value) {
        if (lastSelectedPeriod == period) return

        _reportEmpty.value = false
        _report.value = null
        lastSelectedPeriod = period

        viewModelScope.launch {
            generateShortReportUseCase(
                params = immutableRequestData + listOf(
                    ReportRequestData(
                        id = REQUEST_DATA_ITEM_ID_PERIOD,
                        defaultValue = period.value,
                        values = listOf(period)
                    )
                )
            ).onSuccess {
                _report.value = it
                _reportEmpty.value = it == null
            }.onFailure {
                @OptIn(UnreliableToastApi::class)
                toast(R.string.error_occurred)
            }
        }
    }

    companion object {

        private const val REQUEST_DATA_ITEM_ID_PERIOD = "TERMID"
    }
}
