package com.matsuda.chichibu.data

import androidx.databinding.BaseObservable

data class Area(
    var id: String,
    var name: String,
    var articleCount: Int,
    var hasChild: Boolean
) : BaseObservable()