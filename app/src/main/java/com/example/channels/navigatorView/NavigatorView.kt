package com.example.channels.navigatorView

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.channels.MainActivity
import com.example.channels.R
import com.example.channels.authorization.USER_VIEW_MODEL
import com.example.channels.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates

class NavigatorView(
    private val mainActivity: MainActivity,
    private val binding: ActivityMainBinding,
    navigatorListener: NavigationView.OnNavigationItemSelectedListener
) {

    private var isDarkTheme by Delegates.notNull<Boolean>()

    init {
        mainActivity.setSupportActionBar(binding.mainToolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.navView.setNavigationItemSelectedListener(navigatorListener)
        binding.drawerLayout.addDrawerListener(
            ActionBarDrawerToggle(
                mainActivity,
                binding.drawerLayout,
                binding.mainToolbar,
                R.string.open_nav,
                R.string.close_nav
            ).apply { syncState() }
        )
        isDarkTheme = loadDarkThemeState(mainActivity)
        setHeader()
    }

    private fun setHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val dayNightButton: ImageButton = headerView.findViewById(R.id.day_night_theme)
        dayNightButton.setImageResource(if (isDarkTheme) R.drawable.icon_dark_theme else R.drawable.icon_light_theme)
        dayNightButton.setOnClickListener {
            val newTheme = !isDarkTheme
            saveDarkThemeState(mainActivity, newTheme)
            AppCompatDelegate.setDefaultNightMode(if (newTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }
        setUserData(headerView)
    }

    private fun setUserData(headerView: View) {
        USER_VIEW_MODEL.getUserData().observe(mainActivity) {
            val user = it?.lastOrNull()
            val userName = headerView.findViewById<TextView>(R.id.profile_login)
            val userDescription = headerView.findViewById<TextView>(R.id.profile_phone)
            val userImageView = headerView.findViewById<ImageView>(R.id.profile_pic)
            userName.text = user?.displayName ?: "Вы"
            userDescription.text = user?.phone.takeUnless { phone -> phone.isNullOrEmpty() }
                ?: user?.email.takeUnless { email -> email.isNullOrEmpty() }
                        ?: ""
            val userImage = user?.image
            if (userImage != "" && userImage != null) {
                setUserImage(userImage, userImageView)
            } else {
                val imgResId = R.drawable.icon_user_picture
                val drawable = ContextCompat.getDrawable(mainActivity, imgResId)
                userImageView.setImageDrawable(drawable)
            }
            binding.navView.menu[4].setVisible(user == null)
        }
    }

    private fun setUserImage(image: String, userImageView: ImageView) {
        Glide.with(mainActivity)
            .load(image)
            .apply(RequestOptions.circleCropTransform())
            .into(userImageView)
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