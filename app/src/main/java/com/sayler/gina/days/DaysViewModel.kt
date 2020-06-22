package com.sayler.gina.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DaysViewModel @ViewModelInject constructor() : ViewModel() {
    private val testLD = MutableLiveData(false)

    val test: LiveData<Boolean>
        get() = testLD

    fun updateTest() {
        testLD.postValue(true)
    }

}
