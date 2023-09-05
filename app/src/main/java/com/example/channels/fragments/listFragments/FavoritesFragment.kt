package com.example.channels.fragments.listFragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.channels.RecyclerAdapter
import com.example.channels.databinding.FragmentAllBinding
import com.example.channels.databinding.FragmentFavoritesBinding
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.retrofit.Epg
import com.google.gson.Gson


class FavoritesFragment : BaseChannelFragment() {

    private var _binding: FragmentFavoritesBinding? = null
    override val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val rootView = binding.root
        recyclerView =
            rootView.findViewById(recyclerViewId)
        return rootView
    }

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewBinding {
        return FragmentAllBinding.inflate(inflater, container, false)
    }

    override fun onPageChanged(position: Int) {
        if (position == 1) {
            updateChannelsAndEpg()
            if (!searchQuery.isNullOrEmpty()) {
                filterChannels(searchQuery)
            }
        }
    }

    override fun getAllChannelsList(channelList: List<Channel>, epg: List<Epg>) {
        val intArray = getSavedNewIntArray(requireContext())
        val favoriteChannels = channelList.filter { it.id in intArray }
        adapter = RecyclerAdapter(requireContext(), favoriteChannels, epg, this)
        recyclerView?.adapter = adapter
        if (!searchQuery.isNullOrEmpty()) {
            filterChannels(searchQuery)
        }
    }

    override fun filterChannels(searchQuery: String?) {
        val filteredList: List<Channel> = if (!searchQuery.isNullOrEmpty()) {
            channel.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channel
        }
        val intArray = getSavedNewIntArray(requireContext())
        val favoriteChannels = filteredList.filter { it.id in intArray }
        val adapter = recyclerView?.adapter as? RecyclerAdapter
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
}