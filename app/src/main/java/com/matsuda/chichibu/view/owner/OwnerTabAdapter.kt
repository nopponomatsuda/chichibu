package com.matsuda.chichibu.view.owner

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.matsuda.chichibu.R
import com.matsuda.chichibu.view.owner.pages.OwnerEventFragment
import com.matsuda.chichibu.view.owner.pages.OwnerFoodFragment
import com.matsuda.chichibu.view.owner.pages.OwnerNewsFragment
import com.matsuda.chichibu.view.owner.pages.OwnerPickupFragment

class OwnerTabAdapter(
    private val context: Context,
    fm: FragmentManager,
    private val areaId: String
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                OwnerPickupFragment.newInstance(areaId)
            }
            1 -> {
                OwnerFoodFragment.newInstance(areaId)
            }
            2 -> {
                OwnerEventFragment.newInstance(areaId)
            }
            else -> {
                OwnerNewsFragment.newInstance(areaId)
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                context.resources.getString(R.string.article_pickup_fragment_tab_name)
            }
            1 -> {
                context.resources.getString(R.string.article_food_fragment_tab_name)
            }
            2 -> {
                context.resources.getString(R.string.article_event_fragment_tab_name)
            }
            else -> {
                context.resources.getString(R.string.article_news_fragment_tab_name)
            }
        }
    }

    override fun getCount(): Int {
        return 4
    }
}