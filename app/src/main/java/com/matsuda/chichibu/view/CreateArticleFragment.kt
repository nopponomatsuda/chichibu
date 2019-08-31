package com.matsuda.chichibu.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.amazonaws.amplify.generated.graphql.CreateArticleMutation
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import com.matsuda.chichibu.R
import com.matsuda.chichibu.actions.ActionsCreator
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.databinding.CreateArticleFragmentBinding
import type.CreateArticleInput
import java.lang.Exception

class CreateArticleFragment : Fragment() {
    private var binding: CreateArticleFragmentBinding? = null
    private var aWSAppSyncClient: AWSAppSyncClient? = null

    companion object {
        fun newInstance(): DetailFragment {
            return DetailFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AWSMobileClient.getInstance()
            .initialize(context, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i("INIT", "onResult: " + userStateDetails.userState)
                }

                override fun onError(e: Exception) {
                    Log.e("INIT", "Initialization error.", e)
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.create_article_fragment,
            container, false
        ) ?: return null

        binding?.run {
            lifecycleOwner = this@CreateArticleFragment
        }

        //TODO if budle value is null, show error or back to previous page
        val detailId = arguments?.getInt(Constant.BUNDLE_KEY_DETAIL_ID) ?: return null
        ActionsCreator.showDetail(detailId)

        binding?.root?.setOnTouchListener { _, _ ->
            //prevent bottom layer view from receiving above layer view's event
            true
        }

        return binding?.root
    }

    fun init() {
        aWSAppSyncClient = AWSAppSyncClient.builder()
            .context(context)
            .awsConfiguration(AWSConfiguration(context))
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
            CreateArticleMutation.builder()
                .input(CreateArticleInput.builder().title("title2").text("text2").build())
                .build()
        client.mutate(createCommentMutation)
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
}