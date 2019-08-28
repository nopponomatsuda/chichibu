package com.matsuda.chichibu.view.article

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.matsuda.chichibu.R
import com.matsuda.chichibu.view.article.pages.EventFragment
import com.matsuda.chichibu.view.article.pages.FoodFragment
import com.matsuda.chichibu.view.article.pages.NewsFragment
import com.matsuda.chichibu.view.article.pages.PickupFragment

class TabAdapter(private val context: Context, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                PickupFragment()
            }
            1 -> {
                FoodFragment()
            }
            2 -> {
                EventFragment()
            }
            else -> {
                NewsFragment()
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