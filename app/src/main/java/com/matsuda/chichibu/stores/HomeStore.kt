package com.matsuda.chichibu.stores

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.matsuda.chichibu.actions.PickupAction
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeStore : Store() {
    val pickUpList: ObservableList<BaseObservable> = ObservableArrayList()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: PickupAction.RefreshArticles) {
        pickUpList.clear()
        pickUpList.addAll(action.data.articleList)
    }
    // add receiving actions functions
}