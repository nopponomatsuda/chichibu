package com.matsuda.chichibu.stores

import androidx.lifecycle.MutableLiveData
import com.matsuda.chichibu.actions.DetailAction
import com.matsuda.chichibu.data.Article
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DetailStore : Store() {
    var article : MutableLiveData<Article> = MutableLiveData()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: DetailAction.ShowArticleDetail) {
        article.postValue(action.data)
    }
    // add receiving actions functions
}