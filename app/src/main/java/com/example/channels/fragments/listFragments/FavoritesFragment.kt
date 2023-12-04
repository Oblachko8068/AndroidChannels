package com.example.channels.fragments.listFragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.channels.RecyclerAdapter
import com.example.channels.databinding.FragmentFavoritesBinding
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.google.gson.Gson


class FavoritesFragment : BaseChannelFragment() {

    private var _binding: FragmentFavoritesBinding? = null
    override val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val rootView = binding.root //а смысл в этой перменной?
        recyclerView = binding.recyclerView4
        return rootView
    }

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onPageChanged(position: Int) {
        if (position == 1) {
            updateChannelsAndEpg()
            if (!searchQuery.isNullOrEmpty()) {
                filterChannels(searchQuery)
            }
        }
    }


    //вынести в ChannelViewModel
    override fun getAllChannelsList(channelList: List<Channel>, epg: List<Epg>) {
        val intArray = getSavedNewIntArray(requireContext())
        val favoriteChannels = channelList.filter { it.id in intArray }
        createAdapter(favoriteChannels, epg)
    }

    //вынести в ChannelViewModel
    override fun filterChannels(searchQuery: String?) {
        val filteredList = filterChannelsCommon(searchQuery)
        val intArray = getSavedNewIntArray(requireContext())
        val filteredChannels = filteredList.filter { it.id in intArray }
        val adapter = recyclerView?.adapter as? RecyclerAdapter
        adapter?.setData(filteredChannels)
    }

    //вынести в репозиторий, код дублируется. Если поменяете что-то в одном месте - во втором может быть краш, т.к. забыли поменять
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