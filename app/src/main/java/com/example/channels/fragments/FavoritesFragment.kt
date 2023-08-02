package com.example.channels.fragments

import com.example.channels.list.Channels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.channels.list.CustomRecyclerAdapter
import com.example.channels.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritesFragment : Fragment() {
    var searchQuery: String? = null
    private var originalChannelsList: List<Channels> = emptyList()
    private lateinit var adapter: CustomRecyclerAdapter
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    ///Спиок
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewFavorites)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val channelList = Channels.getCatList(requireContext()).filter { it.fav_selected }
        originalChannelsList = channelList
        adapter = CustomRecyclerAdapter(requireContext(), channelList)
        recyclerView.adapter = adapter

        // Обновление списка при изменении данных в первом фрагменте (AllFragment)
        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewpagerForTabs)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    val newChannelList = Channels.getCatList(requireContext()).filter { it.fav_selected }
                    filterChannelBySearch(searchQuery, newChannelList)
                }

            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
    private fun filterChannelBySearch(searchQuery: String?, newChannelList: List<Channels>){
        val filteredList = if (!searchQuery.isNullOrEmpty()) {
            newChannelList.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            newChannelList
        }
        adapter.setData(filteredList)
    }
    // ... (остальной код FavoritesFragment)

    fun filterChannels(searchQuery: String?) {
        val filteredList = if (!searchQuery.isNullOrEmpty()) {
            originalChannelsList.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            originalChannelsList
        }

        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerViewFavorites)
        val adapter = recyclerView.adapter as? CustomRecyclerAdapter
        adapter?.setData(filteredList)
    }
    ///
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}