package com.matsuda.chichibu.view.navigator

import androidx.fragment.app.FragmentManager
import com.matsuda.chichibu.R
import com.matsuda.chichibu.data.ArticleCategory
import com.matsuda.chichibu.data.Prefecture
import com.matsuda.chichibu.view.DetailFragment
import com.matsuda.chichibu.view.account.ProfileFragment
import com.matsuda.chichibu.view.article.ViewPagerFragment
import com.matsuda.chichibu.view.mypage.MyPageViewPagerFragment
import com.matsuda.chichibu.view.owner.OwnerViewPagerFragment
import com.matsuda.chichibu.view.search.AreaFragment

object ViewNavigator {

    fun moveToOwnerHome(
        fragmentManager: FragmentManager,
        areaId: String = Prefecture.ALL.areaId.toString()
    ) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, OwnerViewPagerFragment.newInstance(areaId))
            .commit()
    }

    fun moveToProfilePage(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ProfileFragment.newInstance())
            .commit()
    }

    fun moveToAreaPage(
        fragmentManager: FragmentManager,
        areaId: String = Prefecture.ALL.areaId.toString()
    ) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AreaFragment.newInstance(areaId))
            .commit()
    }

    fun moveToHome(
        fragmentManager: FragmentManager,
        areaId: String = Prefecture.ALL.areaId.toString()
    ) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ViewPagerFragment.newInstance(areaId))
            .commit()
    }

    fun moveToMyPage(
        fragmentManager: FragmentManager,
        areaId: String = Prefecture.ALL.areaId.toString()
    ) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MyPageViewPagerFragment.newInstance(areaId))
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