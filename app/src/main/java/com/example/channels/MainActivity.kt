package com.example.channels

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FavUtils.addToFavorites(0)
        FavUtils.addToFavorites(2)
        FavUtils.addToFavorites(4)

        //вкладки
        val viewpagerForTabs = findViewById<ViewPager>(R.id.viewpagerForTabs)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)

        viewpagerForTabs.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewpagerForTabs)

        //поиск
        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Вызывается, когда пользователь вводит текст в поисковое поле и нажимает "Поиск" на клавиатуре
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // Вызывается, когда пользователь вводит текст в поисковое поле
            override fun onQueryTextChange(newText: String?): Boolean {
                filterChannels(newText)
                return true
            }
        })
        //
    }

    //поиск
    private lateinit var channelList: List<Channels>
    private lateinit var adapter: CustomRecyclerAdapter

    private fun filterChannels(query: String?) {
        if (query.isNullOrBlank()) {
            // Если поисковый запрос пустой или null, показываем полный список каналов
            adapter.setData(channelList)
        } else {
            // Иначе фильтруем список каналов по запросу и обновляем адаптер
            val filteredList = channelList.filter {
                it.name.contains(query, ignoreCase = true)
            }
            adapter.setData(filteredList)
        }
    }
    //
}