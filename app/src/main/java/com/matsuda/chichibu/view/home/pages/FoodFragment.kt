package com.matsuda.chichibu.view.home.pages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.matsuda.chichibu.R
import com.matsuda.chichibu.actions.ActionsCreator
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.common.view.MasonryAdapter
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.databinding.FoodFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.FoodStore
import com.matsuda.chichibu.view.parts.CustomSpanSizeLookup
import com.matsuda.chichibu.view.navigator.ViewNavigator

class FoodFragment : Fragment() {
    companion object {
        private const val TAG = "PickupFragment"
    }

    private var foodStore = FoodStore()
    private var binding: FoodFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dispatcher.register(foodStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        binding = DataBindingUtil.inflate(
            inflater, R.layout.food_fragment,
            container, false
        ) ?: return null

        ActionsCreator.fetchFoods()

        binding?.foodList?.run {
            layoutManager = GridLayoutManager(context, CustomSpanSizeLookup.SPAN_COUNT).apply {
                spanSizeLookup = CustomSpanSizeLookup.Food
            }
            adapter = MasonryAdapter(context, foodStore.foodList).apply {
                listener = object : MasonryAdapter.OnItemClickListener {
                    override fun onClick(view: View, data: BaseObservable) {
                        data as Article
                        val articleId = bundleOf(Constant.BUNDLE_KEY_DETAIL_ID to data.id)
//                        Navigation.findNavController(this@run)
//                            .navigate(R.id.action_homeFragment_to_detailFragment, articleId)
                        val fragmentManager = fragmentManager ?: return
                        ViewNavigator.moveToDetail(fragmentManager, data.id)
                    }
                }
            }
        }
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Dispatcher.unregister(foodStore)
    }
}
