package com.matsuda.chichibu.stores

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.matsuda.chichibu.actions.ArticleAction
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PickupStore : Store() {
    val list: ObservableList<BaseObservable> = ObservableArrayList()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: ArticleAction.RefreshPickups) {
        Log.d("PickupStore", "on")
        list.clear()
        list.addAll(action.data.articleList)
        loading.postValue(false)
    }
    // add receiving actions functions
}

