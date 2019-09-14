package com.matsuda.chichibu.data

import androidx.databinding.BaseObservable
import java.util.*

data class Article(
    var id: String = "",
    var title: String? = null,
    var text: String? = null,
    var mainImageUrl: String? = null,
    var subTitle: String? = null,
    var date: Date = Date(),
    var likeCount: Int = 0,
    var subImageUrls: List<String> = mutableListOf(),
    var category: ArticleCategory = ArticleCategory.PICKUP
) : BaseObservable()