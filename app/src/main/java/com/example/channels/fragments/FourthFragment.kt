package com.example.channels.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.channels.R
import com.example.channels.list.Channels
import com.example.channels.list.CustomRecyclerAdapter
import com.example.channels.retrofit.Channel
import com.example.channels.retrofit.ChannellsApi
import com.example.channels.retrofit.ChannelsNew
import com.example.channels.retrofit.RecyclerAdapterNew
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FourthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FourthFragment : Fragment() {
    //////////////////////////////////////////////////////
    var searchQuery: String? = null
    lateinit var adapter: RecyclerAdapterNew
    lateinit var ChannelsApi: ChannellsApi
    lateinit var layoutManager: LinearLayoutManager
    private var channelList: List<Channel>? = null
    ///////////////////////////////////////////////////////
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    /////////////////////////////////////////////////////////////////////////
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.jsonserve.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        ChannelsApi = retrofit.create(ChannellsApi::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView4)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        getAllChannelsList()

        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewpagerForTabs)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    getAllChannelsList()
                }
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })

    }
    private fun getAllChannelsList() {
        ChannelsApi.getChannelList().enqueue(object : Callback<ChannelsNew> {
            override fun onFailure(call: Call<ChannelsNew>, t: Throwable) {

            }

            override fun onResponse(call: Call<ChannelsNew>, response: Response<ChannelsNew>) {
                channelList = response.body()?.channels ?: emptyList()
                val allChannels = response.body()?.channels ?: emptyList()
                val intArray = getSavedNewIntArray(requireContext())
                val favoriteChannels = allChannels.filter { it.id in intArray }
                adapter = RecyclerAdapterNew(requireContext(), favoriteChannels)
                val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerView4)
                recyclerView.adapter = adapter
                if(!searchQuery.isNullOrEmpty()) {
                    filterChannels(searchQuery)
                }
            }
        })
    }

    fun filterChannels(searchQuery: String?) {
        val filteredList: List<Channel> = if (!searchQuery.isNullOrEmpty()) {
            channelList?.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            } ?: emptyList()
        } else {
            channelList ?: emptyList()
        }
        val intArray = getSavedNewIntArray(requireContext())
        val favoriteChannels = filteredList.filter { it.id in intArray }
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView4)
        val adapter = recyclerView.adapter as? RecyclerAdapterNew
        adapter?.setData(favoriteChannels)
    }

    fun getSavedNewIntArray(context: Context): IntArray {
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
    /////////////////////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fourth, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FourthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                FourthFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}