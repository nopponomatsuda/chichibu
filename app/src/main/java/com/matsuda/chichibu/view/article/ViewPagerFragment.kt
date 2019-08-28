package com.matsuda.chichibu.view.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.matsuda.chichibu.R
import kotlinx.android.synthetic.main.view_pager_fragment.*

class ViewPagerFragment : Fragment() {
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
            val fragmentManager = fragmentManager ?: return
            viewPager.adapter = TabAdapter(context, fragmentManager)
            tabLayout.setupWithViewPager(viewPager)
        }
    }
}