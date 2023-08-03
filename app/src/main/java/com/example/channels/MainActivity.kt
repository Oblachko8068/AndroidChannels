package com.example.channels

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.viewpager.widget.ViewPager
import com.example.channels.fragments.FourthFragment
import com.example.channels.fragments.FragmentAdapter
import com.example.channels.fragments.ThirdFragment
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ретрофит

        //Поиск
        val searchView = findViewById<SearchView>(R.id.searchView_tv_channels)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val thirdFragment = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpagerForTabs + ":" + 0) as? ThirdFragment
                val fourthFragment = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpagerForTabs + ":" + 1) as? FourthFragment

                thirdFragment?.searchQuery = newText
                fourthFragment?.searchQuery = newText

                thirdFragment?.filterChannels(newText)
                fourthFragment?.filterChannels(newText)

                return true
            }
        })

        //Вкладки
        val viewpagerForTabs = findViewById<ViewPager>(R.id.viewpagerForTabs)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        val fragmentAdapter = FragmentAdapter(supportFragmentManager)

        viewpagerForTabs.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewpagerForTabs)

        //

    }

}