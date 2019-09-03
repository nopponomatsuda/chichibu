package com.matsuda.chichibu.api

import android.util.Log
import com.amazonaws.amplify.generated.graphql.CreateArticleMutation
import com.amazonaws.amplify.generated.graphql.GetArticleQuery
import com.amazonaws.amplify.generated.graphql.ListArticlesQuery
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import com.matsuda.chichibu.actions.MyPageActionCreator
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

    fun saveArticle(
        appSyncClient: AWSAppSyncClient,
        transferUtility: TransferUtility,
        file: File,
        article: Article
    ) {
        uploadFile(appSyncClient, transferUtility, file, article)
    }

    suspend fun listArticles(appSyncClient: AWSAppSyncClient): Articles {
        return withContext(Dispatchers.Default) {
            list(appSyncClient)
        }
    }

    private fun uploadFile(
        appSyncClient: AWSAppSyncClient,
        transferUtility: TransferUtility,
        file: File,
        article: Article
    ) {
        val uniqueID = UUID.randomUUID().toString()
        val key = "public/$uniqueID.jpg"

        val tuConfig =
            AWSMobileClient.getInstance().configuration.optJsonObject("S3TransferUtility")
        val defaultBucket = tuConfig.getString("Bucket")
        article.mainImageUrl = "https://$defaultBucket.s3.amazonaws.com/$key"

        val uploadObserver = transferUtility.upload(key, file)
        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED === state) {
                    Log.i(TAG, "upload completed")
                    file.delete()
                    runMutation(appSyncClient, article)
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
            }

        })
    }

    private fun runMutation(appSyncClient: AWSAppSyncClient, article: Article) {
        val createCommentMutation =
            CreateArticleMutation.builder()
                .input(
                    CreateArticleInput.builder()
                        .title(article.title)
                        .subTitle(article.subTitle)
                        .text(article.text)
                        .mainImageUrl(article.mainImageUrl)
                        .build()
                )
                .build()
        appSyncClient.mutate(createCommentMutation)
            .enqueue(mutationCallback)
    }

    private val mutationCallback = object : GraphQLCall.Callback<CreateArticleMutation.Data>() {
        override fun onResponse(response: com.apollographql.apollo.api.Response<CreateArticleMutation.Data>) {
            Log.d("CreateCommentMutation", response.data().toString())
        }

        override fun onFailure(e: ApolloException) {
            Log.e("Error", e.toString())
        }
    }

    private suspend fun list(appSyncClient: AWSAppSyncClient): Articles {
        Log.d(TAG, "list")
        return suspendCoroutine { continuation ->
            Log.d(TAG, "list2")
            val queueClaa = appSyncClient.query(ListArticlesQuery.builder().build())
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
                    queueClaa.cancel()
                }

                override fun onFailure(e: ApolloException) {
                    Log.e(TAG, e.toString())
                    continuation.resume(Articles(mutableListOf()))
                    queueClaa.cancel()
                }
            }
            queueClaa.enqueue(listCallback)
        }
    }
}