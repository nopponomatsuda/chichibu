package com.matsuda.chichibu.api

import android.util.Log
import com.amazonaws.amplify.generated.graphql.CreateFavoriteMutation
import com.amazonaws.amplify.generated.graphql.ListFavoritesQuery
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.ClearCacheOptions
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import type.CreateFavoriteInput
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object MyFavoriteClient {
    private const val TAG = "ArticleClient"

    suspend fun listFavorites(appSyncClient: AWSAppSyncClient): List<String> {
        return withContext(Dispatchers.Default) {
            list(appSyncClient)
        }
    }

    fun addFavorite(
        appSyncClient: AWSAppSyncClient,
        articleId: String,
        block: Boolean.() -> Unit
    ) {
        val mutationCallback = object : GraphQLCall.Callback<CreateFavoriteMutation.Data>() {
            override fun onResponse(response: com.apollographql.apollo.api.Response<CreateFavoriteMutation.Data>) {
                Log.d(TAG, "CreateCommentMutation : ${response.data().toString()}")
                block(true)
            }

            override fun onFailure(e: ApolloException) {
                Log.e(TAG, "Error : $e")
                block(false)
            }
        }

        val createCommentMutation =
            CreateFavoriteMutation.builder()
                .input(
                    CreateFavoriteInput.builder()
                        .userId(AWSMobileClient.getInstance().identityId)
                        .articleId(articleId)
                        .build()
                )
                .build()
        appSyncClient.mutate(createCommentMutation)
            .enqueue(mutationCallback)
    }

    private suspend fun list(appSyncClient: AWSAppSyncClient): List<String> {
        return suspendCoroutine { continuation ->
            val queueCall = appSyncClient.query(ListFavoritesQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)

            val listCallback = object : GraphQLCall.Callback<ListFavoritesQuery.Data>() {
                override fun onResponse(response: com.apollographql.apollo.api.Response<ListFavoritesQuery.Data>) {
                    Log.d(TAG, "onResponse() data : ${response.data()}")
                    val data = response.data() ?: return

                    //TODO update to best practice
                    val list = mutableListOf<String>()
                    data.listFavorites()?.items()?.forEach {
                        it?.articleId()?.run { list.add(this) }
                    }
                    continuation.resume(list)
                    queueCall.cancel()
                }

                override fun onFailure(e: ApolloException) {
                    Log.e(TAG, e.toString())
                    continuation.resume(mutableListOf())
                    queueCall.cancel()
                }
            }
            queueCall.enqueue(listCallback)
        }
    }
}