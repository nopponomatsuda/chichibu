package com.matsuda.chichibu.actions

import android.util.Log
import com.amazonaws.amplify.generated.graphql.GetArticleQuery
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import com.matsuda.chichibu.api.FoodClient
import com.matsuda.chichibu.api.PickupClient
import com.matsuda.chichibu.dispatchers.Dispatcher

object MyPageActionCreator {
    fun fetchArticles(appSyncClient: AWSAppSyncClient) {
        val articles = PickupClient.fetchPickups()
        Dispatcher.dispatch(PickupAction.RefreshPickups(articles))
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

    private fun query(appSyncClient: AWSAppSyncClient) {
        appSyncClient.query(GetArticleQuery.builder().build())
            .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
            .enqueue(callback)
    }

    private val callback = object : GraphQLCall.Callback<GetArticleQuery.Data>() {
        override fun onResponse(response: com.apollographql.apollo.api.Response<GetArticleQuery.Data>) {
            Log.i("Results", response.data()?.article.toString())
        }

        override fun onFailure(e: ApolloException) {
            Log.e("ERROR", e.toString())
        }
    }
}