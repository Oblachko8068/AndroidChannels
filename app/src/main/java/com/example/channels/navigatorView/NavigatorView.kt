package com.example.channels.navigatorView

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.example.channels.MainActivity
import com.example.channels.R
import com.example.channels.authorization.USER_VIEW_MODEL
import com.example.channels.databinding.ActivityMainBinding
import com.example.channels.viewModels.UserViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlin.properties.Delegates

class NavigatorView(
    private val mainActivity: MainActivity,
    private val binding: ActivityMainBinding,
    navigatorListener: NavigationView.OnNavigationItemSelectedListener
) {

    private var isDarkTheme by Delegates.notNull<Boolean>()
    private val userViewModel: UserViewModel by mainActivity.viewModels()

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
        isDarkTheme = loadDarkThemeState(mainActivity)
        setHeader()
    }

    private fun setHeader(){
        val headerView = binding.navView.getHeaderView(0)
        val dayNightButton: ImageButton = headerView.findViewById(R.id.day_night_theme)
        if (isDarkTheme) {
            dayNightButton.setImageResource(R.drawable.icon_dark_theme)
        } else {
            dayNightButton.setImageResource(R.drawable.icon_light_theme)
        }
        dayNightButton.setOnClickListener {
            if (isDarkTheme) {
                saveDarkThemeState(mainActivity, !isDarkTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                saveDarkThemeState(mainActivity, !isDarkTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        setUserData(headerView)
    }

    private fun setUserData(headerView: View) {
        val userLiveData = userViewModel.getUserData()
        userLiveData.observe(mainActivity){
            val user = it?.lastOrNull()
            val userName = headerView.findViewById<TextView>(R.id.profile_login)
            val userDescription = headerView.findViewById<TextView>(R.id.profile_phone)
            userName.text = user?.displayName ?: "Вы"
            userDescription.text = user?.phone ?: ""
            if (user != null){
                hideLoginButton()
            } else {
                binding.navView.menu[4].setVisible(true)
            }
        }
    }

    private fun hideLoginButton() {
        binding.navView.menu[4].setVisible(false)
    }

    private fun saveDarkThemeState(context: Context, isDarkTheme: Boolean) {
        val sharedPreferences = context.getSharedPreferences("ThemeState", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkTheme", isDarkTheme)
        editor.apply()
    }

    fun loadDarkThemeState(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("ThemeState", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isDarkTheme", false)
    }
}