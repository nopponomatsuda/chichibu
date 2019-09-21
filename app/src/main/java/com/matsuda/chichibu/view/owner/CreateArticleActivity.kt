package com.matsuda.chichibu.view.owner

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import com.matsuda.chichibu.R
import kotlinx.android.synthetic.main.activity_create_article.*
import java.lang.Exception
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.matsuda.chichibu.common.GlideApp
import com.matsuda.chichibu.common.FileUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.matsuda.chichibu.actions.ArticleActionCreator
import com.matsuda.chichibu.actions.OwnerActionCreator
import com.matsuda.chichibu.data.ArticleCategory
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.data.Prefecture
import com.matsuda.chichibu.databinding.ActivityCreateArticleBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.OwnerStore
import java.util.*

class CreateArticleActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_PICK_UP_IMAGE = 1
        const val EXTRA_KEY_ARTICLE_ID = "article_id"
    }

    private val ownerStore: OwnerStore by lazy {
        ViewModelProviders.of(this).get(OwnerStore::class.java)
    }

    private var binding: ActivityCreateArticleBinding? = null
    private var aWSAppSyncClient: AWSAppSyncClient? = null
    private var transferUtility: TransferUtility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_article)

        Dispatcher.register(ownerStore)

        binding?.run {
            viewModel = ownerStore
            lifecycleOwner = this@CreateArticleActivity
        }

        prepareAwsAppSync()

        //TODO set value on layout xml
        setupCategorySpinner()
        setupAreaSpinner()

        val articleId = intent.getStringExtra(EXTRA_KEY_ARTICLE_ID)
        if (articleId != null) {
            aWSAppSyncClient?.also { client ->
                OwnerActionCreator.showDetail(client, articleId)
            }
        } else {
            ownerStore.loading.postValue(false)
            ownerStore.article.postValue(Article())
        }

        select_button.setOnClickListener {
            startActivityForResult(
                Intent().apply {
                    type = "image/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    action = Intent.ACTION_OPEN_DOCUMENT
                },
                REQUEST_PICK_UP_IMAGE
            )
        }

        upload_button.setOnClickListener {
            val transferUtility = transferUtility ?: return@setOnClickListener
            val article = ownerStore.article.value ?: return@setOnClickListener
            val appSyncClient = aWSAppSyncClient ?: return@setOnClickListener
            val drawable = upload_image.drawable ?: return@setOnClickListener

            hideKeyBoard()
            ownerStore.loading.postValue(true)

            val bitmap = drawable.toBitmap()
            val uploadFile = FileUtils.createUploadFile(this, bitmap)

            val uniqueID = UUID.randomUUID().toString()
            val key = "public/$uniqueID.jpg"

            ArticleActionCreator.uploadFile(transferUtility, uploadFile, key) {
                if (!this) return@uploadFile

                val tuConfig =
                    AWSMobileClient.getInstance().configuration.optJsonObject("S3TransferUtility")
                val defaultBucket = tuConfig.getString("Bucket")
                article.mainImageUrl = "https://$defaultBucket.s3.amazonaws.com/$key"
                article.category = ArticleCategory.valueOf(category.selectedItem as String)

                ArticleActionCreator.saveArticle(appSyncClient, article) {
                    Handler(Looper.getMainLooper()).post {
                        ownerStore.loading.postValue(false)
                        finish()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Dispatcher.unregister(ownerStore)
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

    private fun setupCategorySpinner() {
        val categoryList: List<String> = ArticleCategory.values().map { it.name }
        category.run {
            adapter = ArrayAdapter<String>(
                this@CreateArticleActivity,
                android.R.layout.simple_spinner_item, categoryList
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }
    }

    private fun setupAreaSpinner() {
        //FIXME set viewModel
        val areaNameList: List<String> = Prefecture.values().map { it.name }
        area1.run {
            adapter = ArrayAdapter<String>(
                this@CreateArticleActivity,
                android.R.layout.simple_spinner_item, areaNameList
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }
    }

    private fun hideKeyBoard() {
        currentFocus?.run {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}