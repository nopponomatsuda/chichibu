package com.matsuda.chichibu

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider
import com.matsuda.chichibu.view.CreateArticleActivity
import com.matsuda.chichibu.view.navigator.ViewNavigator
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    companion object {
        var aWSAppSyncClient: AWSAppSyncClient? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView = nav_view as BottomNavigationView
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_dashboard ->
                    ViewNavigator.moveToMyPage(supportFragmentManager)
                R.id.navigation_create_article ->
                    startActivity(Intent(this, CreateArticleActivity::class.java))
                R.id.navigation_login -> {
                    startActivity(Intent(this, AuthenticatorActivity::class.java))
                }
                else ->
                    ViewNavigator.moveToHome(supportFragmentManager)
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        if (aWSAppSyncClient == null) {
            aWSAppSyncClient = AWSAppSyncClient.builder()
                .context(this)
                .awsConfiguration(AWSConfiguration(this))
                .cognitoUserPoolsAuthProvider(CognitoUserPoolsAuthProvider {
                    try {
                        return@CognitoUserPoolsAuthProvider AWSMobileClient.getInstance()
                            .tokens.idToken.tokenString
                    } catch (e: Exception) {
                        return@CognitoUserPoolsAuthProvider e.localizedMessage
                    }
                }).build()
        }
        ViewNavigator.moveToHome(supportFragmentManager)
    }

    override fun onDestroy() {
        super.onDestroy()
        aWSAppSyncClient = null
    }
}
