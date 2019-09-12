package com.matsuda.chichibu.stores.mypage

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.matsuda.chichibu.actions.MyPageAction
import com.matsuda.chichibu.stores.Store
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MypagePickupStore : Store() {
    val list: ObservableList<BaseObservable> = ObservableArrayList()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: MyPageAction.RefreshPickups) {
        list.clear()
        list.addAll(action.data.articleList)
        loading.postValue(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: MyPageAction.AddPickupFavorite) {
        list.add(action.data)
        loading.postValue(false)
    }
    // add receiving actions functions
}

