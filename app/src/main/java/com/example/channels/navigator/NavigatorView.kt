package com.example.channels.navigator

import android.content.Context
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import com.example.channels.MainActivity
import com.example.channels.R
import com.example.channels.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates

class NavigatorView(
    mainActivity: MainActivity,
    binding: ActivityMainBinding,
    navigatorListener: NavigationView.OnNavigationItemSelectedListener
) {

    private var isDarkTheme by Delegates.notNull<Boolean>()

    init {
        mainActivity.setSupportActionBar(binding.mainToolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.navView.setNavigationItemSelectedListener(navigatorListener)
        val toggle = ActionBarDrawerToggle(
            mainActivity,
            binding.drawerLayout,
            binding.mainToolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val header = binding.navView.getHeaderView(0)
        val but: View = header.findViewById(R.id.day_night_theme)
        but.setOnClickListener {
            isDarkTheme = loadDarkThemeState(mainActivity)
            if (isDarkTheme) {
                saveDarkThemeState(mainActivity, !isDarkTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                saveDarkThemeState(mainActivity, !isDarkTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private fun saveDarkThemeState(context: Context, isDarkTheme: Boolean) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkTheme", isDarkTheme)
        editor.apply()
    }

    fun loadDarkThemeState(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isDarkTheme", false)
    }
}