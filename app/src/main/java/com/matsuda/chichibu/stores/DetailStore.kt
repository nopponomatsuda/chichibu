package com.matsuda.chichibu.stores

import androidx.lifecycle.MutableLiveData
import com.matsuda.chichibu.actions.DetailAction
import com.matsuda.chichibu.common.ArticleCategory
import com.matsuda.chichibu.data.Article
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.widget.AdapterView
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class DetailStore : Store() {
    var article: MutableLiveData<Article> = MutableLiveData()
    var categoryList = ArticleCategory.values()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(action: DetailAction.ShowArticleDetail) {
        article.postValue(action.data)
    }

//    fun onItemSelectedNumber(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//        setNumber(numberList.get(position))
//        //Call Sip Register here
//    }
    // add receiving actions functions
}