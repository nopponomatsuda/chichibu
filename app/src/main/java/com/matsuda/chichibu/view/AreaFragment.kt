package com.matsuda.chichibu.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.matsuda.chichibu.BR
import com.matsuda.chichibu.R
import com.matsuda.chichibu.actions.AreaActionCreator
import com.matsuda.chichibu.data.Area
import com.matsuda.chichibu.databinding.AreaFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.AreaStore
import com.matsuda.chichibu.view.navigator.ViewNavigator
import com.matsuda.chichibu.view.parts.CustomSpanSizeLookup
import com.matsuda.chichibu.view.parts.MasonryAdapter

class AreaFragment : Fragment() {
    private var binding: AreaFragmentBinding? = null
    private val listStore = AreaStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dispatcher.register(listStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.area_fragment,
            container, false
        ) ?: return null

        listStore.loading.postValue(true)
        AreaActionCreator.fetchAreas()

        binding?.run {
            viewModel = listStore
            lifecycleOwner = this@AreaFragment
        }

        binding?.articleList?.run {
            layoutManager = GridLayoutManager(
                context, CustomSpanSizeLookup.SPAN_COUNT
            ).apply {
                spanSizeLookup = CustomSpanSizeLookup.AreaPpage
            }
            adapter = MasonryAdapter(
                context,
                listStore.list,
                R.layout.area_list_item_view,
                BR.area
            ).apply {
                listener = object : MasonryAdapter.OnItemClickListener {
                    override fun onClick(view: View, data: BaseObservable) {
                        data as Area
                        fragmentManager?.run {
                            ViewNavigator.moveToHome(this)
                        }
                    }
                }
            }
        }
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Dispatcher.unregister(listStore)
    }
}