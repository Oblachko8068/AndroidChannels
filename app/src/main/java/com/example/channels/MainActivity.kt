package com.example.channels

import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Поиск


        //Вкладки
        val viewpagerForTabs = findViewById<ViewPager>(R.id.viewpagerForTabs)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        val fragmentAdapter = FragmentAdapter(supportFragmentManager)

        viewpagerForTabs.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewpagerForTabs)

        //
    }
}