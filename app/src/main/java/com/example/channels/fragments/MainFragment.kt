package com.example.channels.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.channels.R
import com.example.channels.ViewModel.AdsViewModel
import com.example.channels.databinding.FragmentMainBinding
import com.example.channels.fragments.listFragments.AllFragment
import com.example.channels.fragments.listFragments.FavoritesFragment
import com.example.channels.fragments.listFragments.FragmentAdapter
import com.yandex.mobile.ads.banner.BannerAdView


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    //private val adsViewModel = AdsViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //adsViewModel.initializeAdsManager(requireActivity().applicationContext)
        val viewModel: AdsViewModel by activityViewModels()
        viewModel.initializeAdsManager(requireActivity().applicationContext)

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
        binding.bannerAdView.removeAllViews()
        binding.bannerAdView.removeAllViewsInLayout()
        val banner: BannerAdView? = viewModel.getAdsManager().showBannerAd()
        if (banner != null) {
            binding.bannerAdView.addView(banner)
        }
    }
}