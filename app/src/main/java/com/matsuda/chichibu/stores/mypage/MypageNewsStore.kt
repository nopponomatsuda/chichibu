package com.matsuda.chichibu.stores.mypage

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.matsuda.chichibu.actions.MyPageAction
import com.matsuda.chichibu.stores.Store
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MypageNewsStore : Store() {
    val list: ObservableList<BaseObservable> = ObservableArrayList()
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: MyPageAction.RefreshNews) {
        Log.d("NewsStore", "on")
        list.clear()
        list.addAll(action.data.articleList)
        loading.postValue(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: MyPageAction.AddNewsFavorite) {
        list.add(action.data)
        loading.postValue(false)
    }
    // add receiving actions functions
}

