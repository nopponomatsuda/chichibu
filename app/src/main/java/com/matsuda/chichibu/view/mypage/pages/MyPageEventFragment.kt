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
import com.matsuda.chichibu.R
import com.matsuda.chichibu.actions.ActionsCreator
import com.matsuda.chichibu.actions.MyPageActionCreator
import com.matsuda.chichibu.view.parts.MasonryAdapter
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.databinding.ArticleFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.EventStore
import com.matsuda.chichibu.view.mypage.MyPageViewPagerFragment
import com.matsuda.chichibu.view.navigator.ViewNavigator
import com.matsuda.chichibu.view.parts.CustomSpanSizeLookup

class MyPageEventFragment : Fragment() {
    private var binding: ArticleFragmentBinding? = null
    private val listStore = EventStore()

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
            inflater, R.layout.article_fragment,
            container, false
        ) ?: return null

        val parent = parentFragment as MyPageViewPagerFragment
        parent.aWSAppSyncClient?.run {
            MyPageActionCreator.fetchEvents(this)
        }

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
        Dispatcher.unregister(listStore)
    }
}
