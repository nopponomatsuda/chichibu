package com.matsuda.chichibu.view

import androidx.databinding.BaseObservable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.matsuda.chichibu.R
import com.matsuda.chichibu.common.view.MasonryAdapter
import com.matsuda.chichibu.actions.ActionsCreator
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.databinding.PickupFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.HomeStore

class PickupFragment : Fragment() {
    private var homeStore = HomeStore()
    private var binding: PickupFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dispatcher.register(homeStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.pickup_fragment,
            container, false
        ) ?: return null

        ActionsCreator.fetchPickups()

        binding?.pickUpList?.run {
            layoutManager = GridLayoutManager(context, 6).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        //TODO define each position's span size other or this class
                        return when (position % 6) {
                            0 -> 6
                            1, 2, 3, 4 -> 3
                            else -> 3
                        }
                    }
                }
            }
            adapter = MasonryAdapter(context, homeStore.pickUpList).apply {
                listener = object : MasonryAdapter.OnItemClickListener {
                    override fun onClick(view: View, data: BaseObservable) {
                        data as Article
                        val articleId = bundleOf(Constant.BUNDLE_KEY_DETAIL_ID to data.id)
//                        Navigation.findNavController(this@run)
//                            .navigate(R.id.action_homeFragment_to_detailFragment, articleId)

                        val fragmentTransaction = fragmentManager?.beginTransaction() ?: return
                        fragmentTransaction.addToBackStack(null).add(
                            R.id.fragment_container,
                            DetailFragment.newInstance(data.id)
                        ).setCustomAnimations(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        ).commit()
                    }
                }

            }
        }
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Dispatcher.unregister(homeStore)
    }
}
