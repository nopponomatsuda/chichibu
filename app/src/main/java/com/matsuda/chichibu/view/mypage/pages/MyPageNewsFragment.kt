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
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.data.ArticleCategory
import com.matsuda.chichibu.view.parts.MasonryAdapter
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.databinding.MypageArticleFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.mypage.MypageNewsStore
import com.matsuda.chichibu.view.navigator.ViewNavigator
import com.matsuda.chichibu.view.parts.CustomSpanSizeLookup

class MyPageNewsFragment : Fragment() {
    private var binding: MypageArticleFragmentBinding? = null
    private val listStore = MypageNewsStore()

    companion object {
        fun newInstance(areaId: String): MyPageNewsFragment {
            return MyPageNewsFragment().apply {
                arguments = Bundle().apply {
                    putString(Constant.BUNDLE_KEY_AREA_ID, areaId)
                }
            }
        }
    }

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

        binding?.run {
            viewModel = listStore
            lifecycleOwner = this@MyPageNewsFragment
        }

        MainActivity.aWSAppSyncClient?.run {
            MyPageActionCreator.fetchNews(this)
        }

        binding?.articleList?.run {
            layoutManager = GridLayoutManager(context, CustomSpanSizeLookup.SPAN_COUNT).apply {
                spanSizeLookup = CustomSpanSizeLookup.MyPage
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
                        ViewNavigator.moveToDetail(fragmentManager, data.id, ArticleCategory.NEWS)
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
