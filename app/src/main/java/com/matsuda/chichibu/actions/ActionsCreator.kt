package com.matsuda.chichibu.actions

import com.matsuda.chichibu.api.DetailClient
import com.matsuda.chichibu.api.FoodClient
import com.matsuda.chichibu.api.PickupClient
import com.matsuda.chichibu.dispatchers.Dispatcher

object ActionsCreator {
    fun fetchPickups() {
        val pickups = PickupClient.fetchPickups()
        Dispatcher.dispatch(PickupAction.RefreshArticles(pickups))
    }

    fun fetchFoods() {
        //TODO
        val foods = FoodClient.fetchFoods()
        Dispatcher.dispatch(FoodAction.RefreshFoods(foods))
    }

    fun showDetail(articleId: Int) {
        val article = DetailClient.getDetail(articleId)
        Dispatcher.dispatch(DetailAction.ShowArticleDetail(article))
    }
}