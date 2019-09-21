package com.matsuda.chichibu.actions

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.matsuda.chichibu.api.ArticleClient
import com.matsuda.chichibu.api.DetailClient
import com.matsuda.chichibu.data.*
import com.matsuda.chichibu.dispatchers.Dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object OwnerActionCreator {
    fun fetchChildrenArea(parentArea: Prefecture) {
        GlobalScope.launch {
            //TODO fetch area info from API
            val childAreaList = mutableListOf<String>()
            childAreaList.add("chichibu")
            Dispatcher.dispatch(OwnerAction.RefreshAreas(childAreaList))
        }
    }

    fun showDetail(appSyncClient: AWSAppSyncClient, articleId: String) {
        GlobalScope.launch {
            val article = DetailClient.getArticle(appSyncClient, articleId) ?: return@launch
            Dispatcher.dispatch(OwnerAction.ShowArticleDetail(article))
        }
    }

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

    private fun fetch(appSyncClient: AWSAppSyncClient, category: ArticleCategory) {
        GlobalScope.launch {
            val articles = ArticleClient.listOwnerArticles(appSyncClient, category)

            when (category) {
                ArticleCategory.PICKUP -> {
                    Dispatcher.dispatch(OwnerAction.RefreshPickups(articles))
                }
                ArticleCategory.FOOD -> {
                    Dispatcher.dispatch(OwnerAction.RefreshFoods(articles))
                }
                ArticleCategory.NEWS -> {
                    Dispatcher.dispatch(OwnerAction.RefreshNews(articles))
                }
                ArticleCategory.EVENT -> {
                    Dispatcher.dispatch(OwnerAction.RefreshEvents(articles))
                }
            }
        }
    }
}