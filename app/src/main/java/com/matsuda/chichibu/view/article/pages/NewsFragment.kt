package com.matsuda.chichibu.view.article.pages

import android.os.Bundle
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
import com.matsuda.chichibu.actions.ArticleActionCreator
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.data.ArticleCategory
import com.matsuda.chichibu.view.parts.MasonryAdapter
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.databinding.ArticleFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.NewsStore
import com.matsuda.chichibu.view.navigator.ViewNavigator
import com.matsuda.chichibu.view.parts.CustomSpanSizeLookup

class NewsFragment : Fragment() {
    private var binding: ArticleFragmentBinding? = null
    private val listStore = NewsStore()

    companion object {
        fun newInstance(areaId: String): NewsFragment {
            return NewsFragment().apply {
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
        binding = DataBindingUtil.inflate(
            inflater, R.layout.article_fragment,
            container, false
        ) ?: return null

        binding?.run {
            viewModel = listStore
            lifecycleOwner = this@NewsFragment
        }

        MainActivity.aWSAppSyncClient?.run {
            ArticleActionCreator.fetchNews(this)
        }

        binding?.articleList?.run {
            layoutManager = GridLayoutManager(context, CustomSpanSizeLookup.SPAN_COUNT).apply {
                spanSizeLookup = CustomSpanSizeLookup.News
            }
            adapter = MasonryAdapter(
                context,
                listStore.list,
                R.layout.list_item_view,
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
