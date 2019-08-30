package com.matsuda.chichibu

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.amazonaws.amplify.generated.graphql.CreateChichibuMutation
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import type.CreateChichibuInput
import java.lang.Exception

class CreateArticleActivity : Activity() {
    private var aWSAppSyncClient: AWSAppSyncClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_article)

        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i("INIT", "onResult: " + userStateDetails.userState)
                    init()
                }

                override fun onError(e: Exception) {
                    Log.e("INIT", "Initialization error.", e)
                }
            })
    }

    fun init() {
        aWSAppSyncClient = AWSAppSyncClient.builder()
            .context(this.applicationContext)
            .awsConfiguration(AWSConfiguration(this.applicationContext))
            .cognitoUserPoolsAuthProvider(CognitoUserPoolsAuthProvider {
                try {
                    return@CognitoUserPoolsAuthProvider AWSMobileClient.getInstance()
                        .tokens.idToken.tokenString
                } catch (e: Exception) {
                    Log.e("APPSYNC_ERROR", e.localizedMessage)
                    return@CognitoUserPoolsAuthProvider e.localizedMessage
                }
            }).build()
        runMutation()
    }


    private fun runMutation() {
        val client = aWSAppSyncClient ?: return
        val createCommentMutation =
            CreateChichibuMutation.builder()
                .input(CreateChichibuInput.builder().title("title2").text("text2").build())
                .build()
        client.mutate(createCommentMutation)
            .enqueue(mutationCallback)
    }

    private val mutationCallback = object : GraphQLCall.Callback<CreateChichibuMutation.Data>() {
        override fun onResponse(response: com.apollographql.apollo.api.Response<CreateChichibuMutation.Data>) {
            Log.d("CreateCommentMutation", response.data().toString())
        }

        override fun onFailure(e: ApolloException) {
            Log.e("Error", e.toString())
        }
    }
}