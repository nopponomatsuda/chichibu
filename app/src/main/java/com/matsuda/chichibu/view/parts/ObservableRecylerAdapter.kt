package com.matsuda.chichibu.view.parts

import android.content.Context
import androidx.databinding.BaseObservable
import android.view.LayoutInflater
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView

abstract class ObservableRecyclerAdapter<T>(
    context: Context,
    recyclerItems: ObservableList<BaseObservable>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: MutableList<BaseObservable> = mutableListOf()
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    init {
        items.addAll(recyclerItems.toList())

        recyclerItems.addOnListChangedCallback(object :
            ObservableList.OnListChangedCallback<ObservableList<BaseObservable>>() {
            override fun onChanged(sender: ObservableList<BaseObservable>?) {
                sender?.let {
                    items.clear()
                    items.addAll(it.toList())
                }
                notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<BaseObservable>?,
                positionStart: Int,
                itemCount: Int
            ) {
                for (index in 0 until (itemCount - 1)) {
                    items.removeAt(positionStart)
                }
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<BaseObservable>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                val moveItems = items.subList(fromPosition, fromPosition + (itemCount - 1))
                moveItems.map { items.remove(it) }
                moveItems.forEachIndexed { index, customRecyclerItem ->
                    items.add(toPosition + index, customRecyclerItem)
                }
                notifyDataSetChanged()
            }

            override fun onItemRangeInserted(
                sender: ObservableList<BaseObservable>?,
                positionStart: Int,
                itemCount: Int
            ) {
                val insertItems = sender?.toList() ?: return
                items.addAll(positionStart, insertItems.subList(positionStart, positionStart + itemCount))
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<BaseObservable>?,
                positionStart: Int,
                itemCount: Int
            ) {
                val changedItems = sender?.toList() ?: return
                changedItems.subList(positionStart, positionStart + itemCount)
                    .forEachIndexed { index, customRecyclerItem ->
                        items[positionStart + index] = customRecyclerItem
                    }
                notifyItemRangeChanged(positionStart, itemCount)
            }
        })
    }

    override fun getItemCount(): Int = items.size
}