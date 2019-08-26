package com.matsuda.chichibu.view

import android.os.Bundle
import android.util.Log
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
        Log.d("DEBUG", "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        val fragmentManager = fragmentManager ?: return
        viewPager.adapter = TabAdapter(fragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }
}