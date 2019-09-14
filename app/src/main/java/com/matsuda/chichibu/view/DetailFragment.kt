package com.matsuda.chichibu.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.matsuda.chichibu.actions.ArticleActionCreator
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.databinding.DetailFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.DetailStore
import androidx.lifecycle.ViewModelProviders
import com.matsuda.chichibu.MainActivity
import com.matsuda.chichibu.R
import com.matsuda.chichibu.actions.MyPageActionCreator
import com.matsuda.chichibu.data.ArticleCategory

class DetailFragment : Fragment() {
    private val detailStore: DetailStore by lazy {
        ViewModelProviders.of(this).get(DetailStore::class.java)
    }

    private var binding: DetailFragmentBinding? = null

    companion object {
        fun newInstance(articleId: String, articleCategory: ArticleCategory): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(Constant.BUNDLE_KEY_ARTICLE_ID, articleId)
                    putString(Constant.BUNDLE_KEY_ARTICLE_CATEGORY, articleCategory.name)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dispatcher.register(detailStore)
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
            viewModel = detailStore
        }

        val articleId = arguments?.getString(Constant.BUNDLE_KEY_ARTICLE_ID) ?: return null
        val category = arguments?.getString(Constant.BUNDLE_KEY_ARTICLE_CATEGORY) ?: return null
        MainActivity.aWSAppSyncClient?.run {
            ArticleActionCreator.showDetail(this, articleId)

            binding?.favoriteIcon?.setOnClickListener {

                Toast.makeText(
                    context,
                    "favoriteIcon clicked", Toast.LENGTH_LONG
                ).show()

                MyPageActionCreator.addFavorite(
                    this, articleId,
                    ArticleCategory.valueOf(category)
                ) {

                    Handler(Looper.getMainLooper()).post {
                        val context = context ?: return@post
                        if (this) Toast.makeText(
                            context,
                            "Adding favorite is completed", Toast.LENGTH_LONG
                        ).show()
                        else Toast.makeText(
                            context,
                            "Adding favorite is failed", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        binding?.root?.setOnTouchListener { _, _ ->
            //prevent bottom layer view from receiving above layer view's event
            true
        }

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Dispatcher.unregister(detailStore)
    }
}