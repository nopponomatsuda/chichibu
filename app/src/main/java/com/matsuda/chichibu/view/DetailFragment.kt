package com.matsuda.chichibu.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.matsuda.chichibu.actions.ActionsCreator
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.databinding.DetailFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.DetailStore
import androidx.annotation.Nullable
import androidx.lifecycle.Observer
import com.matsuda.chichibu.BR
import com.matsuda.chichibu.R
import com.matsuda.chichibu.data.Article

class DetailFragment : Fragment() {
    private val detailStore = DetailStore()
    private var binding: DetailFragmentBinding? = null

    companion object {
        fun newInstance(articleId: Int): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constant.BUNDLE_KEY_DETAIL_ID, articleId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dispatcher.register(detailStore)
        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.detail_fragment,
            container, false
        ) ?: return null

        binding?.run {
            lifecycleOwner = this@DetailFragment
        }

        //TODO if budle value is null, show error or back to previous page
        val detailId = arguments?.getInt(Constant.BUNDLE_KEY_DETAIL_ID) ?: return null
        ActionsCreator.showDetail(detailId)

        binding?.root?.setOnTouchListener{ _, _ ->
            //prevent bottom layer view from receiving above layer view's event
            true
        }

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Dispatcher.unregister(detailStore)
    }

    private fun observeViewModel() {
        detailStore.article.observe(this, object : Observer<Article> {
            override fun onChanged(@Nullable article: Article?) {
                Log.d(javaClass.simpleName, "onChanged")
                if (article == null) return
                binding?.setVariable(BR.detail, article)
            }
        })
    }
}