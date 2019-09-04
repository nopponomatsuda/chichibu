package com.matsuda.chichibu.stores

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.matsuda.chichibu.actions.ArticleAction
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FoodStore : Store() {
    val list: ObservableList<BaseObservable> = ObservableArrayList()
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: ArticleAction.RefreshFoods) {
        Log.d("FoodStore", "on")
        list.clear()
        list.addAll(action.data.articleList)
    }
    // add receiving actions functions
}

