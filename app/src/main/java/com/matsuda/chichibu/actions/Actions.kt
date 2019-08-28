package com.matsuda.chichibu.actions

import com.matsuda.chichibu.data.*

interface Action<out T> {
    val data: T
}

sealed class PickupAction<out T> : Action<T> {
    class RefreshPickups(override val data: Articles) : PickupAction<Articles>()
}

sealed class FoodAction<out T> : Action<T> {
    class RefreshFoods(override val data: Foods) : FoodAction<Foods>()
}

//TODO create data object
sealed class EventAction<out T> : Action<T> {
    class RefreshEvents(override val data: Articles) : EventAction<Articles>()
}

//TODO create data object
sealed class NewsAction<out T> : Action<T> {
    class RefreshNews(override val data: Articles) : NewsAction<Articles>()
}

sealed class DetailAction<out T> : Action<T> {
    class ShowArticleDetail(override val data: Article) : DetailAction<Article>()
}
