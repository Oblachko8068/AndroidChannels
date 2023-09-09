package com.example.channels

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.channels.databinding.ActivityMainBinding
import com.example.channels.fragments.MainFragment
import com.example.channels.fragments.Navigator
import com.example.channels.fragments.VideoPlayerFragment
import com.example.domain.model.Channel
import com.example.domain.model.Epg

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        com.example.di.di.Di.init(applicationContext, this)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MainFragment())
                .commit()
        }

    }

    override fun showVideoPlayerFragment(channel: Channel, selectedEpgDb: Epg?) {
        launchFragment(VideoPlayerFragment.newInstance(channel, selectedEpgDb))
    }

    override fun goBack() {
        onBackPressed()
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}