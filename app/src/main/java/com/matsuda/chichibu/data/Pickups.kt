package com.matsuda.chichibu.data

import androidx.databinding.BaseObservable

data class Pickups(
    var articleList: List<Article>
) : BaseObservable()