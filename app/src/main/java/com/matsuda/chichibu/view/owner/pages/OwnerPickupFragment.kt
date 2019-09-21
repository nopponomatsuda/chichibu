package com.matsuda.chichibu.view.owner.pages

import android.content.Intent
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
import com.matsuda.chichibu.actions.OwnerActionCreator
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.view.parts.MasonryAdapter
import com.matsuda.chichibu.data.Article
import com.matsuda.chichibu.databinding.OwnerArticleFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.owner.OwnerPickupStore
import com.matsuda.chichibu.view.owner.CreateArticleActivity
import com.matsuda.chichibu.view.parts.CustomSpanSizeLookup

class OwnerPickupFragment : Fragment() {
    private var binding: OwnerArticleFragmentBinding? = null
    private val listStore = OwnerPickupStore()

    companion object {
        fun newInstance(areaId: String): OwnerPickupFragment {
            return OwnerPickupFragment().apply {
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
            inflater, R.layout.owner_article_fragment,
            container, false
        ) ?: return null

        binding?.run {
            viewModel = listStore
            lifecycleOwner = this@OwnerPickupFragment
        }
        listStore.loading.postValue(true)

        MainActivity.aWSAppSyncClient?.run {
            OwnerActionCreator.fetchPickups(this)
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
                        startActivity(
                            Intent(context, CreateArticleActivity::class.java)
                                .putExtra(CreateArticleActivity.EXTRA_KEY_ARTICLE_ID, data.id)
                        )
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
