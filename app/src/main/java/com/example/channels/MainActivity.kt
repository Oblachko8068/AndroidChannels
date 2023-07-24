package com.example.channels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter // Замените MyAdapter на ваш адаптер списка


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //val viewpager = findViewById<ViewPager>(R.id.viewpager)
        //val tabs = findViewById<TabLayout>(R.id.tabs)
        //val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        //viewpager.adapter = fragmentAdapter
        //tabs.setupWithViewPager(viewpager)

        //список

        val dataList = listOf("Item 1", "Item 2", "Item 3", "Item 4") // Ваши данные для списка

        //val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        //val adapter = MyAdapter(dataList)
        recyclerView.adapter = adapter

        //поиск

        searchView = findViewById<SearchView>(R.id.searchView)


        // Установите адаптер для списка
        recyclerView.adapter = adapter

        // Настройте обработчик запроса поиска
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Вызовите метод адаптера для фильтрации списка по новому тексту поиска
                adapter.filter(newText)
                return true
            }
        })


    }
}
