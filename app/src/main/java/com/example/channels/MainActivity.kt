package com.example.channels

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.channels.ViewModel.ChannelViewModel
import com.example.channels.ViewModel.ChannelViewModelFactory
import com.example.channels.databinding.ActivityMainBinding
import com.example.channels.fragments.AllFragment
import com.example.channels.fragments.FavoritesFragment
import com.example.channels.fragments.FragmentAdapter
import com.example.channels.model.repository.EpgRepositoryRetrofit
import com.example.channels.model.room.AppDatabase
import com.example.channels.model.room.ChannelDao
import com.example.channels.model.room.EpgDao


class MainActivity : AppCompatActivity() {

    private lateinit var channelViewModel: ChannelViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Di.init(applicationContext)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ViewModel
        channelViewModel = ViewModelProvider(
            this,
            ChannelViewModelFactory(
                Di.downloadRepository,
                Di.channelRepository,
                Di.epgRepository,
            )
        )[ChannelViewModel::class.java]
        channelViewModel.fetchChannels()
        //Поиск
        binding.searchViewTvChannels.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val allFragment =
                    supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpagerForTabs + ":" + 0) as? AllFragment
                val favoritesFragment =
                    supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpagerForTabs + ":" + 1) as? FavoritesFragment

                allFragment?.searchQuery = newText
                favoritesFragment?.searchQuery = newText

                allFragment?.filterChannels(newText)
                favoritesFragment?.filterChannels(newText)

                return true
            }
        })

        //Вкладки
        val fragmentAdapter = FragmentAdapter(supportFragmentManager)
        binding.viewpagerForTabs.adapter = fragmentAdapter
        binding.tabs.setupWithViewPager(binding.viewpagerForTabs)
    }
}