package com.matsuda.chichibu.actions

import com.matsuda.chichibu.api.DetailClient
import com.matsuda.chichibu.api.PickupClient
import com.matsuda.chichibu.dispatchers.Dispatcher

object ActionsCreator {
    fun fetchPickups() {
        val pickups = PickupClient.fetchPickups()
        Dispatcher.dispatch(PickupAction.RefreshArticles(pickups))
    }

    fun showDetail(articleId : Int) {
        val article = DetailClient.getDetail(articleId)
        Dispatcher.dispatch(DetailAction.ShowArticleDetail(article))
    }
}