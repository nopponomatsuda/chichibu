package com.matsuda.chichibu.stores

import androidx.lifecycle.MutableLiveData
import com.matsuda.chichibu.actions.ProfileAction
import com.matsuda.chichibu.data.Profile
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProfileStore : Store() {
    var profile: MutableLiveData<Profile> = MutableLiveData()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: ProfileAction.RefreshProfile) {
        profile.postValue(action.data)
        loading.postValue(false)
    }
    // add receiving actions functions
}

