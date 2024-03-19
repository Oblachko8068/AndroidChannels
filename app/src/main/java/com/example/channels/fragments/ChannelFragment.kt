package com.example.channels.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.channels.databinding.FragmentChannelBinding
import com.example.channels.fragments.channelListFragments.FragmentAdapter
import com.example.channels.viewModels.AdsViewModel
import com.yandex.mobile.ads.banner.BannerAdView

class ChannelFragment : Fragment() {

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adsViewModel: AdsViewModel by activityViewModels()
        adsViewModel.initializeAdsManager(requireActivity().applicationContext)

        //Поиск
        /*binding.searchViewTvChannels.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                ChannelViewModel.setSearchText(newText.orEmpty())
                return true
            }
        })*/

        //Вкладки
        val fragmentAdapter = FragmentAdapter(childFragmentManager)
        binding.viewpagerForTabs.adapter = fragmentAdapter
        binding.tabs.setupWithViewPager(binding.viewpagerForTabs)

        //Реклама
        binding.bannerAdView.removeAllViewsInLayout()
        val banner: BannerAdView? = adsViewModel.showBannerAd()
        banner?.let {
            val parent = banner.parent as? ViewGroup
            parent?.removeView(banner)
            binding.bannerAdView.addView(banner)
        }
    }
}