package com.matsuda.chichibu.api

import android.util.Log
import com.amazonaws.amplify.generated.graphql.CreateArticleMutation
import com.amazonaws.amplify.generated.graphql.ListArticlesQuery
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import com.matsuda.chichibu.common.ArticleCategory
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.data.Articles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import type.CreateArticleInput
import type.ModelArticleFilterInput
import type.ModelStringFilterInput
import java.io.File
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ArticleClient {
    private const val TAG = "ArticleClient"
    private var cacheListMap = mutableMapOf<ArticleCategory, Articles>()

    suspend fun listArticles(
        appSyncClient: AWSAppSyncClient,
        category: ArticleCategory
    ): Articles {
        if (cacheListMap[category] != null) return cacheListMap[category]!!
        cacheListMap[category] = Articles(mutableListOf())

        return withContext(Dispatchers.Default) {
            val articles = list(appSyncClient, category)
            cacheListMap[category] = articles
            articles
        }
    }

    fun uploadFile(
        transferUtility: TransferUtility,
        file: File,
        key: String,
        block: Boolean.() -> Unit
    ) {
        val uploadObserver = transferUtility.upload(key, file)
        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED === state) {
                    Log.i(TAG, "upload completed")
                    file.delete()
                    block(true)
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDonef.toInt()
                Log.d(
                    TAG, "ID: $id  bytesCurrent: $bytesCurrent"
                            + " bytesTotal: $bytesTotal $percentDone"
                )
            }

            override fun onError(id: Int, ex: Exception) {
                Log.e(TAG, "failed upload ${ex.message}")
                file.delete()
                block(false)
            }
        })
    }

    fun saveArticle(
        appSyncClient: AWSAppSyncClient,
        article: Article,
        category: ArticleCategory,
        block: Boolean.() -> Unit
    ) {
        val createCommentMutation =
            CreateArticleMutation.builder()
                .input(
                    CreateArticleInput.builder()
                        .title(article.title)
                        .subTitle(article.subTitle)
                        .text(article.text)
                        .mainImageUrl(article.mainImageUrl)
                        .category(article.category.name)
                        .build()
                )
                .build()
        val mutateCall = appSyncClient.mutate(createCommentMutation)
        val mutationCallback = object : GraphQLCall.Callback<CreateArticleMutation.Data>() {
            override fun onResponse(response: com.apollographql.apollo.api.Response<CreateArticleMutation.Data>) {
                Log.d("CreateCommentMutation", response.data().toString())
                cacheListMap[category]!!.articleList.add(article)
                block(true)
                mutateCall.cancel()
            }

            override fun onFailure(e: ApolloException) {
                Log.e("Error", e.toString())
                block(false)
                mutateCall.cancel()
            }
        }
        mutateCall.enqueue(mutationCallback)
    }

    private suspend fun list(
        appSyncClient: AWSAppSyncClient,
        category: ArticleCategory
    ): Articles {
        return suspendCoroutine { continuation ->
            val queueCall = appSyncClient.query(
                ListArticlesQuery.builder().filter(
                    ModelArticleFilterInput.builder().category(
                        ModelStringFilterInput.builder().eq(
                            category.name
                        ).build()
                    ).build()
                )
                    .limit(20)
                    .build()
            ).responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)

            val listCallback = object : GraphQLCall.Callback<ListArticlesQuery.Data>() {
                override fun onResponse(response: com.apollographql.apollo.api.Response<ListArticlesQuery.Data>) {
                    Log.d(TAG, "onResponse() data : ${response.data()}")
                    val data = response.data() ?: return

                    //TODO update to best practice
                    val list = mutableListOf<Article>()
                    data.listArticles()?.items()?.forEach {
                        list.add(
                            (Article(
                                id = it.id(),
                                title = it.title(),
                                subTitle = it.subTitle(),
                                text = it.text(),
                                mainImageUrl = it.mainImageUrl(),
                                category = it.category()?.run { ArticleCategory.valueOf(this) }
                                    ?: ArticleCategory.PICKUP
                            ))
                        )
                    }
                    continuation.resume(Articles(list))
                    queueCall.cancel()
                }

                override fun onFailure(e: ApolloException) {
                    Log.e(TAG, e.toString())
                    continuation.resume(Articles(mutableListOf()))
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