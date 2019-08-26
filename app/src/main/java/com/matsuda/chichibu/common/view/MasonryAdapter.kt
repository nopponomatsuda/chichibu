package com.matsuda.chichibu.common.view

import android.content.Context
import androidx.databinding.BaseObservable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.matsuda.chichibu.BR
import com.matsuda.chichibu.R

class MasonryAdapter(context: Context, private val recyclerItems: ObservableList<BaseObservable>) :
    ObservableRecyclerAdapter<MasonryAdapter.MasonryViewBindingHolder>
        (context, recyclerItems) {

    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasonryViewBindingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewDataBinding: ViewDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.list_item_view, parent, false)
        return MasonryViewBindingHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MasonryViewBindingHolder

        val itemData = recyclerItems[position]
        holder.binding.apply {
            // TODO send BR from outside this class
            setVariable(BR.article, itemData)
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