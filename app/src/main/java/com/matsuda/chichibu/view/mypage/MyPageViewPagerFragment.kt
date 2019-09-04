package com.matsuda.chichibu.view.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.matsuda.chichibu.R
import kotlinx.android.synthetic.main.view_pager_fragment.*

class MyPageViewPagerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_pager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context = context ?: return
        if (savedInstanceState == null) {
            viewPager.run {
                adapter = MyPageTabAdapter(context, childFragmentManager)
                offscreenPageLimit = 2
            }
            tabLayout.setupWithViewPager(viewPager)
        }
    }
}