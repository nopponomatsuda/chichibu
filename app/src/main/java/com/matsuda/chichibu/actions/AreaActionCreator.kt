package com.matsuda.chichibu.actions

import com.matsuda.chichibu.data.Area
import com.matsuda.chichibu.data.Prefecture
import com.matsuda.chichibu.dispatchers.Dispatcher

object AreaActionCreator {
    fun fetchAreas(areaId: String?) {
        if (areaId == null) {
            fetchParent()
        } else {
            fetchChildAreas(areaId)
        }
    }

    private fun fetchParent() {
        //TODO get article count from DB
        val list = Prefecture.values().map {
            Area(it.areaId.toString(), it.name, 0, false)
        }
        Dispatcher.dispatch(AreaAction.RefreshAreas(list))
    }

    private fun fetchChildAreas(areaId: String) {
        //TODO get article count from DB
        val list = Prefecture.values().map {
            Area(it.areaId.toString(), it.name, 0, false)
        }
        Dispatcher.dispatch(AreaAction.RefreshAreas(list))
    }
}