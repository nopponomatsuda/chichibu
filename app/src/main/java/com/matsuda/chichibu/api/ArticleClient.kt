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
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.data.Articles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import type.CreateArticleInput
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ArticleClient {
    private const val TAG = "ArticleClient"

    //TODO add pass category as arg
    suspend fun listArticles(appSyncClient: AWSAppSyncClient): Articles {
        return withContext(Dispatchers.Default) {
            list(appSyncClient)
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
        block: Boolean.() -> Unit
    ) {
        val mutationCallback = object : GraphQLCall.Callback<CreateArticleMutation.Data>() {
            override fun onResponse(response: com.apollographql.apollo.api.Response<CreateArticleMutation.Data>) {
                Log.d("CreateCommentMutation", response.data().toString())
                block(true)
            }

            override fun onFailure(e: ApolloException) {
                Log.e("Error", e.toString())
                block(false)
            }
        }
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
        appSyncClient.mutate(createCommentMutation)
            .enqueue(mutationCallback)
    }

    private suspend fun list(appSyncClient: AWSAppSyncClient): Articles {
        return suspendCoroutine { continuation ->
            val queueCall = appSyncClient.query(ListArticlesQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)

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
                                mainImageUrl = it.mainImageUrl()
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
}