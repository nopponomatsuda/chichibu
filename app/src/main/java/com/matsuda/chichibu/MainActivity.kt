package com.matsuda.chichibu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider
import com.matsuda.chichibu.actions.ArticleActionCreator
import com.matsuda.chichibu.actions.MyPageActionCreator
import com.matsuda.chichibu.view.create.CreateArticleActivity
import com.matsuda.chichibu.view.navigator.ViewNavigator
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        var aWSAppSyncClient: AWSAppSyncClient? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AWSMobileClient.getInstance().initialize(this, object : Callback<UserStateDetails> {
            override fun onResult(userStateDetails: UserStateDetails) {
                when (userStateDetails.userState) {
                    UserState.SIGNED_IN -> {
                        runOnUiThread { setUpView() }
                    }
                    else -> moveToLoginPage()
                }
            }

            override fun onError(e: Exception) {
                Log.e(TAG, "Error during initialization", e)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        aWSAppSyncClient = AWSAppSyncClient.builder()
            .context(this)
            .awsConfiguration(AWSConfiguration(this))
            .cognitoUserPoolsAuthProvider(CognitoUserPoolsAuthProvider {
                try {
                    return@CognitoUserPoolsAuthProvider AWSMobileClient.getInstance()
                        .tokens.idToken.tokenString
                } catch (e: Exception) {
                    moveToLoginPage()
                    return@CognitoUserPoolsAuthProvider e.localizedMessage
                }
            }).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        aWSAppSyncClient?.clearCaches()
        aWSAppSyncClient = null
        //TODO handle in MyPageViewPagerFragment
        MyPageActionCreator.clearFavoriteCache()
        ArticleActionCreator.clearArticleCache()
    }

    private fun setUpView() {
        setContentView(R.layout.activity_main)
        ViewNavigator.moveToHome(supportFragmentManager)

        val navView = nav_view as BottomNavigationView
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_favorite ->
                    ViewNavigator.moveToMyPage(supportFragmentManager)
                R.id.navigation_login -> {
                    moveToLoginPage()
                }
                R.id.navigation_search -> {
                    ViewNavigator.moveToAreaPage(supportFragmentManager)
                }
                else ->
                    ViewNavigator.moveToHome(supportFragmentManager)
            }
            false
        }

        fab.setOnClickListener {
            startActivity(Intent(this, CreateArticleActivity::class.java))
        }
    }

    private fun moveToLoginPage() {
        startActivity(Intent(this, AuthenticatorActivity::class.java))
    }
}
