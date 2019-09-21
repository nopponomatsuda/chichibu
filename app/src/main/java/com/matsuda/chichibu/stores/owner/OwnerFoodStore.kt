package com.matsuda.chichibu.stores.owner

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.matsuda.chichibu.actions.OwnerAction
import com.matsuda.chichibu.stores.Store
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OwnerFoodStore : Store() {
    val list: ObservableList<BaseObservable> = ObservableArrayList()
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: OwnerAction.RefreshFoods) {
        list.clear()
        list.addAll(action.data.articleList)
        loading.postValue(false)
    }

    // add receiving actions functions
}

