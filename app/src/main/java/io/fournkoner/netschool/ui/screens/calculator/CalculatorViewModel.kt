package io.fournkoner.netschool.ui.screens.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * @param grades grade to count
 */
class CalculatorViewModel(private val grades: Map<Int, Int>) : ViewModel() {

    private val _average = MutableStateFlow(0f)
    val average = _average.asStateFlow()

    /**
     * grade to count
     */
    private val _added = MutableStateFlow(emptyMap<Int, Int>())
    val added = _added.asStateFlow()

    /**
     * average to map of grade to count
     *
     * for example `4,53f to mapOf(5, 2)` that means you need two more "5" marks to
     * get 4,53 averagely
     */
    private val _neededMarks = MutableStateFlow(emptyList<Pair<Float, Map<Int, Int>>>())
    val neededMarks = _neededMarks.asStateFlow()

    init {
        viewModelScope.launch {
            val average = recalculateAverage()
            _average.value = average

            val neededMap = mutableMapOf<Float, Map<Int, Int>>()
            if (average < 4.5f) {
                calculateNeededMarks(5).forEach { (key, marks) -> neededMap[key] = marks }
                if (average < 3.5f) {
                    calculateNeededMarks(4).forEach { (key, marks) -> neededMap[key] = marks }
                    if (average < 2.5f) {
                        calculateNeededMarks(3).forEach { (key, marks) -> neededMap[key] = marks }
                    }
                }
            }
            _neededMarks.value = neededMap.toList().sortedByDescending { it.first }
            groupNeededMarks()
        }
    }

    fun plus(mark: Int) {
        _added.value = _added.value.toMutableMap().apply {
            put(mark, getOrDefault(mark, 0) + 1)
        }
        _average.value = recalculateAverage()
    }

    fun minus(mark: Int) {
        _added.value = _added.value.toMutableMap().apply {
            put(mark, getOrElse(mark) { throw IllegalAccessException() } - 1)
        }
        _average.value = recalculateAverage()
    }

    private fun recalculateAverage(extraMarks: Map<Int, Int> = _added.value): Float {
        var totalCount = 0f
        var totalSum = 0

        grades
            .mapValues { it.value + extraMarks.getOrDefault(it.key, 0) }
            .forEach { (mark, count) ->
                totalSum += mark * count
                totalCount += count
            }

        return totalSum / totalCount
    }

    private fun calculateNeededMarks(to: Int): List<Pair<Float, Map<Int, Int>>> {
        val target = to - 0.5f
        val map = mutableListOf<Pair<Float, Map<Int, Int>>>()

        for (mark in to..5) {
            var i = 0
            var a: Float

            do {
                a = recalculateAverage(mapOf(mark to i++))
            } while (a < target)

            map += a to mapOf(mark to --i)
        }

        return map
    }

    private fun groupNeededMarks() {
        val result = mutableMapOf<Float, Map<Int, Int>>()

        for (mark in 3..5) {
            val temp = _neededMarks.value.filter { it.first.roundToInt() == mark }

            if (temp.isNotEmpty()) {
                val key = temp.minOf { it.first }
                temp.forEach {
                    result[key] = result.getOrPut(key) { emptyMap() } + it.second
                }
            }
        }

        _neededMarks.value = result.toList()
    }

    /**
     * @param grades grade to count
     */
    class Factory(private val grades: Map<Int, Int>) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val formattedGrades = mapOf(
                2 to grades.getOrDefault(2, 0),
                3 to grades.getOrDefault(3, 0),
                4 to grades.getOrDefault(4, 0),
                5 to grades.getOrDefault(5, 0),
            )
            return CalculatorViewModel(formattedGrades) as T
        }
    }
}