package com.matsuda.chichibu.api

import android.util.Log
import com.amazonaws.amplify.generated.graphql.GetArticleQuery
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.data.Articles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DetailClient {
    suspend fun getArticle(appSyncClient: AWSAppSyncClient, articleId: String): Article? {
        return withContext(Dispatchers.Default) {
            query(appSyncClient, articleId)
        }
    }

    private suspend fun query(appSyncClient: AWSAppSyncClient, articleId: String): Article? {
        return suspendCoroutine { continuation ->
            val callback = object : GraphQLCall.Callback<GetArticleQuery.Data>() {
                override fun onResponse(response: Response<GetArticleQuery.Data>) {
                    Log.i("Results", response.data()?.article.toString())
                    val data = response.data()?.article ?: return
                    val article = Article(
                        id = data.id(),
                        title = data.title(),
                        subTitle = data.subTitle(),
                        mainImageUrl = data.mainImageUrl()
                    )
                    continuation.resume(article)
                }

                override fun onFailure(e: ApolloException) {
                    Log.e("ERROR", e.toString())
                    continuation.resume(null)
                }
            }
            appSyncClient.query(GetArticleQuery.builder().id(articleId).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(callback)
        }
    }

}