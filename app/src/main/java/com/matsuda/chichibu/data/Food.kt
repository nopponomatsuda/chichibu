package com.matsuda.chichibu.data

import androidx.databinding.BaseObservable
import java.util.*

data class Food(
    val id: String,
    val name: String = "",
    val text: String = "",
    val mainImageUrl: String = "",
    val date: Date = Date(),
    val likeCount: Int = 0,
    val subImageUrls: List<String> = mutableListOf(),
    val category: ArticleCategory = ArticleCategory.PICKUP
) : BaseObservable()