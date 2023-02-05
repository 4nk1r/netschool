package io.fournkoner.netschool.ui.screens.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.domain.entities.AssignmentDetailed
import io.fournkoner.netschool.domain.entities.Journal
import io.fournkoner.netschool.domain.usecases.journal.GetDetailedAssignmentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentInfoViewModel @Inject constructor(
    private val getDetailedAssignmentsUseCase: GetDetailedAssignmentsUseCase,
) : ViewModel() {

    private val _assignments = MutableStateFlow(emptyList<AssignmentDetailed>())
    val assignments: StateFlow<List<AssignmentDetailed>> get() = _assignments

    fun init(assigns: List<Journal.Class.Assignment>) {
        viewModelScope.launch {
            _assignments.value = getDetailedAssignmentsUseCase(assigns).getOrDefault(emptyList())
        }
    }
}