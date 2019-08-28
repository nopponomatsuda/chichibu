package com.matsuda.chichibu.view.parts

import android.content.Context
import androidx.databinding.BaseObservable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.matsuda.chichibu.view.parts.ObservableRecyclerAdapter

class MasonryAdapter(
    context: Context,
    private val recyclerItems: ObservableList<BaseObservable>,
    private val itemViewResourceId: Int,
    private val variableId: Int
) :
    ObservableRecyclerAdapter<MasonryAdapter.MasonryViewBindingHolder>
        (context, recyclerItems) {

    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasonryViewBindingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewDataBinding: ViewDataBinding =
            DataBindingUtil.inflate(inflater, itemViewResourceId, parent, false)
        return MasonryViewBindingHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MasonryViewBindingHolder

        val itemData = recyclerItems[position]
        holder.binding.apply {
            setVariable(variableId, itemData)
            notifyChange()
            executePendingBindings()
        }
        holder.binding.root.setOnClickListener {
            listener?.onClick(it, itemData)
        }
    }

    override fun getItemCount(): Int = recyclerItems.size

    class MasonryViewBindingHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onClick(view: View, data: BaseObservable)
    }
}