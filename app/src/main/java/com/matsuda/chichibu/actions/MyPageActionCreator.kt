package com.matsuda.chichibu.actions

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.matsuda.chichibu.api.*
import com.matsuda.chichibu.data.ArticleCategory
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.data.Articles
import com.matsuda.chichibu.dispatchers.Dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object MyPageActionCreator {
    fun fetchPickups(appSyncClient: AWSAppSyncClient) {
        fetch(appSyncClient, ArticleCategory.PICKUP)
    }

    fun fetchFoods(appSyncClient: AWSAppSyncClient) {
        fetch(appSyncClient, ArticleCategory.FOOD)
    }

    fun fetchEvents(appSyncClient: AWSAppSyncClient) {
        fetch(appSyncClient, ArticleCategory.EVENT)
    }

    fun fetchNews(appSyncClient: AWSAppSyncClient) {
        fetch(appSyncClient, ArticleCategory.NEWS)
    }

    fun addFavorite(
        appSyncClient: AWSAppSyncClient,
        articleId: String,
        category: ArticleCategory,
        block: Boolean.() -> Unit
    ) {
        MyFavoriteClient.addFavorite(appSyncClient, articleId, category) {
            block(this)
            if (this) {
                GlobalScope.launch {
                    val article =
                        DetailClient.getArticle(appSyncClient, articleId) ?: return@launch
                    Dispatcher.dispatch(MyPageAction.AddPickupFavorite(article))
                }
            }
        }
    }

    fun clearFavoriteCache() {
        MyFavoriteClient.clearCache()
    }

    private fun fetch(appSyncClient: AWSAppSyncClient, category: ArticleCategory) {
        GlobalScope.launch {
            val articleList = mutableListOf<Article>()
            MyFavoriteClient.listFavorites(appSyncClient, category)
                .forEach {
                    val article = DetailClient.getArticle(appSyncClient, it)
                    article?.run { articleList.add(this) }
                }

            when (category) {
                ArticleCategory.PICKUP -> {
                    Dispatcher.dispatch(MyPageAction.RefreshPickups(Articles(articleList)))
                }
                ArticleCategory.FOOD -> {
                    Dispatcher.dispatch(MyPageAction.RefreshFoods(Articles(articleList)))
                }
                ArticleCategory.NEWS -> {
                    Dispatcher.dispatch(MyPageAction.RefreshNews(Articles(articleList)))
                }
                ArticleCategory.EVENT -> {
                    Dispatcher.dispatch(MyPageAction.RefreshEvents(Articles(articleList)))
                }
            }
        }
    }
}