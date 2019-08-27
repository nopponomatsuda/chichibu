package com.matsuda.chichibu.stores

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.matsuda.chichibu.actions.FoodAction
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FoodStore : Store() {
    val foodList: ObservableList<BaseObservable> = ObservableArrayList()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: FoodAction.RefreshFoods) {
        foodList.clear()
        foodList.addAll(action.data.articleList)
    }
    // add receiving actions functions
}

