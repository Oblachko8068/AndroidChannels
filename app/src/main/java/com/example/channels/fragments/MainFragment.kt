package com.example.channels.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.channels.R
import com.example.channels.databinding.FragmentMainBinding
import com.example.channels.fragments.listFragments.FragmentAdapter
import com.example.channels.fragments.radioPlayer.RadioPlayerFragment
import com.example.channels.fragments.radioPlayer.RadioPlayerService
import com.example.channels.viewModel.AdsViewModel
import com.example.channels.viewModel.ChannelViewModel
import com.yandex.mobile.ads.banner.BannerAdView

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
        val adsViewModel: AdsViewModel by activityViewModels()
        adsViewModel.initializeAdsManager(requireActivity().applicationContext)

        binding.crashButton.setOnClickListener {
            val serviceIntent = Intent(requireContext(), RadioPlayerService::class.java)
            requireContext().startService(serviceIntent)
            launchFragment(RadioPlayerFragment())
        }

        //Поиск
        binding.searchViewTvChannels.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                ChannelViewModel.setSearchText(newText.orEmpty())
                return true
            }
        })

        //Вкладки
        val fragmentAdapter = FragmentAdapter(childFragmentManager)
        binding.viewPagerForTabs.adapter = fragmentAdapter
        binding.tabs.setupWithViewPager(binding.viewPagerForTabs)

        //Реклама
        binding.bannerAdView.removeAllViewsInLayout()
        val banner: BannerAdView? = adsViewModel.showBannerAd()
        banner?.let {
            val parent = banner.parent as? ViewGroup
            parent?.removeView(banner)
            binding.bannerAdView.addView(banner)
        }
    }

    private fun launchFragment(fragment: Fragment){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commitAllowingStateLoss()
    }
}