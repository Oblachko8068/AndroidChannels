package com.example.channels.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.channels.ChannelPlayer
import com.example.channels.ViewModel.ChannelViewModel
import com.example.channels.ViewModel.ChannelViewModelFactory
import com.example.channels.R
import com.example.channels.databinding.FragmentAllBinding
import com.example.channels.model.repository.ChannelRepository
import com.example.channels.model.repository.DownloadRepository
import com.example.channels.model.repository.EpgRepository
import com.example.channels.model.retrofit.ChannelDb
import com.example.channels.model.retrofit.EpgDb
import com.example.channels.RecyclerAdapter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AllFragment : Fragment(), RecyclerAdapter.OnChannelItemClickListener {

    private var _binding: FragmentAllBinding? = null
    private val binding get() = _binding!!

    var searchQuery: String? = null
    lateinit var adapter: RecyclerAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var channelDb: List<ChannelDb>
    lateinit var epgDb: List<EpgDb>

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channelViewModel = ViewModelProvider(
            requireActivity(),
            ChannelViewModelFactory(
                DownloadRepository(requireContext()),
                ChannelRepository(requireContext()),
                EpgRepository(requireContext())
            )
        )[ChannelViewModel::class.java]
        val channelList = channelViewModel.getChannelListLiveData()
        val epgList = channelViewModel.getEpgListLiveData()

        epgDb = epgList.value ?: emptyList()
        channelDb = channelList.value ?: emptyList()
        channelList.observe(viewLifecycleOwner, Observer { channelList ->
            channelDb = channelList
            updateChannelsAndEpg()
        })

        epgList.observe(viewLifecycleOwner, Observer { epgList ->
            epgDb = epgList
            updateChannelsAndEpg()
        })

        binding.recyclerView3.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView3.layoutManager = layoutManager

        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewpagerForTabs)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    updateChannelsAndEpg()
                    //getAllChannelsList(channelList.value!!, epgDb)
                    if (!searchQuery.isNullOrEmpty()) {
                        filterChannels(searchQuery)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
    private fun updateChannelsAndEpg() {
        if (::channelDb.isInitialized && ::epgDb.isInitialized) {
            getAllChannelsList(channelDb, epgDb)
        }
    }

    private fun getAllChannelsList(channelDbList: List<ChannelDb>, epgDb: List<EpgDb>) {
        adapter = RecyclerAdapter(requireContext(), channelDbList, epgDb, this)
        val recyclerView: RecyclerView = binding.recyclerView3
        recyclerView.adapter = adapter
        if (!searchQuery.isNullOrEmpty()) {
            filterChannels(searchQuery)
        }
    }

    fun filterChannels(searchQuery: String?) {
        val filteredList: List<ChannelDb> = if (!searchQuery.isNullOrEmpty()) {
            channelDb.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channelDb
        }

        val recyclerView = binding.recyclerView3
        val adapter = recyclerView.adapter as? RecyclerAdapter
        adapter?.setData(filteredList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onChannelItemClicked(channel: ChannelDb) {
        val epgDbList = epgDb
        val selectedEpgDb  = epgDbList.find { it.channelID == channel.id }
        val bundle = Bundle()

        bundle.putSerializable("channel_data", channel)
        bundle.putSerializable("epg_data", selectedEpgDb )

        val fragment = VideoPlayerFragment()
        fragment.arguments = bundle
        //fragment.show(requireActivity().supportFragmentManager, "videoPlayerDialog")
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
        //val intent = Intent(requireContext(), ChannelPlayer::class.java)
        //intent.putExtras(bundle)
        //requireContext().startActivity(intent)
    }
}