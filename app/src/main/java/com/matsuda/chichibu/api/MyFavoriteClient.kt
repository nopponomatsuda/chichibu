package com.matsuda.chichibu.api

import android.util.Log
import com.amazonaws.amplify.generated.graphql.CreateFavoriteMutation
import com.amazonaws.amplify.generated.graphql.ListFavoritesQuery
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import com.matsuda.chichibu.data.ArticleCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import type.CreateFavoriteInput
import type.ModelFavoriteFilterInput
import type.ModelStringFilterInput
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object MyFavoriteClient {
    private const val TAG = "MyFavoriteClient"
    private var cacheListMap = mutableMapOf<ArticleCategory, MutableList<String>>()

    suspend fun listFavorites(
        appSyncClient: AWSAppSyncClient,
        category: ArticleCategory
    ): List<String> {
        if (cacheListMap[category] != null) return cacheListMap[category]!!
        cacheListMap[category] = mutableListOf()

        return withContext(Dispatchers.Default) {
            val list = list(appSyncClient, category)
            cacheListMap[category]!!.addAll(list)
            list
        }
    }

    fun addFavorite(
        appSyncClient: AWSAppSyncClient,
        articleId: String,
        category: ArticleCategory,
        block: Boolean.() -> Unit
    ) {
        val mutationCallback = object : GraphQLCall.Callback<CreateFavoriteMutation.Data>() {
            override fun onResponse(response: com.apollographql.apollo.api.Response<CreateFavoriteMutation.Data>) {
                Log.d(TAG, "CreateCommentMutation : ${response.data().toString()}")
                block(true)
                if (cacheListMap[category] == null) cacheListMap[category] = mutableListOf()
                cacheListMap[category]!!.add(articleId)
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
                        .category(category.name)
                        .build()
                )
                .build()
        appSyncClient.mutate(createCommentMutation)
            .enqueue(mutationCallback)
    }

    private suspend fun list(
        appSyncClient: AWSAppSyncClient,
        category: ArticleCategory
    ): List<String> {
        return suspendCoroutine { continuation ->
            val queueCall = appSyncClient.query(
                ListFavoritesQuery.builder()
                    .filter(
                        ModelFavoriteFilterInput.builder()
                            .userId(
                                ModelStringFilterInput.builder().eq(
                                    AWSMobileClient.getInstance().identityId
                                ).build()
                            ).category(
                                ModelStringFilterInput.builder().eq(
                                    category.name
                                ).build()
                            ).build()
                    ).limit(20)
                    .build()
            ).responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)

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

    fun clearCache() {
        cacheListMap.clear()
    }
}