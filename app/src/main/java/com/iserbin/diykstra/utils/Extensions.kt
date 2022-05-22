package com.iserbin.diykstra.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

fun <T, LD : LiveData<T>> Fragment.observe(liveData: LD, onChanged: (T) -> Unit) {
    liveData.observe(
        viewLifecycleOwner,
        {
            onChanged(it)
        }
    )
}