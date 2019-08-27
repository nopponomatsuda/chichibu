package com.matsuda.chichibu.view.home

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.matsuda.chichibu.view.home.pages.FoodFragment
import com.matsuda.chichibu.view.home.pages.PickupFragment

class TabAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        Log.d("DEBUG", "getItem")
        return when (position) {
            0 -> {
                PickupFragment()
            }
            else -> {
                FoodFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Pickup"
            }
            else -> {
                "food"
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }
}