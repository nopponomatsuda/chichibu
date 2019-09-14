package com.matsuda.chichibu.actions

import com.matsuda.chichibu.data.Area
import com.matsuda.chichibu.data.Prefecture
import com.matsuda.chichibu.dispatchers.Dispatcher

object AreaActionCreator {
    fun fetchAreas() {
        //TODO get article count from DB
        val list = Prefecture.values().map { Area(it.name, 0) }
        Dispatcher.dispatch(AreaAction.RefreshAreas(list))
    }
}