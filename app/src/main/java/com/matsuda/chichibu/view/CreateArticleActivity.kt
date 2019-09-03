package com.matsuda.chichibu.view

import android.os.Bundle
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import com.matsuda.chichibu.R
import com.matsuda.chichibu.actions.MyPageActionCreator
import kotlinx.android.synthetic.main.activity_create_article.*
import java.lang.Exception
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.matsuda.chichibu.common.GlideApp
import com.matsuda.chichibu.common.util.FileUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.matsuda.chichibu.actions.ActionsCreator
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.databinding.ActivityCreateArticleBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.DetailStore

class CreateArticleActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_PICK_UP_IMAGE = 1
    }

    private val detailStore: DetailStore by lazy {
        ViewModelProviders.of(this).get(DetailStore::class.java)
    }

    private var binding: ActivityCreateArticleBinding? = null
    private var aWSAppSyncClient: AWSAppSyncClient? = null
    private var transferUtility: TransferUtility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_create_article)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_article)

        Dispatcher.register(detailStore)
        detailStore.article.postValue(Article())

        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i("INIT", "onResult: " + userStateDetails.userState)
                    prepareAwsAppSync()
                }

                override fun onError(e: Exception) {
                    Log.e("INIT", "Initialization error.", e)
                }
            })

        binding?.run {
            viewModel = detailStore
            lifecycleOwner = this@CreateArticleActivity
        }

        select_button.setOnClickListener {
            startActivityForResult(Intent().apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                action = Intent.ACTION_OPEN_DOCUMENT
            }, REQUEST_PICK_UP_IMAGE)
        }

        upload_button.setOnClickListener {
            val transferUtility = transferUtility ?: return@setOnClickListener
            val article = detailStore.article.value ?: return@setOnClickListener
            val appSyncClient = aWSAppSyncClient ?: return@setOnClickListener

            val bitmap = upload_image.drawable.toBitmap()
            val uploadFile = FileUtils.createUploadFile(this, bitmap)
            ActionsCreator.saveArticle(appSyncClient, transferUtility, uploadFile, article)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Dispatcher.unregister(detailStore)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        resultData: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == REQUEST_PICK_UP_IMAGE && resultCode == RESULT_OK) {
            val uri = resultData?.data ?: return
            GlideApp.with(this).load(uri).into(upload_image)
        }
    }

    private fun prepareAwsAppSync() {
        aWSAppSyncClient = AWSAppSyncClient.builder()
            .context(this.applicationContext)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .cognitoUserPoolsAuthProvider(CognitoUserPoolsAuthProvider {
                try {
                    return@CognitoUserPoolsAuthProvider AWSMobileClient.getInstance()
                        .tokens.idToken.tokenString
                } catch (e: Exception) {
                    Log.e("APPSYNC_ERROR", e.localizedMessage)
                    return@CognitoUserPoolsAuthProvider e.localizedMessage
                }
            }).build()

        transferUtility = TransferUtility.builder()
            .context(this)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(
                AmazonS3Client(
                    AWSMobileClient.getInstance(),
                    Region.getRegion("us-east-1")
                )
            ) //TODO
            .build()
    }
}