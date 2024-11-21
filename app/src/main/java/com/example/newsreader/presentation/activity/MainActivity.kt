package com.example.newsreader.presentation.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.newsreader.R
import com.example.newsreader.presentation.ui.newslist.NewsListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Activity", "created")
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        Log.d("navHostFragment", "$navHostFragment")

        val navController = navHostFragment.navController

        Log.d("navController", "$navController")

        // Настраиваем работу BottomNavigationView только для Home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (navController.currentDestination?.id != R.id.newsListFragment) {
//                        if(navController.currentDestination?.id == R.id.newsDetailFragment) {
//                            navController.popBackStack()
//                        }
//                        else {
//                            navController.navigate(R.id.newsListFragment)
//                        }

                        navController.navigate(R.id.newsListFragment)
                    }
                    Log.d("Activity", "Navigated to News List")
                    true
                }
                R.id.nav_bookmarks -> {
                    if (navController.currentDestination?.id != R.id.bookmarksFragment) {
//                        if(navController.currentDestination?.id == R.id.newsDetailFragment) {
//                            navController.popBackStack()
//                        }
//                        else {
//                            navController.navigate(R.id.bookmarksFragment)
//                        }

                        navController.navigate(R.id.bookmarksFragment)
                    }
                    Log.d("Activity", "Navigated to Bookmarks")
                    true
                }
                else -> false
            }
        }
    }
}