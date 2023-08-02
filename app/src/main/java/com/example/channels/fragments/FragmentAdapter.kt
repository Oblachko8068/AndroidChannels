package com.example.channels.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class FragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> { AllFragment() }
            2 -> { ThirdFragment() }
            3 -> { FourthFragment() }
            else -> { return FavoritesFragment() }
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Все"
            2 -> "Черновик"
            3 -> "ИЗБРАННЫЙ Черновик"
            else -> {
                return "Избранные"
            }
        }
    }

}