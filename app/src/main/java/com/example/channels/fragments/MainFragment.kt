package com.example.channels.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.channels.R
import com.example.channels.databinding.FragmentMainBinding
import com.example.channels.fragments.listFragments.AllFragment
import com.example.channels.fragments.listFragments.FavoritesFragment
import com.example.channels.fragments.listFragments.FragmentAdapter
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.AdSize
import com.yandex.mobile.ads.common.ImpressionData


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.CrashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        //Поиск
        binding.searchViewTvChannels.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val allFragment =
                    childFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpagerForTabs + ":" + 0) as? AllFragment
                val favoritesFragment =
                    childFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpagerForTabs + ":" + 1) as? FavoritesFragment

                allFragment?.searchQuery = newText
                favoritesFragment?.searchQuery = newText

                allFragment?.filterChannels(newText)
                favoritesFragment?.filterChannels(newText)

                return true
            }
        })

        //Вкладки
        val fragmentAdapter = FragmentAdapter(childFragmentManager)
        binding.viewpagerForTabs.adapter = fragmentAdapter
        binding.tabs.setupWithViewPager(binding.viewpagerForTabs)

        //Реклама
        bannerAd()
    }
    private fun bannerAd(){
        binding.bannerAdView.setAdUnitId("demo-banner-yandex")
        binding.bannerAdView.setAdSize(BannerAdSize.inlineSize(requireContext(), 600, 50))

        val adRequest = AdRequest.Builder().build()

        /*binding.bannerAdView.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                TODO("Not yet implemented")
            }

            override fun onAdClicked() {
                TODO("Not yet implemented")
            }

            override fun onLeftApplication() {
                TODO("Not yet implemented")
            }

            override fun onReturnedToApplication() {
                TODO("Not yet implemented")
            }

            override fun onImpression(p0: ImpressionData?) {
                TODO("Not yet implemented")
            }
        })*/
        binding.bannerAdView.loadAd(adRequest)
    }
}