package com.matsuda.chichibu.actions

import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.matsuda.chichibu.data.Profile
import com.matsuda.chichibu.dispatchers.Dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ProfileActionCreator {
    fun fetchAreas() {
        //TODO get info count from DB
        GlobalScope.launch {
            AWSMobileClient.getInstance().run {
                val profile = Profile(username, "TestUrl")
                Dispatcher.dispatch(ProfileAction.RefreshProfile(profile))
            }
        }
    }
}