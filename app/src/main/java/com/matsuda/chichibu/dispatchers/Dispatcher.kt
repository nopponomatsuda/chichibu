package com.matsuda.chichibu.dispatchers

import com.matsuda.chichibu.actions.Action
import org.greenrobot.eventbus.EventBus

object  Dispatcher {

    fun<T> dispatch(action : Action<T>) {
        EventBus.getDefault().post(action)
    }

    fun register(observer : Any) {
        EventBus.getDefault().register(observer)
    }

    fun unregister(observer: Any) {
        EventBus.getDefault().unregister(observer)
    }
}