package com.matsuda.chichibu.view.navigator

import androidx.fragment.app.FragmentManager
import com.matsuda.chichibu.R
import com.matsuda.chichibu.view.DetailFragment

object ViewNavigator {
    fun moveToDetail(fragmentManager: FragmentManager, articleId: Int) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null).add(
            R.id.fragment_container,
            DetailFragment.newInstance(articleId)
        ).setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        ).commit()
    }
}