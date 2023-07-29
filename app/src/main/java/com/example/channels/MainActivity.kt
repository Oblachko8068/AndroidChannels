package com.example.channels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Вкладки
        val viewpagerForTabs = findViewById<ViewPager>(R.id.viewpagerForTabs)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        val fragmentAdapter = FragmentAdapter(supportFragmentManager)

        viewpagerForTabs.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewpagerForTabs)

        //
    }


}