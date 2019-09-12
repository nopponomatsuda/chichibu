package com.matsuda.chichibu

import android.os.Bundle
import android.app.Activity
import android.util.Log
import android.content.Intent
import com.amazonaws.mobile.client.*

class AuthenticatorActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticator)

        AWSMobileClient.getInstance().initialize(this, object : Callback<UserStateDetails> {
            override fun onResult(userStateDetails: UserStateDetails) {
                when (userStateDetails.userState) {
                    UserState.SIGNED_IN -> signInSuccess()
                    UserState.SIGNED_OUT -> showSignIn()
                    else -> AWSMobileClient.getInstance().signOut()
                }
            }

            override fun onError(e: Exception) {
                Log.e("initialize", "Error during initialization", e)
            }
        })
    }

    private fun signInSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showSignIn() {
        AWSMobileClient.getInstance().showSignIn(
            this@AuthenticatorActivity,
            SignInUIOptions.builder()
                .nextActivity(MainActivity::class.java)
                .build(),
            object : Callback<UserStateDetails> {
                override fun onResult(result: UserStateDetails) {
                    Log.d("showSignIn", result.userState.name)
                }

                override fun onError(e: Exception) {
                }
            }
        )
    }
}