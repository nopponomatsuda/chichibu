package com.matsuda.chichibu.stores.mypage

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.matsuda.chichibu.actions.MyPageAction
import com.matsuda.chichibu.stores.Store
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MypageEventStore : Store() {
    val list: ObservableList<BaseObservable> = ObservableArrayList()
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: MyPageAction.RefreshEvents) {
        Log.d("EventStore", "on")
        list.clear()
        list.addAll(action.data.articleList)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: MyPageAction.AddEventsFavorite) {
        list.add(action.data)
    }
    // add receiving actions functions
}

