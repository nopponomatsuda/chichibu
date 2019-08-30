package com.matsuda.chichibu

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.matsuda.chichibu.view.navigator.ViewNavigator
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewNavigator.moveToHome(supportFragmentManager)

        val navView = nav_view as BottomNavigationView
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_dashboard ->
                    ViewNavigator.moveToMyPage(supportFragmentManager)
                R.id.navigation_login -> {
//                    ViewNavigator.moveToLogin(supportFragmentManager)
                    startActivity(Intent(this, AuthenticatorActivity::class.java))
                }
                else ->
                    ViewNavigator.moveToHome(supportFragmentManager)
            }
            false
        }

        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i("INIT", "onResult: " + userStateDetails.userState)
                }

                override fun onError(e: Exception) {
                    Log.e("INIT", "Initialization error.", e)
                }
            })
    }
}
