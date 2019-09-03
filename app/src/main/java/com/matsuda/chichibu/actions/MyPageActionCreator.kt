package com.matsuda.chichibu.actions

import android.util.Log
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.matsuda.chichibu.api.ArticleClient
import com.matsuda.chichibu.api.FoodClient
import com.matsuda.chichibu.api.MyFavoriteClient
import com.matsuda.chichibu.api.PickupClient
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.dispatchers.Dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

object MyPageActionCreator {
    private const val TAG = "MyPageActionCreator"
    fun fetchArticles(appSyncClient: AWSAppSyncClient) {
        Log.d(TAG, "fetchArticles")
        GlobalScope.launch {
            val articles = ArticleClient.listArticles(appSyncClient)
            Dispatcher.dispatch(PickupAction.RefreshPickups(articles))
        }
    }

    fun fetchFoods(appSyncClient: AWSAppSyncClient) {
        val foods = FoodClient.fetchFoods()
        Dispatcher.dispatch(FoodAction.RefreshFoods(foods))
    }

    fun fetchEvents(appSyncClient: AWSAppSyncClient) {
        val events = PickupClient.fetchPickups() //TODO
        Dispatcher.dispatch(EventAction.RefreshEvents(events))
    }

    fun fetchNews(appSyncClient: AWSAppSyncClient) {
        val news = PickupClient.fetchPickups() //TODO
        Dispatcher.dispatch(NewsAction.RefreshNews(news))
    }

    fun addFavorite(
        appSyncClient: AWSAppSyncClient,
        articleId: String
    ) {
        MyFavoriteClient.addFavorite(appSyncClient, articleId)
    }

}