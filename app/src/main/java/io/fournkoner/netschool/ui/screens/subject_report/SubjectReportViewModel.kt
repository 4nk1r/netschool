package io.fournkoner.netschool.ui.screens.subject_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.domain.entities.reports.ReportRequestData
import io.fournkoner.netschool.domain.entities.reports.SubjectReport
import io.fournkoner.netschool.domain.usecases.reports.GenerateSubjectReportUseCase
import io.fournkoner.netschool.domain.usecases.reports.GetSubjectReportRequestDataUseCase
import io.fournkoner.netschool.utils.debugValue
import io.fournkoner.netschool.utils.toLocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectReportViewModel @Inject constructor(
    private val getSubjectReportRequestDataUseCase: GetSubjectReportRequestDataUseCase,
    private val generateSubjectReportUseCase: GenerateSubjectReportUseCase
) : ViewModel() {

    private var immutableRequestData = emptyList<ReportRequestData>()

    private val _availableRange = MutableStateFlow<LongRange?>(null)
    private val _defaultRange = MutableStateFlow<LongRange?>(null)
    private val _subjects = MutableStateFlow<List<ReportRequestData.Value>?>(null)

    private val _selectedSubject = MutableStateFlow<ReportRequestData.Value?>(null)
    private val _selectedRange = MutableStateFlow<String?>(null)

    val availableRange: StateFlow<LongRange?> get() = _availableRange
    val defaultRange: StateFlow<LongRange?> get() = _defaultRange
    val subjects: StateFlow<List<ReportRequestData.Value>?> get() = _subjects

    val selectedSubject: StateFlow<ReportRequestData.Value?> get() = _selectedSubject
    val selectedRange: StateFlow<String?> get() = _selectedRange

    private val _subjectReport = MutableStateFlow<SubjectReport?>(null)
    private val _reportEmpty = MutableStateFlow(false)

    val subjectReport: StateFlow<SubjectReport?> get() = _subjectReport
    val reportEmpty: StateFlow<Boolean> get() = _reportEmpty

    init {
        viewModelScope.launch {
            getSubjectReportRequestDataUseCase().getOrNull().debugValue()?.let { data ->
                _availableRange.value = data.period.availableRange
                _defaultRange.value = data.period.defaultStart..data.period.defaultEnd
                _subjects.value = data.pairs.find { it.id == SUBJECTS_PARAM_ID }?.values

                immutableRequestData = data.pairs.filter {
                    it.id != SUBJECTS_PARAM_ID && it.id != PERIOD_PARAM_ID
                }
            }
        }
    }

    fun generate(period: LongRange) {
        viewModelScope.launch {
            val start = period.first.toLocalDate().run {
                String.format("%02d.%02d.%s", dayOfMonth, monthValue, year.toString().takeLast(2))
            }
            val end = period.last.toLocalDate().run {
                String.format("%02d.%02d.%s", dayOfMonth, monthValue, year.toString().takeLast(2))
            }
            _selectedRange.value = "$start-$end"

            generateSubjectReportUseCase(
                params = immutableRequestData + listOf(
                    ReportRequestData(
                        id = SUBJECTS_PARAM_ID,
                        defaultValue = _selectedSubject.value!!.value,
                        values = listOf(_selectedSubject.value!!)
                    ),
                    ReportRequestData(
                        id = PERIOD_PARAM_ID,
                        defaultValue = "$start - $end",
                        values = listOf(
                            ReportRequestData.Value(
                                name = "$start - $end",
                                value = period.first.toLocalDate().run {
                                    String.format(
                                        "%d-%02d-%02dT00:00:00.000Z",
                                        year,
                                        monthValue,
                                        dayOfMonth,
                                    )
                                } + " - " + period.last.toLocalDate().run {
                                    String.format(
                                        "%d-%02d-%02dT00:00:00.000Z",
                                        year,
                                        monthValue,
                                        dayOfMonth,
                                    )
                                }
                            )
                        )
                    )
                )
            ).onSuccess {
                _subjectReport.value = it
                _reportEmpty.value = it == null
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun resetChoice() {
        _selectedSubject.value = null
        _selectedRange.value = null
        _subjectReport.value = null
        _reportEmpty.value = false
    }

    fun selectSubject(id: String?) {
        _selectedSubject.value = _subjects.value?.find {it.value == id }
    }

    companion object {

        private const val SUBJECTS_PARAM_ID = "SGID"
        private const val PERIOD_PARAM_ID = "period"
    }
}