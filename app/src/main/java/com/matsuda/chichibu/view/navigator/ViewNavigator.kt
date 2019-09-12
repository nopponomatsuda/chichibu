package com.matsuda.chichibu.view.navigator

import androidx.fragment.app.FragmentManager
import com.matsuda.chichibu.R
import com.matsuda.chichibu.common.ArticleCategory
import com.matsuda.chichibu.view.DetailFragment
import com.matsuda.chichibu.view.article.ViewPagerFragment
import com.matsuda.chichibu.view.mypage.MyPageViewPagerFragment

object ViewNavigator {

    fun moveToHome(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ViewPagerFragment())
            .commit()
    }

    fun moveToMyPage(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MyPageViewPagerFragment())
            .commit()
    }

    fun moveToDetail(
        fragmentManager: FragmentManager,
        articleId: String,
        category: ArticleCategory
    ) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null).add(
            R.id.fragment_container,
            DetailFragment.newInstance(articleId, category)
        ).setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        ).commit()
    }
}