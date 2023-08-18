package com.example.channels

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.channels.ViewModel.ChannelViewModel
import com.example.channels.ViewModel.ChannelViewModelFactory
import com.example.channels.databinding.ActivityMainBinding
import com.example.channels.fragments.AllFragment
import com.example.channels.fragments.FavoritesFragment
import com.example.channels.fragments.FragmentAdapter
import com.example.channels.fragments.VideoPlayerFragment
import com.example.channels.model.repository.ChannelRepository
import com.example.channels.model.repository.DownloadRepository
import com.example.channels.model.repository.EpgRepository
import com.example.channels.model.retrofit.ChannelDb


class MainActivity : AppCompatActivity() {

    private lateinit var channelViewModel: ChannelViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ViewModel
        channelViewModel = ViewModelProvider(
            this,
            ChannelViewModelFactory(
                DownloadRepository(this.applicationContext),
                ChannelRepository(this.applicationContext),
                EpgRepository(this.applicationContext)
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
        /*supportFragmentManager.beginTransaction()
            .replace(androidx.fragment.R.id.fragment_container_view_tag, VideoPlayerFragment())
            .addToBackStack(null)
            .commit()*/
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(android.R.id.content)

        // Если найден активный фрагмент и он поддерживает возврат назад
        if (fragment is VideoPlayerFragment && fragment.isAdded) {
            // Закрываем фрагмент
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }
}