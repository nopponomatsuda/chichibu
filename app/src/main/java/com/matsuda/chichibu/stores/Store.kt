package com.matsuda.chichibu.stores

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class Store : ViewModel() {
    var loading = MutableLiveData<Boolean>(true)
}