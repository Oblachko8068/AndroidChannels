package com.example.channels

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.channels.ads.AdShownListener
import com.example.channels.databinding.ActivityMainBinding
import com.example.channels.exoPlayer.ExoPlayerFragment
import com.example.channels.fragments.ChannelFragment
import com.example.channels.fragments.Navigator
import com.example.channels.fragments.VideoAdsFragment
import com.example.channels.navigatorView.NavigatorView
import com.example.channels.radioPlayer.RadioPlayerFragment
import com.example.channels.viewModels.AdsViewModel
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding
    private val adsViewModel: AdsViewModel by viewModels()

    @SuppressLint("CommitTransaction", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigatorView()
        adsViewModel.initializeAdsManager(this)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, ChannelFragment())
                .commit()
        }
    }

    override fun showVideoPlayerFragment(channel: Channel, selectedEpgDb: Epg?) {
        val listener = object : AdShownListener {
            override fun onAdLoadedAndShown() {
                launchFragment(ExoPlayerFragment.newInstance(channel, selectedEpgDb))
            }
        }
        val instreamAd = adsViewModel.showInterOrInstreamAd(listener)
        if (instreamAd != null) {
            launchFragment(VideoAdsFragment(instreamAd))
        }
    }

    private fun initNavigatorView() {
        val navigatorListener = NavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_tv -> launchFragment(ChannelFragment())
                R.id.nav_radio -> launchFragment(RadioPlayerFragment())
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        val navigatorView = NavigatorView(this, binding, navigatorListener)
        val isDarkTheme = navigatorView.loadDarkThemeState(this)
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun goBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    @SuppressLint("CommitTransaction")
    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commitAllowingStateLoss()
    }
}