package com.example.channels.fragments

import android.content.Context
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
import com.example.channels.databinding.FragmentFavoritesBinding
import com.example.channels.retrofit.ChannelJSON
import com.example.channels.retrofit.RecyclerAdapter
import com.example.channels.retrofit.toChannelDB
import com.example.channels.retrofit.toEpgDB
import com.google.gson.Gson

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavoritesFragment : Fragment(), RecyclerAdapter.OnChannelItemClickListener {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    var searchQuery: String? = null
    lateinit var adapter: RecyclerAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var channelJSONList2: List<ChannelJSON>

    private var param1: String? = null
    private var param2: String? = null

    override fun onChannelItemClicked(channel: ChannelJSON) {
        val channelDB = channel.toChannelDB()
        val epgDbList = channel.toEpgDB()
        val epgDb = epgDbList.find { it.channelID == channelDB.id }
        val bundle = Bundle()

        bundle.putSerializable("channel_data", channelDB)
        bundle.putSerializable("epg_data", epgDb)

        val intent = Intent(requireContext(), ChannelPlayer::class.java)
        intent.putExtras(bundle)
        requireContext().startActivity(intent)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channelViewModel =
            ViewModelProvider(requireActivity())[ChannelViewModel::class.java]
        channelViewModel.fetchChannels()
        val channelList = channelViewModel.getChannelListLiveData()
        channelList.observe(requireActivity(), Observer { channelList ->
            // Обработка изменений в списке каналов
            // channelList - список каналов, который был обновлен
            getAllChannelsList(channelList)
            channelJSONList2 = channelList
        })

        binding.recyclerView4.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView4.layoutManager = layoutManager

        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewpagerForTabs)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 1) {
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
        val intArray = getSavedNewIntArray(requireContext())
        val favoriteChannels = channelJSONList.filter { it.id in intArray }
        adapter = RecyclerAdapter(requireContext(), favoriteChannels, this)
        val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerView4)
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
        val intArray = getSavedNewIntArray(requireContext())
        val favoriteChannels = filteredList.filter { it.id in intArray }
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView4)
        val adapter = recyclerView.adapter as? RecyclerAdapter
        adapter?.setData(favoriteChannels)
    }

    private fun getSavedNewIntArray(context: Context): IntArray {
        val sharedPref = context.getSharedPreferences("new_array_preferences", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("new_int_array_data", null)

        return try {
            if (jsonString != null) {
                Gson().fromJson(jsonString, IntArray::class.java)
            } else {
                IntArray(0)
            }
        } catch (e: Exception) {
            IntArray(0)  // Возвращаем пустой (нулевой) массив в случае ошибки
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
}