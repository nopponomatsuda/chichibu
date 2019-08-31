package com.matsuda.chichibu

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.matsuda.chichibu.view.navigator.ViewNavigator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewNavigator.moveToHome(supportFragmentManager)

        val navView = nav_view as BottomNavigationView
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_dashboard ->
                    ViewNavigator.moveToMyPage(supportFragmentManager)
                R.id.navigation_create_article ->
                    ViewNavigator.moveToCreateArticle(supportFragmentManager)
                R.id.navigation_login -> {
                    startActivity(Intent(this, AuthenticatorActivity::class.java))
                }
                else ->
                    ViewNavigator.moveToHome(supportFragmentManager)
            }
            false
        }
    }
}
