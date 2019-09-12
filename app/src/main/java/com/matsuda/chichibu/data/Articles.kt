package com.matsuda.chichibu.data

import androidx.databinding.BaseObservable

data class Articles(
    var articleList: MutableList<Article>
) : BaseObservable()