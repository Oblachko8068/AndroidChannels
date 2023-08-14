package com.example.channels.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.channels.ChannelPlayer
import com.example.channels.ChannelViewModel
import com.example.channels.R
import com.example.channels.databinding.FragmentAllBinding
import com.example.channels.retrofit.ChannelJSON
import com.example.channels.retrofit.RecyclerAdapter
import com.example.channels.retrofit.toChannelDB

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AllFragment : Fragment(), RecyclerAdapter.OnChannelItemClickListener {

    private var _binding: FragmentAllBinding? = null
    private val binding get() = _binding!!

    var searchQuery: String? = null
    lateinit var adapter: RecyclerAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var channelJSONList2: List<ChannelJSON>

    private var param1: String? = null
    private var param2: String? = null

    override fun onChannelItemClicked(channel: ChannelJSON) {
        val intent = Intent(requireContext(), ChannelPlayer::class.java)
        val channelDB = channel.toChannelDB()
        val bundle = Bundle()
        bundle.putSerializable("", channelDB)
        bundle.putString("channel_name", channel.name)
        bundle.putString("channel_description", channel.epg[0].title)
        bundle.putString("channel_icon_resource", channel.image)
        bundle.putString("channel_stream", channel.stream)
        bundle.putLong("channel_timestart", channel.epg[0].timestart)
        bundle.putLong("channel_timestop", channel.epg[0].timestop)
        intent.putExtras(bundle)
        requireContext().startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channelViewModel = ViewModelProvider(requireActivity())[ChannelViewModel::class.java]
        channelViewModel.fetchChannels()
        val channelList = channelViewModel.getChannelListLiveData()
        channelList.observe(requireActivity(), Observer { channelList ->
            // Обработка изменений в списке каналов
            // channelList - список каналов, который был обновлен
            getAllChannelsList(channelList)
            channelJSONList2 = channelList
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
                    getAllChannelsList(channelList.value!!)
                    if (!searchQuery.isNullOrEmpty()) {
                        filterChannels(searchQuery)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun getAllChannelsList(channelJSONList: List<ChannelJSON>) {
        adapter = RecyclerAdapter(requireContext(), channelJSONList, this)
        val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerView3)
        recyclerView.adapter = adapter
        if (!searchQuery.isNullOrEmpty()) {
            filterChannels(searchQuery)
        }
    }

    fun filterChannels(searchQuery: String?) {
        val filteredList: List<ChannelJSON> = if (!searchQuery.isNullOrEmpty()) {
            channelJSONList2.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channelJSONList2
        }

        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView3)
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
}