package com.matsuda.chichibu.actions

import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.data.Foods
import com.matsuda.chichibu.data.Pickups

interface Action<out T> {
    val data: T
}

sealed class PickupAction<out T> : Action<T> {
    class RefreshArticles(override val data: Pickups) : PickupAction<Pickups>()
}

sealed class FoodAction<out T> : Action<T> {
    class RefreshFoods(override val data: Foods) : FoodAction<Foods>()
}

sealed class DetailAction<out T> : Action<T> {
    class ShowArticleDetail(override val data: Article) : DetailAction<Article>()
}
