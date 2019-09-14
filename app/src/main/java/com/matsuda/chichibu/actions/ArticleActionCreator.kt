package com.matsuda.chichibu.actions

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.matsuda.chichibu.api.*
import com.matsuda.chichibu.data.ArticleCategory
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.dispatchers.Dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

object ArticleActionCreator {
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

    fun showDetail(appSyncClient: AWSAppSyncClient, articleId: String) {
        GlobalScope.launch {
            val article = DetailClient.getArticle(appSyncClient, articleId) ?: return@launch
            Dispatcher.dispatch(DetailAction.ShowArticleDetail(article))
        }
    }

    fun clearArticleCache() {
        ArticleClient.clearCache()
    }

    fun uploadFile(
        transferUtility: TransferUtility,
        file: File,
        key: String,
        block: Boolean.() -> Unit
    ) {
        ArticleClient.uploadFile(transferUtility, file, key, block)
    }

    fun saveArticle(
        appSyncClient: AWSAppSyncClient,
        article: Article,
        block: Boolean.() -> Unit
    ) {
        ArticleClient.saveArticle(appSyncClient, article, block)
    }


    private fun fetch(appSyncClient: AWSAppSyncClient, category: ArticleCategory) {
        GlobalScope.launch {
            val articles = ArticleClient.listArticles(appSyncClient, category)
            when (category) {
                ArticleCategory.PICKUP -> {
                    Dispatcher.dispatch(ArticleAction.RefreshPickups(articles))
                }
                ArticleCategory.FOOD -> {
                    Dispatcher.dispatch(ArticleAction.RefreshFoods(articles))
                }
                ArticleCategory.EVENT -> {
                    Dispatcher.dispatch(ArticleAction.RefreshEvents(articles))
                }
                ArticleCategory.NEWS -> {
                    Dispatcher.dispatch(ArticleAction.RefreshNews(articles))
                }
            }
        }
    }
}