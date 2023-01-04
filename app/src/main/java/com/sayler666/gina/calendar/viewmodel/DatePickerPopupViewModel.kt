package com.sayler666.gina.calendar.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth

class DatePickerPopupViewModel(currentYearMonth: YearMonth) : ViewModel() {

    private val _date =
        MutableStateFlow(LocalDate.of(currentYearMonth.year, currentYearMonth.month, 1))
    val date: StateFlow<LocalDate>
        get() = _date.asStateFlow()

    fun plusDay() {
        _date.value = _date.value.plusDays(1)
    }

    fun plusMonth() {
        _date.value = _date.value.plusMonths(1)
    }

    fun plusYear() {
        _date.value = _date.value.plusYears(1)
    }

    fun minusDay() {
        _date.value = _date.value.minusDays(1)
    }

    fun minusMonth() {
        _date.value = _date.value.minusMonths(1)

    }

    fun minusYear() {
        _date.value = _date.value.minusYears(1)
    }

}
