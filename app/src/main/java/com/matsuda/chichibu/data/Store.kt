package com.matsuda.chichibu.data

import androidx.databinding.BaseObservable

data class Store(
    var name: String,
    var imageUrl: String
) : BaseObservable()
