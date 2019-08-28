package com.matsuda.chichibu.actions

import com.matsuda.chichibu.api.DetailClient
import com.matsuda.chichibu.api.FoodClient
import com.matsuda.chichibu.api.PickupClient
import com.matsuda.chichibu.dispatchers.Dispatcher

object ActionsCreator {

    fun fetchArticles() {
        val articles  = PickupClient.fetchPickups()
        Dispatcher.dispatch(PickupAction.RefreshPickups(articles))
    }

    fun fetchFoods() {
        val foods  = FoodClient.fetchFoods()
        Dispatcher.dispatch(FoodAction.RefreshFoods(foods))
    }

    fun fetchEvents() {
        val events  = PickupClient.fetchPickups() //TODO
        Dispatcher.dispatch(EventAction.RefreshEvents(events))
    }

    fun fetchNews() {
        val news  = PickupClient.fetchPickups() //TODO
        Dispatcher.dispatch(NewsAction.RefreshNews(news))
    }

    fun showDetail(articleId: Int) {
        val article = DetailClient.getDetail(articleId)
        Dispatcher.dispatch(DetailAction.ShowArticleDetail(article))
    }
}