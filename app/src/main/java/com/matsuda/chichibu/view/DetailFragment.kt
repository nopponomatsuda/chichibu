package com.matsuda.chichibu.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.matsuda.chichibu.actions.ActionsCreator
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.databinding.DetailFragmentBinding
import com.matsuda.chichibu.dispatchers.Dispatcher
import com.matsuda.chichibu.stores.DetailStore
import androidx.lifecycle.ViewModelProviders
import com.matsuda.chichibu.MainActivity
import com.matsuda.chichibu.R
import com.matsuda.chichibu.actions.MyPageActionCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {
    private val detailStore: DetailStore by lazy {
        ViewModelProviders.of(this).get(DetailStore::class.java)
    }

    private var binding: DetailFragmentBinding? = null

    companion object {
        fun newInstance(articleId: String): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(Constant.BUNDLE_KEY_ARTICLE_ID, articleId)
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
        MainActivity.aWSAppSyncClient?.run {
            ActionsCreator.showDetail(this, articleId)

            binding?.favoriteIcon?.setOnClickListener {
                MyPageActionCreator.addFavorite(this, articleId) {

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