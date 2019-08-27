package com.matsuda.chichibu.data

import androidx.databinding.BaseObservable

data class Foods(
    var articleList: List<Article>
) : BaseObservable()