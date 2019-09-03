package com.matsuda.chichibu.actions

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.matsuda.chichibu.api.*
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.dispatchers.Dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

object ActionsCreator {

    fun fetchArticles(appSyncClient: AWSAppSyncClient) {
        GlobalScope.launch {
            val articles = ArticleClient.listArticles(appSyncClient)
            Dispatcher.dispatch(PickupAction.RefreshPickups(articles))
        }
    }

    fun fetchFoods() {
        val foods = FoodClient.fetchFoods()
        Dispatcher.dispatch(FoodAction.RefreshFoods(foods))
    }

    fun fetchEvents() {
        val events = PickupClient.fetchPickups() //TODO
        Dispatcher.dispatch(EventAction.RefreshEvents(events))
    }

    fun fetchNews() {
        val news = PickupClient.fetchPickups() //TODO
        Dispatcher.dispatch(NewsAction.RefreshNews(news))
    }

    fun showDetail(appSyncClient: AWSAppSyncClient, articleId: String) {
        GlobalScope.launch {
            val article = DetailClient.getArticle(appSyncClient, articleId) ?: return
            Dispatcher.dispatch(DetailAction.ShowArticleDetail(article))
        }
    }

    fun saveArticle(
        appSyncClient: AWSAppSyncClient,
        transferUtility: TransferUtility,
        file: File,
        article: Article
    ) {
        ArticleClient.saveArticle(appSyncClient, transferUtility, file, article)
    }

}