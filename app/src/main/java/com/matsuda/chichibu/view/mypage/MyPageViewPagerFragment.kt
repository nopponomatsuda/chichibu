package com.matsuda.chichibu.view.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider
import com.matsuda.chichibu.R
import kotlinx.android.synthetic.main.view_pager_fragment.*
import java.lang.Exception

class MyPageViewPagerFragment : Fragment() {
    var aWSAppSyncClient: AWSAppSyncClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_pager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context = context ?: return

        aWSAppSyncClient = AWSAppSyncClient.builder()
            .context(context)
            .awsConfiguration(AWSConfiguration(context))
            .cognitoUserPoolsAuthProvider(CognitoUserPoolsAuthProvider {
                try {
                    return@CognitoUserPoolsAuthProvider AWSMobileClient.getInstance()
                        .tokens.idToken.tokenString
                } catch (e: Exception) {
                    return@CognitoUserPoolsAuthProvider e.localizedMessage
                }
            }).build()

        val fragmentManager = fragmentManager ?: return
        viewPager.adapter = MyPageTabAdapter(context, fragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }
}