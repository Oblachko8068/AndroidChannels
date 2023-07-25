package com.example.channels


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.channels.MyPagerAdapter
import com.example.channels.R
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewpager = findViewById<ViewPager>(R.id.viewpager)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)

        viewpager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewpager)
    }
}
