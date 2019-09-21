package com.matsuda.chichibu.view.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.matsuda.chichibu.R
import com.matsuda.chichibu.common.Constant
import com.matsuda.chichibu.view.mypage.MyPageViewPagerFragment
import kotlinx.android.synthetic.main.view_pager_fragment.*

class ViewPagerFragment : Fragment() {

    companion object {
        fun newInstance(areaId: String): ViewPagerFragment {
            return ViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putString(Constant.BUNDLE_KEY_AREA_ID, areaId)
                }
            }
        }
    }

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
        val areaId = arguments?.getString(Constant.BUNDLE_KEY_AREA_ID) ?: return

        if (savedInstanceState == null) {
            viewPager.run {
                adapter = TabAdapter(context, childFragmentManager, areaId)
                offscreenPageLimit = 2
            }
            tabLayout.setupWithViewPager(viewPager)
        }
    }
}