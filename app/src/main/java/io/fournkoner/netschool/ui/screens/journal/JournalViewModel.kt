package io.fournkoner.netschool.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.domain.entities.Journal
import io.fournkoner.netschool.domain.usecases.journal.GetJournalUseCase
import io.fournkoner.netschool.utils.currentWeekEnd
import io.fournkoner.netschool.utils.currentWeekStart
import io.fournkoner.netschool.utils.getWeekEndFromCurrent
import io.fournkoner.netschool.utils.getWeekStartFromCurrent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val getJournalUseCase: GetJournalUseCase,
) : ViewModel() {

    private var weekOffset = 0

    private val _journal = MutableStateFlow<Journal?>(null)
    val journal: StateFlow<Journal?> get() = _journal

    private val _week = MutableStateFlow(getWeekString(currentWeekStart, currentWeekEnd))
    val week: StateFlow<String> get() = _week

    init {
        loadWeek()
    }

    fun nextWeek() {
        weekOffset++
        loadWeek()
    }

    fun previousWeek() {
        weekOffset--
        loadWeek()
    }

    private fun loadWeek() {
        _journal.value = null

        viewModelScope.launch {
            val wStart = getWeekStartFromCurrent(offset = weekOffset)
            val wEnd = getWeekEndFromCurrent(offset = weekOffset)

            _journal.value = getJournalUseCase(weekStart = wStart, weekEnd = wEnd).getOrNull()
            _week.value = getWeekString(wStart, wEnd)
        }
    }

    private fun getWeekString(weekStart: String, weekEnd: String): String {
        val start = LocalDate.parse(weekStart).run {
            String.format("%02d.%02d.%s", dayOfMonth, monthValue, year.toString().takeLast(2))
        }
        val end = LocalDate.parse(weekEnd).run {
            String.format("%02d.%02d.%s", dayOfMonth, monthValue, year.toString().takeLast(2))
        }
        return "$start-$end"
    }
}