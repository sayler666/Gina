package com.sayler.gina3.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sayler.data.entity.Day
import com.sayler.gina3.days.domain.DaysUseCase

class DaysViewModel @ViewModelInject constructor(
    private val daysUseCase: DaysUseCase
) : ViewModel() {

    val days: LiveData<List<Day>>
        get() = daysUseCase.getDays().asLiveData()

}
