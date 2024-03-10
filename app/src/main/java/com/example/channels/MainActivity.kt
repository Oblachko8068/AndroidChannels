package com.example.channels

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.example.channels.viewModel.AdsViewModel
import com.example.channels.ads.AdShownListener
import com.example.channels.databinding.ActivityMainBinding
import com.example.channels.fragments.ExoPlayerFragment
import com.example.channels.fragments.MainFragment
import com.example.channels.fragments.Navigator
import com.example.channels.fragments.VideoAdsFragment
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding
    private val adsViewModel: AdsViewModel by viewModels()

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /////Splash
        installSplashScreen()
        /////

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adsViewModel.initializeAdsManager(this)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MainFragment())
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
        if (instreamAd != null){
            launchFragment(VideoAdsFragment(instreamAd))
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

