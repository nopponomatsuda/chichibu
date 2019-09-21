package com.matsuda.chichibu.actions

import com.matsuda.chichibu.data.*

interface Action<out T> {
    val data: T
}

sealed class OwnerAction<out T> : Action<T> {
    class RefreshAreas(override val data: List<String>) : OwnerAction<List<String>>()
    class ShowArticleDetail(override val data: Article) : OwnerAction<Article>()
    class RefreshPickups(override val data: Articles) : OwnerAction<Articles>()
    class RefreshFoods(override val data: Articles) : OwnerAction<Articles>()
    class RefreshEvents(override val data: Articles) : OwnerAction<Articles>()
    class RefreshNews(override val data: Articles) : OwnerAction<Articles>()
}

sealed class AreaAction<out T> : Action<T> {
    class RefreshAreas(override val data: List<Area>) : AreaAction<List<Area>>()
}

sealed class ProfileAction<out T> : Action<T> {
    class RefreshProfile(override val data: Profile) : ProfileAction<Profile>()
}

sealed class ArticleAction<out T> : Action<T> {
    class RefreshPickups(override val data: Articles) : ArticleAction<Articles>()
    class RefreshFoods(override val data: Articles) : ArticleAction<Articles>()
    class RefreshEvents(override val data: Articles) : ArticleAction<Articles>()
    class RefreshNews(override val data: Articles) : ArticleAction<Articles>()
}

sealed class DetailAction<out T> : Action<T> {
    class ShowArticleDetail(override val data: Article) : DetailAction<Article>()
}

sealed class MyPageAction<out T> : Action<T> {
    class RefreshPickups(override val data: Articles) : MyPageAction<Articles>()
    class RefreshFoods(override val data: Articles) : MyPageAction<Articles>()
    class RefreshEvents(override val data: Articles) : MyPageAction<Articles>()
    class RefreshNews(override val data: Articles) : MyPageAction<Articles>()
    class AddPickupFavorite(override val data: Article) : MyPageAction<Article>()
    class AddFoodsFavorite(override val data: Article) : MyPageAction<Article>()
    class AddEventsFavorite(override val data: Article) : MyPageAction<Article>()
    class AddNewsFavorite(override val data: Article) : MyPageAction<Article>()
}