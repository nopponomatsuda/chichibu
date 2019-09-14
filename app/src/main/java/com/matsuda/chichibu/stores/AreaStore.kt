package com.matsuda.chichibu.stores

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.matsuda.chichibu.actions.AreaAction
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AreaStore : Store() {
    val list: ObservableList<BaseObservable> = ObservableArrayList()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: AreaAction.RefreshAreas) {
        list.clear()
        list.addAll(action.data)
        loading.postValue(false)
    }
    // add receiving actions functions
}

