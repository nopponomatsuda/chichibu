package com.matsuda.chichibu.api

import android.util.Log
import com.amazonaws.amplify.generated.graphql.CreateArticleMutation
import com.amazonaws.amplify.generated.graphql.GetArticleQuery
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.data.ArticleCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DetailClient {
    private const val TAG = "DetailClient"
    suspend fun getArticle(appSyncClient: AWSAppSyncClient, articleId: String): Article? {
        return withContext(Dispatchers.Default) {
            query(appSyncClient, articleId)
        }
    }

    private suspend fun query(appSyncClient: AWSAppSyncClient, articleId: String): Article? {
        return suspendCoroutine { continuation ->
            val queueCall = appSyncClient.query(GetArticleQuery.builder().id(articleId).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)

            val callback = object : GraphQLCall.Callback<GetArticleQuery.Data>() {
                override fun onResponse(response: Response<GetArticleQuery.Data>) {
                    Log.i(TAG, "onResponse : ${response.data()?.article.toString()}")
                    val data = response.data()?.article ?: return
                    continuation.resume(mapDataToArticle(data))
                    queueCall.cancel()
                }

                override fun onFailure(e: ApolloException) {
                    Log.e(TAG, "onFailure $e")
                    continuation.resume(null)
                    queueCall.cancel()
                }
            }
            queueCall.enqueue(callback)
        }
    }

    //TODO move mapping class
    private fun mapDataToArticle(data: GetArticleQuery.GetArticle): Article {
        data.apply {
            return Article(
                id = id(),
                title = title(),
                subTitle = subTitle(),
                text = text(),
                mainImageUrl = mainImageUrl(),
                category = category()?.run { ArticleCategory.valueOf(this) }
                    ?: ArticleCategory.PICKUP
            )
        }
    }
}