package com.matsuda.chichibu.stores

import androidx.lifecycle.MutableLiveData
import com.matsuda.chichibu.actions.OwnerAction
import com.matsuda.chichibu.data.Article
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OwnerStore : Store() {
    var article: MutableLiveData<Article> = MutableLiveData()
    var childAreas: MutableLiveData<List<String>> = MutableLiveData()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: OwnerAction.ShowArticleDetail) {
        article.postValue(action.data)
        loading.postValue(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: OwnerAction.RefreshAreas) {
        childAreas.postValue(action.data)
    }
    // add receiving actions functions
}

