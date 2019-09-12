package com.matsuda.chichibu.view.mypage.pages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.matsuda.chichibu.BR
import com.matsuda.chichibu.MainActivity
import com.matsuda.chichibu.R
import com.matsuda.chichibu.actions.MyPageActionCreator
import com.matsuda.chichibu.common.ArticleCategory
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.view.parts.MasonryAdapter
import com.matsuda.chichibu.databinding.MypageArticleFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.mypage.MypageFoodStore
import com.matsuda.chichibu.view.navigator.ViewNavigator
import com.matsuda.chichibu.view.parts.CustomSpanSizeLookup

class MyPageFoodFragment : Fragment() {
    private var binding: MypageArticleFragmentBinding? = null
    private val listStore = MypageFoodStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dispatcher.register(listStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("BaseFragment", "onCreateView")
        binding = DataBindingUtil.inflate(
            inflater, R.layout.mypage_article_fragment,
            container, false
        ) ?: return null

        MainActivity.aWSAppSyncClient?.run {
            MyPageActionCreator.fetchFoods(this)
        }

        binding?.run {
            viewModel = listStore
            lifecycleOwner = this@MyPageFoodFragment
        }
        listStore.loading.postValue(true)

        binding?.articleList?.run {
            layoutManager = GridLayoutManager(context, CustomSpanSizeLookup.SPAN_COUNT).apply {
                spanSizeLookup = CustomSpanSizeLookup.Mypage
            }
            adapter = MasonryAdapter(
                context,
                listStore.list,
                R.layout.mypage_list_item_view,
                BR.article
            ).apply {
                listener = object : MasonryAdapter.OnItemClickListener {
                    override fun onClick(view: View, data: BaseObservable) {
                        data as Article
                        val fragmentManager = parentFragment?.fragmentManager ?: return
                        ViewNavigator.moveToDetail(fragmentManager, data.id, ArticleCategory.FOOD)
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
