package com.matsuda.chichibu.data

import androidx.databinding.BaseObservable

data class Foods(
    var foodList: List<Food>
) : BaseObservable()